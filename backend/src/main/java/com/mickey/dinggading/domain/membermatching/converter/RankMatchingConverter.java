package com.mickey.dinggading.domain.membermatching.converter;

import com.mickey.dinggading.domain.TimeWrapper;
import com.mickey.dinggading.domain.memberrank.model.Attempt;
import com.mickey.dinggading.domain.memberrank.model.MemberRank;
import com.mickey.dinggading.domain.memberrank.model.MemberTierLog;
import com.mickey.dinggading.domain.memberrank.model.RankMatching;
import com.mickey.dinggading.domain.memberrank.model.SongByInstrument;
import com.mickey.dinggading.domain.memberrank.model.SongInstrumentPack;
import com.mickey.dinggading.model.AttemptDTO;
import com.mickey.dinggading.model.AttemptDTO.GameTypeEnum;
import com.mickey.dinggading.model.AttemptDTO.RankTypeEnum;
import com.mickey.dinggading.model.DefencePeriodDTO;
import com.mickey.dinggading.model.MemberRankDTO;
import com.mickey.dinggading.model.MemberRankDTO.LastAttemptTierEnum;
import com.mickey.dinggading.model.MemberTierLogDTO;
import com.mickey.dinggading.model.MemberTierLogDTO.AfterTierEnum;
import com.mickey.dinggading.model.MemberTierLogDTO.BeforeTierEnum;
import com.mickey.dinggading.model.RankMatchingDTO;
import com.mickey.dinggading.model.RankMatchingDTO.InstrumentEnum;
import com.mickey.dinggading.model.RankMatchingDTO.StatusEnum;
import com.mickey.dinggading.model.RankMatchingDTO.TargetTierEnum;
import com.mickey.dinggading.model.SongByInstrumentDTO;
import com.mickey.dinggading.model.SongByInstrumentDTO.TierEnum;
import com.mickey.dinggading.model.SongInstrumentPackDTO;
import com.mickey.dinggading.model.SongInstrumentPackDTO.SongPackInstrumentEnum;
import com.mickey.dinggading.model.SongInstrumentPackDTO.SongPackTierEnum;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 랭크 매칭 관련 Entity와 DTO 간 변환을 담당하는 컨버터 클래스
 */
@Component
@RequiredArgsConstructor
public class RankMatchingConverter {

    private final TimeWrapper timeWrapper;

    // RankMatching Entity를 DTO로 변환
    public RankMatchingDTO toDTO(RankMatching rankMatching) {
        return RankMatchingDTO.builder()
                .rankMatchingId(rankMatching.getRankMatchingId())
                .status(StatusEnum.valueOf(rankMatching.getStatus().name()))
                .songInstrumentPackId(rankMatching.getSongInstrumentPack().getSongInstrumentPackId())
                .expireDate(rankMatching.getExpireDate())
                .startedAt(rankMatching.getStartedAt())
                .attemptCount(rankMatching.getAttemptCount())
                .successCount(rankMatching.getSuccessCount())
                .instrument(InstrumentEnum.valueOf(rankMatching.getInstrument().name()))
                .rankType(RankMatchingDTO.RankTypeEnum.valueOf(rankMatching.getRankType().name()))
                .targetTier(TargetTierEnum.valueOf(rankMatching.getTargetTier().name()))
                .build();
    }

    // RankMatching Entity를 DTO로 변환 (상세 정보 포함)
    public RankMatchingDTO toDTOWithDetails(RankMatching rankMatching) {
        RankMatchingDTO dto = toDTO(rankMatching);

        // SongInstrumentPack 정보 추가
        if (rankMatching.getSongInstrumentPack() != null) {
            dto.setSongInstrumentPack(toDTO(rankMatching.getSongInstrumentPack()));
        }

        // Attempt 목록 추가
        if (rankMatching.getAttempts() != null && !rankMatching.getAttempts().isEmpty()) {
            for (Attempt attempt : rankMatching.getAttempts()) {

            }
            List<AttemptDTO> attemptDTOs = rankMatching.getAttempts().stream()
                    .map(attempt -> toDTO(attempt))
                    .collect(Collectors.toList());
            dto.setAttempts(attemptDTOs);
        }

        return dto;
    }

    // Attempt Entity를 DTO로 변환
    public AttemptDTO toDTO(Attempt attempt) {
        AttemptDTO dto = AttemptDTO.builder()
                .attemptId(attempt.getAttemptId())
                .rankMatchingId(attempt.getRankMatching().getRankMatchingId())
                .songByInstrumentId(attempt.getSongByInstrument().getSongByInstrumentId())
                .tuneScore(attempt.getTuneScore())
                .toneScore(attempt.getToneScore())
                .beatScore(attempt.getBeatScore())
                .totalScore(attempt.getTotalScore())
                .status(AttemptDTO.StatusEnum.valueOf(attempt.getStatus().name()))
                .gameType(GameTypeEnum.valueOf(attempt.getGameType().name()))
                .rankType(attempt.getRankType() != null ? RankTypeEnum.valueOf(attempt.getRankType().name()) : null)
                .build();

        // SongByInstrument 정보 추가
        if (attempt.getSongByInstrument() != null) {
            dto.setSongByInstrument(toDTO(attempt.getSongByInstrument()));
        }

        return dto;
    }

    // SongInstrumentPack Entity를 DTO로 변환
    public SongInstrumentPackDTO toDTO(SongInstrumentPack songInstrumentPack) {
        SongInstrumentPackDTO dto = SongInstrumentPackDTO.builder()
                .songInstrumentPackId(songInstrumentPack.getSongInstrumentPackId())
                .packName(songInstrumentPack.getPackName())
                .songPackTier(SongPackTierEnum.valueOf(songInstrumentPack.getSongPackTier().name()))
                .songPackInstrument(SongPackInstrumentEnum.valueOf(songInstrumentPack.getSongPackInstrument().name()))
                .build();

        // SongByInstrument 목록 추가 (상세 조회 시)
        if (songInstrumentPack.getSongByInstruments() != null && !songInstrumentPack.getSongByInstruments().isEmpty()) {
            List<SongByInstrumentDTO> songDTOs = songInstrumentPack.getSongByInstruments().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            dto.setSongs(songDTOs);
        }

        return dto;
    }

    // SongByInstrument Entity를 DTO로 변환
    public SongByInstrumentDTO toDTO(SongByInstrument songByInstrument) {
        return SongByInstrumentDTO.builder()
                .songByInstrumentId(songByInstrument.getSongByInstrumentId())
                .songId(songByInstrument.getSongByInstrumentId())
                .songInstrumentPackId(songByInstrument.getSongInstrumentPack().getSongInstrumentPackId())
                .songByInstrumentExFilename(songByInstrument.getSongByInstrumentExFilename())
                .songByInstrumentAnalysisJson(songByInstrument.getSongByInstrumentAnalysisJson())
                .songByInstrumentFilename(songByInstrument.getSongByInstrumentFilename())
                .instrument(SongByInstrumentDTO.InstrumentEnum.valueOf(songByInstrument.getInstrument().name()))
                .tier(TierEnum.valueOf(songByInstrument.getTier().name()))
                .build();
    }

    // MemberRank Entity를 DTO로 변환
    public MemberRankDTO toDTO(MemberRank memberRank) {
        LocalDate defenceExpireDate =
                memberRank.getDefenceExpireDate() != null ? memberRank.getDefenceExpireDate().toLocalDate() : null;
        LocalDate lastAttemptDate =
                memberRank.getLastAttemptDate() != null ? memberRank.getLastAttemptDate().toLocalDate() : null;

        return MemberRankDTO.builder()
                .memberRankId(memberRank.getMemberRankId())
                .memberId(memberRank.getMember().getMemberId())
                .instrument(MemberRankDTO.InstrumentEnum.valueOf(memberRank.getInstrument().name()))
                .tier(MemberRankDTO.TierEnum.valueOf(memberRank.getTier().name()))
                .beatScore(memberRank.getBeatScore())
                .tuneScore(memberRank.getTuneScore())
                .toneScore(memberRank.getToneScore())
                .rankSuccessCount(memberRank.getRankSuccessCount())
                .lastAttemptTier(
                        memberRank.getLastAttemptTier() != null ? LastAttemptTierEnum.valueOf(
                                memberRank.getLastAttemptTier().name()) : null)
                // TODO: 나중에 LocalDate로 변경하는 로직 빼주기
                .defenceExpireDate(defenceExpireDate)
                .lastAttemptDate(lastAttemptDate)
                .build();
    }

    // MemberTierLog Entity를 DTO로 변환
    public MemberTierLogDTO toDTO(MemberTierLog tierLog) {
        return MemberTierLogDTO.builder()
                .tierLogId(tierLog.getTierLogId())
                .memberRankId(tierLog.getMemberRank().getMemberRankId())
                .beforeTier(BeforeTierEnum.valueOf(tierLog.getBeforeTier().name()))
                .afterTier(AfterTierEnum.valueOf(tierLog.getAfterTier().name()))
                .changedDate(tierLog.getChangedDate())
                .build();
    }

    // 방어 기간 정보를 DTO로 변환
    public DefencePeriodDTO toDefencePeriodDTO(MemberRank memberRank) {
        boolean isInDefencePeriod = memberRank.isInDefencePeriod(timeWrapper.now());
        int daysLeft = 0;

        if (isInDefencePeriod && memberRank.getDefenceExpireDate() != null) {
            daysLeft = (int) ChronoUnit.DAYS.between(timeWrapper.now(), memberRank.getDefenceExpireDate());
            if (daysLeft < 0) {
                daysLeft = 0;
            }
        }

        return DefencePeriodDTO.builder()
                .memberId(memberRank.getMember().getMemberId())
                .instrument(DefencePeriodDTO.InstrumentEnum.valueOf(memberRank.getInstrument().name()))
                .tier(DefencePeriodDTO.TierEnum.valueOf(memberRank.getTier().name()))
                // TODO: 나중에 LocalDate로 변경하는 로직 빼주기
                .defenceExpireDate(memberRank.getDefenceExpireDate().toLocalDate())
                .isInDefencePeriod(isInDefencePeriod)
                .daysLeft(daysLeft)
                .build();
    }
}