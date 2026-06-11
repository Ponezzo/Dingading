package com.mickey.dinggading.domain.band.controller;

import com.mickey.dinggading.api.RecruitmentApi;
import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.band.service.BandRecruitmentService;
import com.mickey.dinggading.exception.ExceptionHandler;
import com.mickey.dinggading.model.AddRecruitmentInstrumentRequest;
import com.mickey.dinggading.model.ApplicantDTO;
import com.mickey.dinggading.model.ApplyRecruitmentRequest;
import com.mickey.dinggading.model.CreateBandRecruitmentRequest;
import com.mickey.dinggading.model.RecruitmentDTO;
import com.mickey.dinggading.model.RecruitmentInstrumentDTO;
import com.mickey.dinggading.model.RecruitmentStatus;
import com.mickey.dinggading.util.SecurityUtil;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BandRecruitmentController implements RecruitmentApi {

    private final SecurityUtil securityUtil;
    private final BandRecruitmentService recruitmentService;

    /**
     * POST /bands/{bandId}/recruitments : 밴드 구인 공고 등록 밴드에 새로운 구인 공고를 등록합니다. 제목, 설명, 오디션 날짜, 오디션 곡 등 포함. 밴드는 동시간대에 하나의
     * 구인 공고만 가질 수 있습니다.
     *
     * @param bandId                       밴드 ID (required)
     * @param createBandRecruitmentRequest (required)
     * @return 구인 공고 정보 (status code 201) or 잘못된 요청입니다. (status code 400) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를
     * 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> createBandRecruitment(Long bandId,
                                                   CreateBandRecruitmentRequest createBandRecruitmentRequest) {
        log.info("Creating band recruitment for bandId: {}", bandId);

        UUID memberId = securityUtil.getCurrentMemberId();
        RecruitmentDTO recruitmentDTO = recruitmentService.createBandRecruitment(bandId, createBandRecruitmentRequest,
                memberId);

        log.info("Successfully created band recruitment: {}", recruitmentDTO.getBandRecruitmentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(recruitmentDTO);
    }

    /**
     * GET /bands/recruitments : 전체 구인 공고 목록 조회 전체 구인 공고 목록을 조회합니다. 페이징 처리된 결과를 반환합니다.
     *
     * @param pageable
     * @return 구인 공고 목록 (status code 200)
     */
    @Override
    public ResponseEntity<?> getAllRecruitments(Pageable pageable) {
        log.info("Getting all recruitments with page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<RecruitmentDTO> recruitmentPage = recruitmentService.getAllRecruitments(pageable);

        log.info("Retrieved {} recruitments, total elements: {}", recruitmentPage.getContent().size(),
                recruitmentPage.getTotalElements());
        return ResponseEntity.ok(recruitmentPage);
    }

    /**
     * PATCH /bands/{bandId}/recruitments/{recruitmentId} : 밴드 구인 공고 수정 특정 구인 공고 정보를 수정합니다.
     *
     * @param bandId                       밴드 ID (required)
     * @param recruitmentId                구인 공고 ID (required)
     * @param createBandRecruitmentRequest (required)
     * @return 구인 공고 정보 (status code 200) or 잘못된 요청입니다. (status code 400) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를
     * 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> updateBandRecruitment(Long bandId, Long recruitmentId,
                                                   CreateBandRecruitmentRequest createBandRecruitmentRequest) {
        log.info("Updating band recruitment. BandId: {}, RecruitmentId: {}", bandId, recruitmentId);

        UUID memberId = securityUtil.getCurrentMemberId();
        RecruitmentDTO recruitmentDTO = recruitmentService.updateBandRecruitment(bandId, recruitmentId,
                createBandRecruitmentRequest, memberId);

        log.info("Successfully updated band recruitment: {}", recruitmentId);
        return ResponseEntity.ok(recruitmentDTO);
    }

    /**
     * GET /bands/{bandId}/recruitments : 밴드 구인 공고 조회 밴드의 구인 공고를 조회합니다. 밴드는 동시간대에 하나의 구인 공고만 가질 수 있습니다.
     *
     * @param bandId 밴드 ID (required)
     * @return 구인 공고 정보 (status code 200) or 요청한 리소스를 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> getBandRecruitment(Long bandId) {
        log.info("Getting recruitment for band ID: {}", bandId);

        RecruitmentDTO recruitmentDTO = recruitmentService.getBandRecruitment(bandId);

        log.info("Found recruitment for band ID: {}, recruitment ID: {}", bandId,
                recruitmentDTO.getBandRecruitmentId());
        return ResponseEntity.ok(recruitmentDTO);
    }

    /**
     * GET /bands/{bandId}/recruitments/{recruitmentId}/instruments : 구인 공고 모집 악기 목록 조회 구인 공고의 모집 악기 포지션 정보를 조회합니다. 각
     * 포지션별 요구 티어, 모집 인원 포함
     *
     * @param bandId        밴드 ID (required)
     * @param recruitmentId 구인 공고 ID (required)
     * @return 모집 악기 목록 (status code 200) or 요청한 리소스를 찾을 수 없습니다. (status code 404)
     */

    @Override
    public ResponseEntity<?> getRecruitmentInstruments(Long bandId, Long recruitmentId) {
        log.info("Getting instruments for band ID: {} and recruitment ID: {}", bandId, recruitmentId);

        List<RecruitmentInstrumentDTO> instruments = recruitmentService.getRecruitmentInstruments(bandId,
                recruitmentId);

        log.info("Found {} instruments for recruitment ID: {}", instruments.size(), recruitmentId);
        return ResponseEntity.ok(instruments);
    }

    /**
     * POST /bands/{bandId}/recruitments/{recruitmentId}/{recruitmentStatus} : 밴드 구인 공고 상태 변경 밴드 구인 프로세스 상태를 변경합니다 (모집
     * 중, 모집 완료)
     *
     * @param bandId            밴드 ID (required)
     * @param recruitmentId     구인 공고 ID (required)
     * @param recruitmentStatus 구인 공고 상태 (required)
     * @return 구인 공고 정보 (status code 200) or 잘못된 요청입니다. (status code 400) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를
     * 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> updateRecruitmentStatus(Long bandId, Long recruitmentId,
                                                     RecruitmentStatus recruitmentStatus) {
        log.info("Updating recruitment status. BandId: {}, RecruitmentId: {}, Status: {}",
                bandId, recruitmentId, recruitmentStatus);

        UUID memberId = securityUtil.getCurrentMemberId();
        RecruitmentStatus status;

        try {
            status = RecruitmentStatus.valueOf(String.valueOf(recruitmentStatus));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid recruitment status: {}", recruitmentStatus);
            throw new ExceptionHandler(ErrorStatus.INVALID_RECRUITMENT_STATUS);
        }

        RecruitmentDTO recruitmentDTO = recruitmentService.updateRecruitmentStatus(bandId, recruitmentId, status,
                memberId);

        log.info("Successfully updated recruitment status to {}: RecruitmentId: {}",
                status, recruitmentId);
        return ResponseEntity.ok(recruitmentDTO);
    }

    /**
     * DELETE /bands/{bandId}/recruitments/{recruitmentId} : 밴드 구인 공고 삭제 특정 구인 공고를 삭제합니다.
     *
     * @param bandId        밴드 ID (required)
     * @param recruitmentId 구인 공고 ID (required)
     * @return 성공적으로 처리되었습니다. (status code 204) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를 찾을 수 없습니다. (status code
     * 404)
     */
    @Override
    public ResponseEntity<?> deleteBandRecruitment(Long bandId, Long recruitmentId) {
        log.info("Deleting recruitment. BandId: {}, RecruitmentId: {}", bandId, recruitmentId);

        UUID memberId = securityUtil.getCurrentMemberId();
        recruitmentService.deleteBandRecruitment(bandId, recruitmentId, memberId);

        log.info("Successfully deleted recruitment. BandId: {}, RecruitmentId: {}", bandId, recruitmentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /bands/{bandId}/recruitments/{recruitmentId}/accept/{applicantId} : 지원자 승인 특정 지원자의 밴드 가입을 승인합니다.
     *
     * @param bandId        밴드 ID (required)
     * @param recruitmentId 구인 공고 ID (required)
     * @param applicantId   지원자 ID (required)
     * @return 성공적으로 승인되었습니다. (status code 200) or 잘못된 요청입니다. (status code 400) or 접근 권한이 없습니다. (status code 403) or 요청한
     * 리소스를 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> acceptApplicant(Long bandId, Long recruitmentId, UUID applicantId) {
        log.info("Accepting applicant. BandId: {}, RecruitmentId: {}, ApplicantId: {}",
                bandId, recruitmentId, applicantId);

        UUID memberId = securityUtil.getCurrentMemberId();
        recruitmentService.acceptApplicant(bandId, recruitmentId, applicantId, memberId);

        log.info("Successfully accepted applicant. BandId: {}, RecruitmentId: {}, ApplicantId: {}",
                bandId, recruitmentId, applicantId);
        return ResponseEntity.ok().body(Map.of("message", "지원자가 성공적으로 승인되었습니다."));
    }

    /**
     * POST /bands/{bandId}/recruitments/{recruitmentId}/instruments : 구인 공고 악기 포지션 추가 구인 공고에 새로운 악기 포지션을 추가합니다.
     *
     * @param bandId                          밴드 ID (required)
     * @param recruitmentId                   구인 공고 ID (required)
     * @param addRecruitmentInstrumentRequest (required)
     * @return 모집 악기 정보 (status code 201) or 잘못된 요청입니다. (status code 400) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를
     * 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> addRecruitmentInstrument(Long bandId, Long recruitmentId,
                                                      AddRecruitmentInstrumentRequest addRecruitmentInstrumentRequest) {
        log.info("Adding instrument to recruitment. BandId: {}, RecruitmentId: {}, Instrument: {}",
                bandId, recruitmentId, addRecruitmentInstrumentRequest.getInstrument());

        UUID memberId = securityUtil.getCurrentMemberId();
        RecruitmentInstrumentDTO instrumentDTO = recruitmentService.addRecruitmentInstrument(
                bandId, recruitmentId, addRecruitmentInstrumentRequest, memberId);

        log.info("Successfully added instrument: {} to recruitment: {}",
                instrumentDTO.getBandRecruitmentInstrumentsId(), recruitmentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(instrumentDTO);
    }

    /**
     * POST /recruitments/{recruitmentId}/apply : 구인 공고 지원 구인 공고에 지원합니다. 지원자 ID, 악기 포지션 포함
     *
     * @param recruitmentId           구인 공고 ID (required)
     * @param applyRecruitmentRequest (required)
     * @return 지원자 정보 (status code 201) or 잘못된 요청입니다. (status code 400) or 요청한 리소스를 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> applyRecruitment(Long recruitmentId, ApplyRecruitmentRequest applyRecruitmentRequest) {
        log.info("Applying to recruitment. RecruitmentId: {}, InstrumentId: {}",
                recruitmentId, applyRecruitmentRequest.getBandRecruitmentInstrumentsId());

        UUID memberId = securityUtil.getCurrentMemberId();
        ApplicantDTO applicantDTO = recruitmentService.applyRecruitment(
                recruitmentId, applyRecruitmentRequest, memberId);

        log.info("Successfully applied to recruitment. RecruitmentId: {}, ApplicantId: {}",
                recruitmentId, applicantDTO.getBandRecruitmentApplicantId());
        return ResponseEntity.status(HttpStatus.CREATED).body(applicantDTO);
    }

    /**
     * DELETE /recruitments/{recruitmentId}/applicants/{applicantId} : 구인 공고 지원 취소 구인 공고 지원을 취소합니다.
     *
     * @param recruitmentId 구인 공고 ID (required)
     * @param applicantId   지원자 ID (required)
     * @return 성공적으로 처리되었습니다. (status code 204) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를 찾을 수 없습니다. (status code
     * 404)
     */
    @Override
    public ResponseEntity<?> cancelRecruitmentApplication(Long recruitmentId, UUID applicantId) {
        log.info("Canceling application. RecruitmentId: {}, ApplicantId: {}", recruitmentId, applicantId);

        UUID memberId = securityUtil.getCurrentMemberId();
        recruitmentService.cancelRecruitmentApplication(recruitmentId, applicantId, memberId);

        log.info("Successfully canceled application. RecruitmentId: {}, ApplicantId: {}", recruitmentId, applicantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /bands/{bandId}/recruitments/{recruitmentId}/applicants : 구인 공고 지원자 목록 조회 구인 공고에 지원한 지원자 목록을 조회합니다.
     *
     * @param bandId        밴드 ID (required)
     * @param recruitmentId 구인 공고 ID (required)
     * @return 지원자 목록 (status code 200) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> getRecruitmentApplicants(Long bandId, Long recruitmentId) {
        log.info("Getting applicants for band ID: {} and recruitment ID: {}", bandId, recruitmentId);

        UUID memberId = securityUtil.getCurrentMemberId();
        List<ApplicantDTO> applicants = recruitmentService.getRecruitmentApplicants(bandId, recruitmentId, memberId);

        log.info("Found {} applicants for recruitment ID: {}", applicants.size(), recruitmentId);
        return ResponseEntity.ok(applicants);
    }

    /**
     * POST /bands/{bandId}/recruitments/{recruitmentId}/reject/{applicantId} : 지원자 거절 특정 지원자의 밴드 가입을 거절합니다.
     *
     * @param bandId        밴드 ID (required)
     * @param recruitmentId 구인 공고 ID (required)
     * @param applicantId   지원자 ID (required)
     * @return 성공적으로 거절되었습니다. (status code 200) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를 찾을 수 없습니다. (status code
     * 404)
     */
    @Override
    public ResponseEntity<?> rejectApplicant(Long bandId, Long recruitmentId, UUID applicantId) {
        log.info("Rejecting applicant. BandId: {}, RecruitmentId: {}, ApplicantId: {}",
                bandId, recruitmentId, applicantId);

        UUID memberId = securityUtil.getCurrentMemberId();
        recruitmentService.rejectApplicant(bandId, recruitmentId, applicantId, memberId);

        log.info("Successfully rejected applicant. BandId: {}, RecruitmentId: {}, ApplicantId: {}",
                bandId, recruitmentId, applicantId);
        return ResponseEntity.ok().body(Map.of("message", "지원자가 성공적으로 거절되었습니다."));
    }

    /**
     * DELETE /bands/{bandId}/recruitments/{recruitmentId}/instruments/{instrumentId} : 구인 공고 악기 포지션 제거 구인 공고에서 특정 악기
     * 포지션을 제거합니다.
     *
     * @param bandId        밴드 ID (required)
     * @param recruitmentId 구인 공고 ID (required)
     * @param instrumentId  모집 악기 ID (required)
     * @return 성공적으로 처리되었습니다. (status code 204) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를 찾을 수 없습니다. (status code
     * 404)
     */
    @Override
    public ResponseEntity<?> removeRecruitmentInstrument(Long bandId, Long recruitmentId, Long instrumentId) {
        log.info("Removing instrument from recruitment. BandId: {}, RecruitmentId: {}, InstrumentId: {}",
                bandId, recruitmentId, instrumentId);

        UUID memberId = securityUtil.getCurrentMemberId();
        recruitmentService.removeRecruitmentInstrument(bandId, recruitmentId, instrumentId, memberId);

        log.info("Successfully removed instrument: {} from recruitment: {}", instrumentId, recruitmentId);
        return ResponseEntity.noContent().build();
    }
}
