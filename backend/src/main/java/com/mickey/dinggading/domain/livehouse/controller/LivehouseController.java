// 파일 경로: src/main/java/com/mickey/dinggading/domain/livehouse/controller/LivehouseController.java
package com.mickey.dinggading.domain.livehouse.controller;

import com.mickey.dinggading.api.LivehouseApi;
import com.mickey.dinggading.domain.livehouse.dto.LivehouseDTO;
import com.mickey.dinggading.domain.livehouse.dto.LivehouseSessionDTO;
import com.mickey.dinggading.domain.livehouse.dto.ParticipantDTO;
import com.mickey.dinggading.domain.livehouse.service.LivehouseService;
import com.mickey.dinggading.model.CreateLivehouseRequestDTO;
import com.mickey.dinggading.util.SecurityUtil;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LivehouseController implements LivehouseApi {

    private final LivehouseService livehouseService;
    private final SecurityUtil securityUtil;

    @Override
    public ResponseEntity<Page<LivehouseDTO>> listLivehouses(Pageable pageable) {
        log.info("listLivehouses 호출");
        return ResponseEntity.ok(livehouseService.listLivehouses(pageable));
    }

    @Override
    public ResponseEntity<LivehouseDTO> getLivehouse(@PathVariable Long livehouseId) {
        log.info("getLivehouse - /livehouseId 호출");
        return ResponseEntity.ok(livehouseService.getLivehouse(livehouseId));
    }

    @Override
    public ResponseEntity<?> createLivehouse(CreateLivehouseRequestDTO requestDTO) {
        log.info("createLivehouse 호출");

        UUID memberId = securityUtil.getCurrentMemberId();

        LivehouseSessionDTO livehouse = livehouseService.createLivehouse(requestDTO, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(livehouse);
    }

    @Override
    public ResponseEntity<LivehouseSessionDTO> joinLivehouse(@PathVariable Long livehouseId) {
        log.info("joinLivehouse - /{livehouseId}/join 호출");

        UUID memberId = securityUtil.getCurrentMemberId();

        return ResponseEntity.ok(livehouseService.joinLivehouse(livehouseId, memberId));
    }

    @Override
    public ResponseEntity<Void> leaveLivehouse(@PathVariable Long livehouseId, @PathVariable Long participantId) {
        log.info("leaveLivehouse - /{livehouseId}/leave/{participantId 호출");
        livehouseService.leaveLivehouse(livehouseId, participantId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> closeLivehouse(
            @PathVariable Long livehouseId,
            @RequestParam Long participantId) {
        log.info("closeLivehouse - /{livehouseId}/close 호출");
        livehouseService.closeLivehouse(livehouseId, participantId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ParticipantDTO>> listParticipants(@PathVariable Long livehouseId) {
        log.info("listParticipants - /livehouseId/participants 호출");
        return ResponseEntity.ok(livehouseService.listParticipants(livehouseId));
    }

    @Override
    public ResponseEntity<Page<LivehouseDTO>> searchLivehouses(@RequestParam("keyword") String keyword,
                                                               Pageable pageable) {
        log.info("searchLivehouses - /search/keyword 호출");
        return ResponseEntity.ok(livehouseService.searchLivehouses(keyword, pageable));
    }
}