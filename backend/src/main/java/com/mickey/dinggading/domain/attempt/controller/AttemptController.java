package com.mickey.dinggading.domain.attempt.controller;

import com.mickey.dinggading.api.AttemptApi;
import com.mickey.dinggading.domain.attempt.service.AttemptService;
import com.mickey.dinggading.model.AttemptDTO;
import com.mickey.dinggading.model.CreateAttemptRequestDTO;
import com.mickey.dinggading.util.SecurityUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 시도 기록 관련 API를 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AttemptController implements AttemptApi {

    private final AttemptService attemptService;
    private final SecurityUtil securityUtil;

    /**
     * POST /api/attempts : 연주 시도 기록 생성 사용자의 연주 시도를 기록하고 음성 분석을 요청합니다. 연습 모드와 랭크 모드 모두 사용 가능합니다. 랭크 모드의 경우
     * rankMatchingId와 rankType을 함께 제공해야 합니다.
     *
     * @param requestDTO (required)
     * @return CreateAttempt201Response 시도 기록 생성 성공 응답 (status code 201) or 잘못된 요청입니다. (status code 400) or 인증되지 않은
     * 요청입니다. (status code 401) or 요청한 리소스를 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> createAttempt(CreateAttemptRequestDTO requestDTO) {
        log.info("연주 시도 기록 생성 요청: {}", requestDTO);
        UUID currentId = securityUtil.getCurrentMemberId();

        // 시도 기록 생성 및 분석 요청
        AttemptDTO attempt = attemptService.createAttempt(requestDTO, currentId);

        return new ResponseEntity<>(attempt, HttpStatus.CREATED);
    }

    @GetMapping("/api/attempts/test")
    public ResponseEntity<?> test(
            @RequestParam(required = true) Long attemptId,
            @RequestParam Integer score1,
            @RequestParam Integer score2,
            @RequestParam Integer score3
    ) {
        attemptService.updateAttemptScore(attemptId, score1, score2, score3);
        return new ResponseEntity<>("successes", HttpStatus.CREATED);

    }

    /**
     * 특정 시도 기록의 상세 정보를 조회합니다.
     *
     * @param attemptId 시도 ID
     * @return 시도 상세 정보
     */
    @Override
    public ResponseEntity<?> getAttempt(@PathVariable("attempt_id") Long attemptId) {
        AttemptDTO attempt = attemptService.getAttempt(attemptId);
        return ResponseEntity.ok(attempt);
    }

    /**
     * 연습 모드 시도 기록을 조회합니다.
     *
     * @param instrument 악기 (선택적)
     * @param pageable   페이징 정보
     * @return 연습 모드 시도 기록 페이지
     */
    @Override
    public ResponseEntity<Page<?>> getPracticeAttempts(
            @RequestParam(required = false) String instrument,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UUID currentUser = securityUtil.getCurrentMemberId();
        Page<AttemptDTO> attempts = attemptService.getPracticeAttempts(currentUser, instrument,
                pageable);
        return ResponseEntity.ok(attempts);
    }

    /**
     * 랭크 모드 시도 기록을 조회합니다.
     *
     * @param instrument 악기 (선택적)
     * @param rankType   랭크 유형 (선택적)
     * @param pageable   페이징 정보
     * @return 랭크 모드 시도 기록 페이지
     */
    @Override
    public ResponseEntity<Page<?>> getRankAttempts(
            @RequestParam(required = false) String instrument,
            @RequestParam(required = false) String rankType,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        UUID currentUser = securityUtil.getCurrentMemberId();

        Page<AttemptDTO> attempts = attemptService.getRankAttempts(currentUser, instrument, rankType,
                pageable);
        return ResponseEntity.ok(attempts);
    }
}