package com.mickey.dinggading.domain.membermatching.service;

import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.TimeWrapper;
import com.mickey.dinggading.domain.membermatching.converter.RankMatchingConverter;
import com.mickey.dinggading.domain.membermatching.repository.AttemptRepository;
import com.mickey.dinggading.domain.membermatching.repository.RankMatchingRepository;
import com.mickey.dinggading.domain.memberrank.model.Instrument;
import com.mickey.dinggading.domain.memberrank.model.MemberRank;
import com.mickey.dinggading.domain.memberrank.model.RankMatching;
import com.mickey.dinggading.domain.memberrank.model.RankType;
import com.mickey.dinggading.domain.memberrank.model.SongInstrumentPack;
import com.mickey.dinggading.domain.memberrank.model.Tier;
import com.mickey.dinggading.domain.memberrank.repository.MemberRankRepository;
import com.mickey.dinggading.domain.song.repository.SongInstrumentPackRepository;
import com.mickey.dinggading.exception.ExceptionHandler;
import com.mickey.dinggading.model.CreateRankMatchingRequestDTO;
import com.mickey.dinggading.model.DefencePeriodDTO;
import com.mickey.dinggading.model.RankMatchingDTO;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 랭크 매칭 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankMatchingService {

    private final RankMatchingRepository rankMatchingRepository;
    private final MemberRankRepository memberRankRepository;
    private final AttemptRepository attemptRepository;
    private final SongInstrumentPackRepository songInstrumentPackRepository;
    private final RankMatchingConverter rankMatchingConverter;
    private final TimeWrapper timeWrapper;

    /**
     * 모든 랭크 매칭 목록을 페이지네이션하여 조회
     *
     * @param pageable 페이징 정보
     * @return 랭크 매칭 DTO 페이지
     */
    public Page<RankMatchingDTO> getRankMatchings(Pageable pageable) {
        return rankMatchingRepository.findAll(pageable)
                .map(rankMatchingConverter::toDTO);
    }

    /**
     * 특정 랭크 매칭의 상세 정보를 조회
     *
     * @param rankMatchingId 랭크 매칭 ID
     * @return 랭크 매칭 DTO
     */
    public RankMatchingDTO getRankMatching(Long rankMatchingId) {
        // 중복 제거: findById 로직을 별도 메소드로 추출
        RankMatching rankMatching = findRankMatchingById(rankMatchingId);
        return rankMatchingConverter.toDTOWithDetails(rankMatching);
    }

    /**
     * 랭크 매칭 ID로 랭크 매칭 엔티티를 조회
     *
     * @param rankMatchingId 랭크 매칭 ID
     * @return 랭크 매칭 엔티티
     * @throws ExceptionHandler 랭크 매칭을 찾을 수 없는 경우
     */
    private RankMatching findRankMatchingById(Long rankMatchingId) {
        return rankMatchingRepository.findById(rankMatchingId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.RANK_MATCHING_NOT_FOUND));
    }

    /**
     * 티어별 도전 가능한 랭크 매칭 정보를 조회합니다. 각 티어마다 배치고사, 도전, 방어 가능 여부를 확인하여 반환합니다. 현재 진행 중인 랭크 매칭이 있으면 해당 정보도 포함합니다.
     *
     * @param memberId   회원 ID
     * @param instrument 악기 종류
     * @return 티어별 도전 가능한 랭크 매칭 정보 맵
     */
    public Map<String, Object> getAvailableRankMatchingsByTier(UUID memberId, Instrument instrument) {
        Map<String, Object> result = new HashMap<>();

        // 회원 랭크 조회
        MemberRank memberRank = findMemberRankByMemberIdAndInstrument(memberId, instrument);

        // 현재 티어
        Tier currentTier = memberRank.getTier();
        result.put("currentTier", currentTier.name());

        // 진행 중인 랭크 매칭 정보 추가
        List<RankMatching> ongoingMatchings = rankMatchingRepository.findOngoingMatchingsByMemberIdAndInstrument(
                memberId, instrument);

        if (!ongoingMatchings.isEmpty()) {
            RankMatching ongoing = ongoingMatchings.get(0); // 첫 번째 진행 중인 매칭 가져오기
            Map<String, Object> ongoingInfo = new HashMap<>();

            // 도전이면 목표 티어, 방어면 현재 티어
            ongoingInfo.put("tier", ongoing.getTargetTier().name());
            ongoingInfo.put("type", ongoing.getRankType().name());
            ongoingInfo.put("startedAt", ongoing.getStartedAt().toString());
            ongoingInfo.put("expireDate", ongoing.getExpireDate().toString());
            ongoingInfo.put("attemptCount", ongoing.getAttemptCount());
            ongoingInfo.put("successCount", ongoing.getSuccessCount());

            result.put("ongoingMatching", ongoingInfo);
        }

        // 진행 중인 매칭이 있으면 모든 티어에 대해 도전/방어 불가능으로 설정
        boolean hasOngoingMatching = !ongoingMatchings.isEmpty();

        // 각 티어별 가능한 랭크 매칭 정보 추가 (UNRANKED 제외)
        for (Tier tier : Tier.values()) {
            if (tier.equals(Tier.UNRANKED)) {
                continue;
            }

            result.put(tier.name(), availableRankMatchingInfo(tier, hasOngoingMatching, memberRank));
        }

        return result;
    }

    private Map<String, Boolean> availableRankMatchingInfo(Tier tier, boolean hasOngoingMatching,
                                                           MemberRank memberRank) {
        Map<String, Boolean> tierAvailability = new HashMap<>();

        if (hasOngoingMatching) {
            // 진행 중인 매칭이 있으면 모두 불가능
            tierAvailability.put("availableFirst", false);
            tierAvailability.put("availableChallenge", false);
            tierAvailability.put("availableDefence", false);
        } else {
            boolean availableFirst = memberRank.canFirst(tier);
            boolean availableChallenge = memberRank.canChallenge(tier);
            boolean availableDefence = memberRank.isInDefencePeriod(timeWrapper.now());

            tierAvailability.put("availableFirst", availableFirst);
            tierAvailability.put("availableChallenge", availableChallenge);
            tierAvailability.put("availableDefence", availableDefence);
        }

        return tierAvailability;
    }

    /**
     * 새로운 랭크 매칭을 생성
     *
     * @param memberId   회원 ID
     * @param requestDTO 요청 DTO
     * @return 생성된 랭크 매칭 DTO
     */
    @Transactional
    public RankMatchingDTO createRankMatching(UUID memberId, CreateRankMatchingRequestDTO requestDTO) {
        // 요청 파라미터 변환
        Instrument instrument = Instrument.valueOf(requestDTO.getInstrument().name());
        RankType rankType = RankType.valueOf(requestDTO.getRankType().name());
        Tier targetTier = Tier.valueOf(requestDTO.getTargetTier().name());

        // 중복 제거: 엔티티 조회 로직을 별도 메소드로 추출
        SongInstrumentPack songPack = findSongPackById(requestDTO.getSongInstrumentPackId());
        MemberRank memberRank = findMemberRankByMemberIdAndInstrument(memberId, instrument);

        // 중복 제거: 진행 중인 매칭 확인 로직을 별도 메소드로 추출
        checkNoOngoingMatching(memberId, instrument);

        // 랭크 매칭 생성 및 저장
        RankMatching rankMatching = RankMatching.createRankMatching(
                memberRank, songPack, instrument, rankType, targetTier, timeWrapper.now());

        RankMatching save = rankMatchingRepository.save(rankMatching);

        return rankMatchingConverter.toDTO(save);
    }

    /**
     * 곡 팩 ID로 곡 팩 엔티티를 조회
     *
     * @param songPackId 곡 팩 ID
     * @return 곡 팩 엔티티
     * @throws ExceptionHandler 곡 팩을 찾을 수 없는 경우
     */
    private SongInstrumentPack findSongPackById(Long songPackId) {
        return songInstrumentPackRepository.findById(songPackId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.SONG_PACK_NOT_FOUND));
    }

    /**
     * 회원 ID와 악기로 회원 랭크 엔티티를 조회
     *
     * @param memberId   회원 ID
     * @param instrument 악기
     * @return 회원 랭크 엔티티
     * @throws ExceptionHandler 회원 랭크를 찾을 수 없는 경우
     */
    private MemberRank findMemberRankByMemberIdAndInstrument(UUID memberId, Instrument instrument) {
        return memberRankRepository.findByMemberMemberIdAndInstrument(memberId, instrument)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_RANK_NOT_FOUND));
    }

    /**
     * 진행 중인 매칭이 없는지 확인
     *
     * @param memberId   회원 ID
     * @param instrument 악기
     * @throws ExceptionHandler 진행 중인 매칭이 있는 경우
     */
    private void checkNoOngoingMatching(UUID memberId, Instrument instrument) {
        if (hasOngoingMatching(memberId, instrument)) {
            throw new ExceptionHandler(ErrorStatus.ONGOING_MATCHING_EXISTS);
        }
    }

    /**
     * 진행 중인 매칭이 있는지 확인
     *
     * @param memberId   회원 ID
     * @param instrument 악기
     * @return 진행 중인 매칭 여부
     */
    private boolean hasOngoingMatching(UUID memberId, Instrument instrument) {
        List<RankMatching> ongoingMatchings = rankMatchingRepository.findOngoingMatchingsByMemberIdAndInstrument(
                memberId, instrument);
        return !ongoingMatchings.isEmpty();
    }

    /**
     * 회원의 랭크 매칭 이력을 페이지네이션하여 조회
     *
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 랭크 매칭 DTO 페이지
     */
    public Page<RankMatchingDTO> getRankMatchingHistory(UUID memberId, Pageable pageable) {
        return rankMatchingRepository.findByMemberId(memberId, pageable)
                .map(rankMatchingConverter::toDTO);
    }

    /**
     * 회원의 티어 방어 기간 정보를 조회
     *
     * @param memberId   회원 ID
     * @param instrument 악기
     * @return 방어 기간 정보 DTO
     */
    public DefencePeriodDTO getDefencePeriod(UUID memberId, Instrument instrument) {
        // 중복 제거: 회원 랭크 조회 로직을 별도 메소드로 추출
        MemberRank memberRank = findMemberRankByMemberIdAndInstrument(memberId, instrument);
        return rankMatchingConverter.toDefencePeriodDTO(memberRank);
    }

    /**
     * 만료된 랭크 매칭을 처리 (스케줄러에서 호출)
     */
    @Transactional
    public void processExpiredMatchings() {
        LocalDate today = timeWrapper.now().toLocalDate();

        List<RankMatching> expiredMatchings = rankMatchingRepository.findExpiredMatchings(today);

        for (RankMatching matching : expiredMatchings) {
            matching.expire();
        }
    }

}