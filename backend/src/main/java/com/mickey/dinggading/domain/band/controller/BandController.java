package com.mickey.dinggading.domain.band.controller;

import com.mickey.dinggading.api.BandApi;
import com.mickey.dinggading.domain.band.repository.BandRepository;
import com.mickey.dinggading.domain.band.service.BandService;
import com.mickey.dinggading.model.AddBandMemberRequest;
import com.mickey.dinggading.model.BandDTO;
import com.mickey.dinggading.model.BandMemberDTO;
import com.mickey.dinggading.model.ContactDTO;
import com.mickey.dinggading.model.CreateBandContactRequest;
import com.mickey.dinggading.model.CreateBandRequest;
import com.mickey.dinggading.model.PageBandDTO;
import com.mickey.dinggading.model.TransferBandMasterRequest;
import com.mickey.dinggading.model.UpdateBandMemberInstrumentRequest;
import com.mickey.dinggading.model.UpdateBandRequest;
import com.mickey.dinggading.util.JWTUtil;
import com.mickey.dinggading.util.SecurityUtil;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BandController implements BandApi {

    private final BandService bandService;
    private final JWTUtil jwtUtil;
    private final SecurityUtil securityUtil;
    private final BandRepository bandRepository;

    /**
     * POST /bands/{bandId}/members : 밴드 멤버 추가 밴드에 새로운 멤버를 추가합니다. 멤버 ID와 악기 포지션 지정
     *
     * @param bandId               밴드 ID (required)
     * @param addBandMemberRequest (required)
     * @return 밴드 멤버 정보 (status code 201) or 잘못된 요청입니다. (status code 400) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를
     * 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> addBandMember(Long bandId, AddBandMemberRequest addBandMemberRequest) {
        UUID memberId = securityUtil.getCurrentMemberId();

        List<BandMemberDTO> bandMemberList = bandService.addBandMember(memberId, bandId, addBandMemberRequest);
        return ResponseEntity.status(HttpStatus.OK).body(bandMemberList);
    }

    /**
     * POST /bands : 밴드 생성 새로운 밴드를 생성합니다. 밴드 마스터 정보, 이름, 설명 등 필수 정보 설정
     *
     * @param createBandRequest (required)
     * @return 밴드 정보 (status code 201) or 잘못된 요청입니다. (status code 400)
     */
    @Override
    public ResponseEntity<?> createBand(CreateBandRequest createBandRequest) {

        UUID memberId = securityUtil.getCurrentMemberId();
        log.info("현재 로그인한 사용자 ID: {}", memberId);

        BandDTO band = bandService.createBand(createBandRequest, memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(band);
    }

    /**
     * DELETE /bands/{bandId} : 밴드 삭제 밴드를 삭제합니다. 연관된 모든 정보 정리
     *
     * @param bandId 밴드 ID (required)
     * @return 성공적으로 처리되었습니다 (status code 204) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를 찾을 수 없습니다. (status code
     * 404)
     */
    @Override
    public ResponseEntity<?> deleteBand(Long bandId) {
        UUID currentMemberId = securityUtil.getCurrentMemberId();
        log.info("Delete band request received for bandId: {} by memberId: {}", bandId, currentMemberId);

        // 밴드 삭제 서비스 호출
        bandService.deleteBand(bandId, currentMemberId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * GET /bands : 전체 밴드 목록 조회 전체 밴드 목록을 조회합니다. 페이징 처리된 결과를 반환합니다. size, page를 쿼리 파라미터로 전달하여 페이지네이션이 가능합니다.
     *
     * @param pageable
     * @return 밴드 목록 (status code 200)
     */
    @Override
    public ResponseEntity<?> getBands(Pageable pageable) {
        PageBandDTO pageBandDTO = bandService.getAllBands(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(pageBandDTO);
    }

    /**
     * GET /bands/{bandId} : 밴드 정보 조회 밴드 정보를 조회합니다. 이름, 설명, 장르, 태그, 프로필 이미지 등 기본 정보 포함
     *
     * @param bandId 밴드 ID (required)
     * @return 밴드 정보 (status code 200) or 요청한 리소스를 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> getBand(Long bandId) {
        BandDTO bandDTO = bandService.getBandById(bandId);

        return ResponseEntity.status(HttpStatus.OK).body(bandDTO);
    }

    /**
     * GET /bands/{bandId}/members : 밴드 멤버 목록 조회 밴드 멤버 목록을 조회합니다. 각 멤버의 악기 포지션 정보 포함
     *
     * @param bandId   밴드 ID (required)
     * @param pageable
     * @return 밴드 멤버 목록 (status code 200) or 요청한 리소스를 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> getBandMembers(Long bandId, Pageable pageable) {
        List<BandMemberDTO> bandMembers = bandService.getBandMembers(bandId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(bandMembers);
    }

    /**
     * PATCH /bands/{bandId}/members/{memberId}/instrument : 밴드 멤버 악기 변경 밴드 내 멤버의 악기 포지션을 변경합니다.
     *
     * @param bandId                            밴드 ID (required)
     * @param memberId                          회원 ID (required)
     * @param updateBandMemberInstrumentRequest (required)
     * @return 밴드 멤버 정보 (status code 200) or 잘못된 요청입니다. (status code 400) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를
     * 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> updateBandMemberInstrument(Long bandId, UUID memberId,
                                                        UpdateBandMemberInstrumentRequest updateBandMemberInstrumentRequest) {
        UUID currentMemberId = securityUtil.getCurrentMemberId();
        log.info("Request to update instrument for member: {} in band: {} by user: {}",
                memberId, bandId, currentMemberId);

        BandMemberDTO updatedMember = bandService.updateBandMemberInstrument(
                bandId, memberId, updateBandMemberInstrumentRequest.getInstrument(), currentMemberId);

        return ResponseEntity.status(HttpStatus.OK).body(updatedMember);
    }

    /**
     * DELETE /bands/{bandId}/members/{memberId} : 밴드 멤버 제거 밴드에서 특정 멤버를 제거합니다.
     *
     * @param bandId   밴드 ID (required)
     * @param memberId 회원 ID (required)
     * @return 성공적으로 처리되었습니다 (status code 204) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를 찾을 수 없습니다. (status code
     * 404)
     */
    @Override
    public ResponseEntity<?> removeBandMember(Long bandId, UUID memberId) {
        UUID currentMemberId = securityUtil.getCurrentMemberId();

        bandService.removeBandMember(bandId, memberId, currentMemberId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * GET /bands/search : 밴드 검색 밴드를 검색합니다. 이름, 지역, 장르 등 검색 조건 지원. 페이징 처리된 결과를 반환합니다. size, page를 쿼리 파라미터로 전달하여 페이지네이션이
     * 가능합니다.
     *
     * @param keyword    검색 키워드 (optional)
     * @param sigun      지역(시군) (optional)
     * @param jobOpening 구인 여부 (optional)
     * @param pageable
     * @return 밴드 목록 (status code 200)
     */
    @Override
    public ResponseEntity<?> searchBands(String keyword, String sigun, Boolean jobOpening, Pageable pageable) {
        PageBandDTO searchResult = bandService.searchBands(keyword, sigun, jobOpening, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(searchResult);
    }

    /**
     * PATCH /bands/{bandId} : 밴드 정보 수정 밴드 정보를 수정합니다. 이름, 설명, 장르, 태그 등 업데이트
     *
     * @param bandId            밴드 ID (required)
     * @param updateBandRequest (required)
     * @return 밴드 정보 (status code 200) or 잘못된 요청입니다. (status code 400) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를 찾을
     * 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> updateBand(Long bandId, UpdateBandRequest updateBandRequest) {
        UUID currentMemberId = securityUtil.getCurrentMemberId();

        BandDTO updatedBand = bandService.updateBand(bandId, updateBandRequest, currentMemberId);

        return ResponseEntity.status(HttpStatus.OK).body(updatedBand);
    }

    /**
     * POST /bands/{bandId}/contacts : 밴드 연락처 추가 밴드에 새로운 연락처 정보를 추가합니다. SNS 유형, URL 등 포함
     *
     * @param bandId                   밴드 ID (required)
     * @param createBandContactRequest (required)
     * @return 연락처 정보 (status code 201) or 잘못된 요청입니다. (status code 400) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를 찾을
     * 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> createBandContact(Long bandId, CreateBandContactRequest createBandContactRequest) {
        UUID currentMemberId = securityUtil.getCurrentMemberId();
        log.info("Request to add new contact to band: {} by user: {}", bandId, currentMemberId);

        ContactDTO newContact = bandService.createBandContact(bandId, createBandContactRequest, currentMemberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(newContact);
    }

    /**
     * GET /bands/{bandId}/contacts : 밴드 연락처 목록 조회 밴드의 연락처 정보 목록을 조회합니다. SNS 링크 등 포함
     *
     * @param bandId 밴드 ID (required)
     * @return 연락처 목록 (status code 200) or 요청한 리소스를 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> getBandContacts(Long bandId) {
        List<ContactDTO> contacts = bandService.getBandContacts(bandId);

        return ResponseEntity.status(HttpStatus.OK).body(contacts);
    }

    /**
     * PATCH /bands/{bandId}/contacts/{contactId} : 밴드 연락처 수정 특정 연락처 정보를 수정합니다.
     *
     * @param bandId                   밴드 ID (required)
     * @param contactId                연락처 ID (required)
     * @param createBandContactRequest (required)
     * @return 연락처 정보 (status code 200) or 잘못된 요청입니다. (status code 400) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를 찾을
     * 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> updateBandContact(Long bandId, Long contactId,
                                               CreateBandContactRequest createBandContactRequest) {
        UUID currentMemberId = securityUtil.getCurrentMemberId();
        log.info("Request to update contact: {} for band: {} by user: {}",
                contactId, bandId, currentMemberId);

        ContactDTO updatedContact = bandService.updateBandContact(bandId, contactId, createBandContactRequest,
                currentMemberId);

        return ResponseEntity.status(HttpStatus.OK).body(updatedContact);
    }

    /**
     * DELETE /bands/{bandId}/contacts/{contactId} : 밴드 연락처 삭제 특정 연락처 정보를 삭제합니다.
     *
     * @param bandId    밴드 ID (required)
     * @param contactId 연락처 ID (required)
     * @return 성공적으로 처리되었습니다 (status code 204) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를 찾을 수 없습니다. (status code
     * 404)
     */
    @Override
    public ResponseEntity<?> deleteBandContact(Long bandId, Long contactId) {
        UUID currentMemberId = securityUtil.getCurrentMemberId();
        log.info("Request to delete contact: {} from band: {} by user: {}",
                contactId, bandId, currentMemberId);

        bandService.deleteBandContact(bandId, contactId, currentMemberId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * POST /bands/{bandId}/master/transfer : 밴드 마스터 권한 이전 밴드 마스터 권한을 다른 멤버에게 이전합니다.
     *
     * @param bandId                    밴드 ID (required)
     * @param transferBandMasterRequest (required)
     * @return 밴드 정보 (status code 200) or 잘못된 요청입니다. (status code 400) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를 찾을
     * 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> transferBandMaster(Long bandId, TransferBandMasterRequest transferBandMasterRequest) {
        UUID currentMemberId = securityUtil.getCurrentMemberId();
        log.info("Request to transfer band master rights for band: {} to member: {} by user: {}",
                bandId, transferBandMasterRequest.getNewMasterId(), currentMemberId);

        BandDTO updatedBand = bandService.transferBandMaster(
                bandId, transferBandMasterRequest.getNewMasterId(), currentMemberId);

        return ResponseEntity.status(HttpStatus.OK).body(updatedBand);
    }
}
