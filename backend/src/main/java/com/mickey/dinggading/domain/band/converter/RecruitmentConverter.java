package com.mickey.dinggading.domain.band.converter;

import com.mickey.dinggading.domain.band.model.entity.Band;
import com.mickey.dinggading.domain.band.model.entity.BandRecruitment;
import com.mickey.dinggading.domain.band.model.entity.BandRecruitmentApplicant;
import com.mickey.dinggading.domain.band.model.entity.BandRecruitmentInstrument;
import com.mickey.dinggading.domain.memberrank.model.Song;
import com.mickey.dinggading.model.ApplicantDTO;
import com.mickey.dinggading.model.CreateBandRecruitmentRequest;
import com.mickey.dinggading.model.RecruitmentDTO;
import com.mickey.dinggading.model.RecruitmentInstrumentDTO;
import com.mickey.dinggading.model.RecruitmentStatus;
import com.mickey.dinggading.model.Tier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecruitmentConverter {

    /**
     * CreateBandRecruitmentRequest를 BandRecruitment 엔티티로 변환
     *
     * @param request      구인 공고 생성 요청 객체
     * @param band         밴드 엔티티
     * @param auditionSong 오디션 곡 엔티티
     * @return BandRecruitment 엔티티
     */
    public BandRecruitment toBandRecruitment(CreateBandRecruitmentRequest request, Band band, Song auditionSong) {
        return BandRecruitment.builder()
                .band(band)
                .title(request.getTitle())
                .description(request.getDescription())
                .auditionDate(request.getAuditionDate())
                .auditionSong(auditionSong)
                .status(RecruitmentStatus.RECRUITING) // 초기 상태는 항상 RECRUITING
                .build();
    }

    /**
     * BandRecruitment 엔티티를 RecruitmentDTO로 변환
     *
     * @param bandRecruitment 밴드 구인 공고 엔티티
     * @return RecruitmentDTO 객체
     */
    public RecruitmentDTO toBandRecruitmentDTO(BandRecruitment bandRecruitment) {
        return RecruitmentDTO.builder()
                .bandRecruitmentId(bandRecruitment.getBandRecruitmentId())
                .bandId(bandRecruitment.getBand().getBandId())
                .title(bandRecruitment.getTitle())
                .description(bandRecruitment.getDescription())
                .auditionDate(bandRecruitment.getAuditionDate())
                .status(bandRecruitment.getStatus())
                .auditionSongId(bandRecruitment.getAuditionSong().getSongId())
                .auditionSongTitle(bandRecruitment.getAuditionSong().getTitle())
                .createdAt(bandRecruitment.getCreatedAt())
                .updatedAt(bandRecruitment.getUpdatedAt())
                .build();
    }

    public RecruitmentInstrumentDTO toRecruitmentInstrumentDTO(BandRecruitmentInstrument instrument) {
        return RecruitmentInstrumentDTO.builder()
                .bandRecruitmentInstrumentsId(instrument.getBandRecruitmentInstrumentsId())
                .bandRecruitmentId(instrument.getBandRecruitment().getBandRecruitmentId())
                .instrument(instrument.getInstrument())
                .requiredTier(instrument.getRequiredTier())
                .maxSize(instrument.getMaxSize())
                .currentApplicants(instrument.getCurrentApplicantsCount())
                .build();
    }

    public ApplicantDTO toApplicantDTO(BandRecruitmentApplicant applicant, Tier tier) {
        return ApplicantDTO.builder()
                .bandRecruitmentApplicantId(applicant.getBandRecruitmentApplicantId())
                .bandRecruitmentInstrumentsId(
                        applicant.getBandRecruitmentInstrument().getBandRecruitmentInstrumentsId())
                .applicantId(applicant.getApplicant().getMemberId())
                .nickname(applicant.getApplicant().getNickname())
                .profileImgUrl(applicant.getApplicant().getProfileImgUrl())
                .instrument(applicant.getBandRecruitmentInstrument().getInstrument())
                .tier(tier)
                .applyDate(applicant.getApplyDate())
                .build();
    }

}