package com.mickey.dinggading.domain.membermatching.controller;

import com.mickey.dinggading.api.TierApi;
import com.mickey.dinggading.domain.membermatching.service.RankMatchingService;
import com.mickey.dinggading.domain.memberrank.model.Instrument;
import com.mickey.dinggading.model.DefencePeriodDTO;
import com.mickey.dinggading.util.SecurityUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 티어 시스템 관련 API를 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tiers")
public class TierController implements TierApi {

    private final RankMatchingService rankMatchingService;
    private final SecurityUtil securityUtil;

    /**
     * 티어 시스템에 대한 기본 정보를 조회합니다.
     *
     * @return 티어 시스템 기본 정보
     */
    @GetMapping
    public ResponseEntity<?> getTierInfo() {
        // 티어 시스템 정보 구성
        return null;
    }

    /**
     * GET /api/tiers/defence-period : 티어 방어 기간 정보 조회 현재 로그인한 회원의 티어 방어 기간 정보를 조회합니다.
     *
     * @param instrument 악기 종류 (VOCAL, GUITAR, DRUM, BASS) (required)
     * @return 티어 방어 기간 정보 응답 (status code 200) or 잘못된 요청입니다. (status code 400) or 인증되지 않은 요청입니다. (status code 401)
     */

    @GetMapping("/defence-period")
    public ResponseEntity<?> getDefencePeriod(
            @RequestParam String instrument) {
        UUID currentUser = securityUtil.getCurrentMemberId();
        DefencePeriodDTO defencePeriod = rankMatchingService.getDefencePeriod(
                currentUser, Instrument.valueOf(instrument));
        return ResponseEntity.ok(defencePeriod);
    }
}