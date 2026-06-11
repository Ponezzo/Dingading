package com.mickey.dinggading.domain.memberrank.converter;

import com.mickey.dinggading.domain.memberrank.model.MemberRank;
import com.mickey.dinggading.model.MemberRankDTO;
import com.mickey.dinggading.model.MemberRankDTO.InstrumentEnum;
import com.mickey.dinggading.model.MemberRankDTO.LastAttemptTierEnum;
import com.mickey.dinggading.model.MemberRankDTO.TierEnum;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberRankConverter {

    /**
     * MemberRank 엔티티를 MemberRankDTO로 변환
     *
     * @param memberRank 변환할 MemberRank 엔티티
     * @return 변환된 MemberRankDTO
     */
    public MemberRankDTO toDTO(MemberRank memberRank) {
        if (memberRank == null) {
            return null;
        }

        return MemberRankDTO.builder()
                .memberRankId(memberRank.getMemberRankId())
                .memberId(memberRank.getMember().getMemberId())
//                .rankMatchingId(memberRank.getRankMatching() != null ?
//                        memberRank.getRankMatching().getRankMatchingId() : null)
                .instrument(InstrumentEnum.valueOf(memberRank.getInstrument().name()))
                .tier(TierEnum.valueOf(memberRank.getTier().name()))
                .beatScore(memberRank.getBeatScore())
                .tuneScore(memberRank.getTuneScore())
                .toneScore(memberRank.getToneScore())
                .rankSuccessCount(memberRank.getRankSuccessCount())
                .lastAttemptTier(memberRank.getLastAttemptTier() != null ?
                        LastAttemptTierEnum.valueOf(memberRank.getLastAttemptTier().name()) : null)

                // TODO: 나중에 바꿔
                .defenceExpireDate(memberRank.getDefenceExpireDate().toLocalDate())
                .lastAttemptDate(memberRank.getLastAttemptDate().toLocalDate())
                .build();
    }

    /**
     * MemberRank 엔티티 리스트를 MemberRankDTO 리스트로 변환
     *
     * @param memberRanks 변환할 MemberRank 엔티티 리스트
     * @return 변환된 MemberRankDTO 리스트
     */
    public List<MemberRankDTO> toDTOList(List<MemberRank> memberRanks) {
        if (memberRanks == null) {
            return Collections.emptyList();
        }

        return memberRanks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}