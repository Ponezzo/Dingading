package com.mickey.dinggading.domain.attempt.service;

import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.TimeWrapper;
import com.mickey.dinggading.domain.membermatching.converter.RankMatchingConverter;
import com.mickey.dinggading.domain.membermatching.repository.AttemptRepository;
import com.mickey.dinggading.domain.membermatching.repository.RankMatchingRepository;
import com.mickey.dinggading.domain.membermatching.service.RankMatchingService;
import com.mickey.dinggading.domain.memberrank.model.Attempt;
import com.mickey.dinggading.domain.memberrank.model.GameType;
import com.mickey.dinggading.domain.memberrank.model.Instrument;
import com.mickey.dinggading.domain.memberrank.model.RankMatching;
import com.mickey.dinggading.domain.memberrank.model.RankType;
import com.mickey.dinggading.domain.memberrank.model.SongByInstrument;
import com.mickey.dinggading.domain.song.repository.SongByInstrumentRepository;
import com.mickey.dinggading.exception.ExceptionHandler;
import com.mickey.dinggading.model.AttemptDTO;
import com.mickey.dinggading.model.CreateAttemptRequestDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 시도 기록 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttemptService {

    private final SongByInstrumentRepository songByInstrumentRepository;
    private final AttemptRepository attemptRepository;
    private final RankMatchingConverter rankMatchingConverter;
    private final RankMatchingService rankMatchingService;
    private final RankMatchingRepository rankMatchingRepository;
    private final TimeWrapper timeWrapper;

    // 전략 맵
    private final Map<GameType, AttemptCreationStrategy> strategies = new HashMap<>() {{
        put(GameType.RANK, new RankAttemptCreationStrategy());
        put(GameType.PRACTICE, new PracticeAttemptCreationStrategy());
    }};

    /**
     * 연주 시도 기록을 생성하고 분석을 요청합니다.
     *
     * @param requestDTO 시도 생성 요청 DTO
     * @param memberId   회원 ID
     * @return 생성된 시도 ID
     */
    @Transactional
    public AttemptDTO createAttempt(CreateAttemptRequestDTO requestDTO, UUID memberId) {
        Long songByInstrumentId = requestDTO.getSongByInstrumentId();
        Long rankMatchingId = requestDTO.getRankMatchingId();
        GameType gameType = GameType.valueOf(requestDTO.getGameType().name());
        // requestDTO.getRankType();
        // requestDTO.getSongByInstrumentId();

        log.info("시도 기록 생성 요청: {}, 회원 ID: {}", requestDTO, memberId);

        // 1. SongByInstrument 조회
        SongByInstrument songByInstrument = songByInstrumentRepository.findById(songByInstrumentId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.SONG_BY_INSTRUMENT_NOT_FOUND));

        // 랭크 매칭 조회 (랭크 모드일 경우에만 사용됨)
        RankMatching rankMatching = null;
        if (gameType == GameType.RANK && rankMatchingId != null) {
            rankMatching = rankMatchingRepository.findById(rankMatchingId).get();
        }

        // 전략 패턴을 사용하여 게임 타입에 맞는 전략 선택 및 시도 생성
        AttemptCreationStrategy strategy = strategies.get(gameType);
        Attempt attempt = strategy.createAttempt(songByInstrument, rankMatching, memberId);

        Attempt save = attemptRepository.save(attempt);
        log.info("시도 기록 생성 완료: 시도 ID: {}", save.getAttemptId());

        return rankMatchingConverter.toDTO(save);
    }

    /**
     * 특정 시도 기록의 상세 정보를 조회
     *
     * @param attemptId 시도 ID
     * @return 시도 DTO
     */
    public AttemptDTO getAttempt(Long attemptId) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.ATTEMPT_NOT_FOUND));
        return rankMatchingConverter.toDTO(attempt);
    }

    /**
     * 연습 모드 시도 기록을 페이지네이션하여 조회
     *
     * @param memberId      회원 ID
     * @param instrumentStr 악기 (선택)
     * @param pageable      페이징 정보
     * @return 시도 DTO 페이지
     */
    public Page<AttemptDTO> getPracticeAttempts(UUID memberId, String instrumentStr,
                                                Pageable pageable) {
        if (instrumentStr != null) {
            Instrument instrument = Instrument.valueOf(instrumentStr);
            return attemptRepository.findByMemberIdAndGameTypeAndInstrument(memberId, GameType.PRACTICE,
                            instrument, pageable)
                    .map(rankMatchingConverter::toDTO);
        } else {
            return attemptRepository.findByMemberIdAndGameType(memberId, GameType.PRACTICE, pageable)
                    .map(rankMatchingConverter::toDTO);
        }
    }

    /**
     * 랭크 모드 시도 기록을 페이지네이션하여 조회
     *
     * @param memberId      회원 ID
     * @param instrumentStr 악기 (선택)
     * @param rankTypeStr   랭크 유형 (선택)
     * @param pageable      페이징 정보
     * @return 시도 DTO 페이지
     */
    public Page<AttemptDTO> getRankAttempts(UUID memberId, String instrumentStr, String rankTypeStr,
                                            Pageable pageable) {
        if (instrumentStr != null && rankTypeStr != null) {
            Instrument instrument = Instrument.valueOf(instrumentStr);
            RankType rankType = RankType.valueOf(rankTypeStr);
            return attemptRepository.findByMemberIdAndGameTypeAndInstrumentAndRankType(
                            memberId, GameType.RANK, instrument, rankType, pageable)
                    .map(rankMatchingConverter::toDTO);
        } else if (instrumentStr != null) {
            Instrument instrument = Instrument.valueOf(instrumentStr);
            return attemptRepository.findByMemberIdAndGameTypeAndInstrument(
                            memberId, GameType.RANK, instrument, pageable)
                    .map(rankMatchingConverter::toDTO);
        } else {
            return attemptRepository.findByMemberIdAndGameType(memberId, GameType.RANK, pageable)
                    .map(rankMatchingConverter::toDTO);
        }
    }

    /**
     * 분석 결과를 기반으로 시도 기록 업데이트 (음성 분석 API에서 호출)
     *
     * @param attemptId 시도 ID
     * @param beatScore 박자 점수
     * @param tuneScore 음정 점수
     * @param toneScore 톤 점수
     */
    @Transactional
    public void updateAttemptScore(Long attemptId, Integer beatScore, Integer tuneScore,
                                   Integer toneScore) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.ATTEMPT_NOT_FOUND));

        // 점수 설정 및 상태 업데이트
        attempt.updateScore(beatScore, tuneScore, toneScore, timeWrapper.now());

    }

}