package com.mickey.dinggading.domain.memberrank.controller;

import com.mickey.dinggading.api.MemberRankApi;
import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.memberrank.model.Instrument;
import com.mickey.dinggading.domain.memberrank.service.MemberRankService;
import com.mickey.dinggading.exception.ExceptionHandler;
import com.mickey.dinggading.model.MemberRankDTO;
import com.mickey.dinggading.util.SecurityUtil;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.service.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberRankController implements MemberRankApi {

    private final MemberRankService memberRankService;
    private final SecurityService securityService;
    private final SecurityUtil securityUtil;

    /**
     * 회원 악기별 랭크 정보 조회 API 특정 회원의 악기별 랭크 정보를 조회합니다.
     *
     * @param memberId 회원 ID
     * @return 회원의 악기별 랭크 정보 DTO 리스트
     * @deprecated API 명세에 따라 더 이상 사용되지 않는 API
     */
    @Override
    @Deprecated
    public ResponseEntity<?> getMemberRanks(UUID memberId) {
        log.info("Request to get rank information for member ID: {}", memberId);

        List<MemberRankDTO> memberRanks = memberRankService.getMyRanks(memberId);
        return ResponseEntity.ok(memberRanks);
    }

    /**
     * 현재 로그인한 회원의 악기별 랭크 정보를 조회하는 API
     *
     * @return 회원의 악기별 랭크 정보 DTO 리스트
     */
    @Override
    public ResponseEntity<?> getMyRanks() {
        UUID memberId = securityUtil.getCurrentMemberId();
        log.info("Request to get rank information for current user");

        // 현재 로그인한 회원 ID 추출
        log.debug("Current member ID: {}", memberId);

        // 서비스 호출하여 회원의 악기별 랭크 정보 조회
        List<MemberRankDTO> memberRanks = memberRankService.getMyRanks(memberId);

        return ResponseEntity.ok(memberRanks);
    }

    /**
     * 현재 로그인한 회원의 특정 악기에 대한 랭크 정보를 조회하는 API
     *
     * @param instrument 조회할 악기 종류
     * @return 회원의 특정 악기에 대한 랭크 정보 DTO
     */
    @Override
    public ResponseEntity<?> getMyRankByInstrument(String instrument) {
        log.info("Request to get rank information for current user and instrument: {}", instrument);

        // 현재 로그인한 회원 ID 추출
        UUID memberId = securityUtil.getCurrentMemberId();

        log.debug("Current member ID: {}", memberId);

        // String을 Instrument enum으로 변환
        Instrument instrumentEnum;
        try {
            instrumentEnum = Instrument.valueOf(instrument.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid instrument type: {}", instrument);
            throw new ExceptionHandler(ErrorStatus.INVALID_INSTRUMENT_TYPE);
        }

        // 서비스 호출하여 회원의 특정 악기에 대한 랭크 정보 조회
        MemberRankDTO memberRank = memberRankService.getMyRankByInstrument(memberId, instrumentEnum);

        return ResponseEntity.ok(memberRank);
    }
}