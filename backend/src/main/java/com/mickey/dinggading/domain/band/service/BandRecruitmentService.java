package com.mickey.dinggading.domain.band.service;

import com.mickey.dinggading.model.AddRecruitmentInstrumentRequest;
import com.mickey.dinggading.model.ApplicantDTO;
import com.mickey.dinggading.model.ApplyRecruitmentRequest;
import com.mickey.dinggading.model.CreateBandRecruitmentRequest;
import com.mickey.dinggading.model.RecruitmentDTO;
import com.mickey.dinggading.model.RecruitmentInstrumentDTO;
import com.mickey.dinggading.model.RecruitmentStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BandRecruitmentService {

    /**
     * 밴드 구인 공고 등록
     *
     * @param bandId   밴드 ID
     * @param request  구인 공고 생성 요청 객체
     * @param memberId 요청자 ID (밴드 마스터여야 함)
     * @return 생성된 구인 공고 DTO
     */
    RecruitmentDTO createBandRecruitment(Long bandId, CreateBandRecruitmentRequest request, UUID memberId);

    RecruitmentDTO updateBandRecruitment(Long bandId, Long recruitmentId,
                                         CreateBandRecruitmentRequest createBandRecruitmentRequest, UUID memberId);

    Page<RecruitmentDTO> getAllRecruitments(Pageable pageable);

    RecruitmentDTO getBandRecruitment(Long bandId);

    List<RecruitmentInstrumentDTO> getRecruitmentInstruments(Long bandId, Long recruitmentId);

    RecruitmentDTO updateRecruitmentStatus(Long bandId, Long recruitmentId, RecruitmentStatus status, UUID memberId);

    void deleteBandRecruitment(Long bandId, Long recruitmentId, UUID memberId);

    void acceptApplicant(Long bandId, Long recruitmentId, UUID applicantId, UUID memberId);

    RecruitmentInstrumentDTO addRecruitmentInstrument(Long bandId, Long recruitmentId,
                                                      AddRecruitmentInstrumentRequest addRecruitmentInstrumentRequest,
                                                      UUID memberId);

    ApplicantDTO applyRecruitment(Long recruitmentId, ApplyRecruitmentRequest applyRecruitmentRequest, UUID memberId);

    void cancelRecruitmentApplication(Long recruitmentId, UUID applicantId, UUID memberId);

    List<ApplicantDTO> getRecruitmentApplicants(Long bandId, Long recruitmentId, UUID memberId);

    void rejectApplicant(Long bandId, Long recruitmentId, UUID applicantId, UUID memberId);

    void removeRecruitmentInstrument(Long bandId, Long recruitmentId, Long instrumentId, UUID memberId);
}