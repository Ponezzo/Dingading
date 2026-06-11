package com.mickey.dinggading.domain.record.controller;

import com.mickey.dinggading.api.RecordApi;
import com.mickey.dinggading.domain.record.service.RecordService;
import com.mickey.dinggading.model.RecordCreateRequestDTO;
import com.mickey.dinggading.model.RecordDTO;
import com.mickey.dinggading.util.SecurityUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RecordController implements RecordApi {
    private final RecordService recordService;
    private final SecurityUtil securityUtil;

    /**
     * POST /api/records/me : 녹음 생성 새로운 연주 녹음을 업로드합니다. 업로드가 끝나면 분석이 바로 시작됩니다.
     *
     * @param recordInfo (required)
     * @param audioFile  녹음 파일 (mp3, wav 형식) (required)
     * @return RecordDTO 녹음 생성 결과 (status code 201) or 잘못된 요청 (status code 400) or 인증 실패 (status code 401)
     */
    @Override
    public ResponseEntity<?> createRecord(RecordCreateRequestDTO recordInfo, MultipartFile audioFile) {
        UUID memberId = securityUtil.getCurrentMemberId();
        log.info("녹음 생성 요청: dtype = {}", recordInfo.getDtype());

        RecordDTO createdRecord = recordService.createRecord(recordInfo, memberId, audioFile);
        return new ResponseEntity<>(createdRecord, HttpStatus.CREATED);
    }

    /**
     * GET /api/attempts/{attemptId}/records : 도전별 녹음 조회 특정 도전에 대한 녹음을 조회합니다.
     *
     * @param attemptId 도전 ID (required)
     * @return 녹음 정보 (status code 200) or 인증 실패 (status code 401) or 요청한 리소스를 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> getAttemptRecords(Long attemptId) {
        log.info("도전별 녹음 조회 요청: attemptId = {}", attemptId);
        RecordDTO record = recordService.getAttemptRecord(attemptId);
        return ResponseEntity.ok(record);
    }

    /**
     * GET /api/members/{memberId}/records : 회원별 녹음 목록 조회 특정 회원의 녹음 목록을 조회합니다. 페이지네이션이 가능합니다. size, page
     *
     * @param memberId 회원 ID (required)
     * @param dtype    녹음 유형으로 필터링 (optional)
     * @param pageable
     * @return 회원별 녹음 목록 조회 결과 (status code 200) or 인증 실패 (status code 401) or 요청한 리소스를 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> getMemberRecords(UUID memberId, String dtype, Pageable pageable) {
        log.info("회원별 녹음 목록 조회 요청: memberId = {}, dtype = {}, page = {}, size = {}",
                memberId, dtype, pageable.getPageNumber(), pageable.getPageSize());
        Page<RecordDTO> recordPage = recordService.getMemberRecords(memberId, dtype, pageable);
        return ResponseEntity.ok(recordPage);
    }

    /**
     * GET /api/records/{recordId} : 녹음 상세 조회 특정 녹음의 상세 정보를 조회합니다.
     *
     * @param recordId 녹음 ID (required)
     * @return 녹음 정보 (status code 200) or 인증 실패 (status code 401) or 요청한 리소스를 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> getRecord(Long recordId) {
        log.info("녹음 상세 조회 요청: recordId = {}", recordId);
        RecordDTO record = recordService.getRecord(recordId);
        return ResponseEntity.ok(record);
    }

    /**
     * GET /api/records : 녹음 목록 조회 모든 녹음 목록을 조회합니다.  페이지네이션이 가능합니다. size, page
     *
     * @param memberId 회원 ID로 필터링 (optional)
     * @param dtype    녹음 유형으로 필터링 (optional)
     * @param pageable
     * @return 녹음 목록 조회 결과 (status code 200) or 인증 실패 (status code 401)
     */
    @Override
    public ResponseEntity<?> getRecords(UUID memberId, String dtype, Pageable pageable) {
        log.info("녹음 목록 조회 요청: memberId = {}, dtype = {}, page = {}, size = {}",
                memberId, dtype, pageable.getPageNumber(), pageable.getPageSize());
        Page<RecordDTO> recordPage = recordService.getRecords(memberId, dtype, pageable);
        return ResponseEntity.ok(recordPage);
    }
}