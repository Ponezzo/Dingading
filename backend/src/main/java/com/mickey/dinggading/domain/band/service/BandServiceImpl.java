package com.mickey.dinggading.domain.band.service;

import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.band.converter.BandConverter;
import com.mickey.dinggading.domain.band.model.entity.Band;
import com.mickey.dinggading.domain.band.model.entity.BandMember;
import com.mickey.dinggading.domain.band.model.entity.Contact;
import com.mickey.dinggading.domain.band.repository.BandMemberRepository;
import com.mickey.dinggading.domain.band.repository.BandRepository;
import com.mickey.dinggading.domain.band.repository.ContactRepository;
import com.mickey.dinggading.domain.member.model.entity.Member;
import com.mickey.dinggading.domain.member.repository.MemberRepository;
import com.mickey.dinggading.exception.ExceptionHandler;
import com.mickey.dinggading.model.AddBandMemberRequest;
import com.mickey.dinggading.model.BandDTO;
import com.mickey.dinggading.model.BandMemberDTO;
import com.mickey.dinggading.model.ContactDTO;
import com.mickey.dinggading.model.CreateBandContactRequest;
import com.mickey.dinggading.model.CreateBandRequest;
import com.mickey.dinggading.model.Instrument;
import com.mickey.dinggading.model.InstrumentAvailabilityDTO;
import com.mickey.dinggading.model.PageBandDTO;
import com.mickey.dinggading.model.UpdateBandRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BandServiceImpl implements BandService {

    private final BandRepository bandRepository;
    private final BandMemberRepository bandMemberRepository;
    private final ContactRepository contactRepository;
    private final MemberRepository memberRepository;
    private final BandConverter bandConverter;

    // 요청자가 밴드 마스터인지 확인
    private static void isBandMaster(Long bandId, UUID memberId, Band band) {
        if (!band.getBandMasterId().equals(memberId)) {
            log.warn("Unauthorized attempt to delete band. BandId: {}, RequesterId: {}, MasterId: {}",
                    bandId, memberId, band.getBandMasterId());
            throw new ExceptionHandler(ErrorStatus.UNAUTHORIZED_ACCESS);
        }
    }

    @Override
    public BandDTO createBand(CreateBandRequest request, UUID memberId) {

        Member master = memberRepository.findById(memberId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Band band = new Band(request, master.getMemberId());

        BandMember bandMember = new BandMember(band, master, null);
        bandRepository.save(band);

        return bandConverter.toBandDTO(band);

    }

    @Override
    public List<BandMemberDTO> addBandMember(UUID memberId, Long bandId, AddBandMemberRequest addBandMemberRequest) {
        // 밴드 존재 여부 확인
        Band band = getBand(bandId);

        // 요청자가 밴드 마스터인지 확인
        isBandMaster(bandId, memberId, band);

        // 요청된 멤버가 존재하는지 확인
        Member member = memberRepository.findById(addBandMemberRequest.getMemberId())
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 이미 밴드에 속해있는지 확인
        if (bandMemberRepository.existsByBand_BandIdAndMember_MemberId(bandId, member.getMemberId())) {
            throw new ExceptionHandler(ErrorStatus.MEMBER_ALREADY_IN_BAND);
        }

        BandMember bandMember = new BandMember(band, member, addBandMemberRequest.getInstrument());
        bandRepository.save(band);

        List<BandMember> bandMembers = bandMemberRepository.getBandMembersByBand_BandId(bandId);

        // 업데이트된 전체 밴드 멤버 목록 반환
        return bandMembers.stream()
                .map(bandConverter::toBandMemberDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBand(Long bandId, UUID memberId) {
        // 밴드 존재 여부 확인
        Band band = getBand(bandId);

        // 요청자가 밴드 마스터인지 확인
        isBandMaster(bandId, memberId, band);

        // CascadeType.ALL과 orphanRemoval=true 설정으로 인해
        // 밴드를 삭제하면 연관된 밴드 멤버와 연락처도 자동으로 삭제됨
        bandRepository.delete(band);

        log.info("Band successfully deleted. BandId: {}", bandId);
    }

    @Override
    public PageBandDTO getAllBands(Pageable pageable) {
        Page<Band> bandPage = bandRepository.findAll(pageable);

        // 조회된 밴드 ID 목록 추출
        List<Long> bandIds = bandPage.getContent().stream()
                .map(Band::getBandId)
                .collect(Collectors.toList());

        // 모든 밴드에 대한 멤버 정보를 한 번에 조회 (N+1 문제 해결)
        Map<Long, List<BandMember>> bandMembersMap;

        if (!bandIds.isEmpty()) {
            List<BandMember> allBandMembers = bandMemberRepository.findAllByBand_BandIdIn(bandIds);

            // 밴드 ID별로 멤버 그룹화
            bandMembersMap = allBandMembers.stream()
                    .collect(Collectors.groupingBy(member -> member.getBand().getBandId()));
        } else {
            bandMembersMap = new HashMap<>();
        }

        // 각 밴드에 멤버 정보 설정 및 DTO 변환
        List<BandDTO> bandDTOs = bandPage.getContent().stream()
                .map(band -> {
                    // 해당 밴드의 멤버 가져오기
                    List<BandMember> bandMembers = bandMembersMap.getOrDefault(band.getBandId(),
                            Collections.emptyList());

                    // 밴드 엔티티에 멤버 정보 설정
                    band.updateBandMembers(bandMembers);

                    // BandDTO로 변환
                    BandDTO bandDTO = bandConverter.toBandDTO(band);

                    // 악기 보유 현황 설정
                    setInstrumentAvailability(bandDTO, bandMembers);

                    return bandDTO;
                })
                .collect(Collectors.toList());

        return bandConverter.toPageBandDTO(bandPage, bandDTOs);

    }

    private void setInstrumentAvailability(BandDTO bandDTO, List<BandMember> bandMembers) {
        // 악기 보유 현황 DTO 생성
        InstrumentAvailabilityDTO instrumentAvailability = InstrumentAvailabilityDTO.builder()
                .VOCAL(bandMembers.stream().anyMatch(member -> Instrument.VOCAL.equals(member.getInstrument())))
                .GUITAR(bandMembers.stream().anyMatch(member -> Instrument.GUITAR.equals(member.getInstrument())))
                .DRUM(bandMembers.stream().anyMatch(member -> Instrument.DRUM.equals(member.getInstrument())))
                .BASS(bandMembers.stream().anyMatch(member -> Instrument.BASS.equals(member.getInstrument())))
                .build();

        // BandDTO에 악기 보유 현황 설정
        bandDTO.setInstrumentAvailability(instrumentAvailability);
    }

    @Override
    public BandDTO getBandById(Long bandId) {
        // 밴드 존재 여부 확인
        Band band = getBand(bandId);

        List<BandMember> bandMembers = bandMemberRepository.findAllWithMemberByBandId(bandId);
        band.updateBandMembers(bandMembers);

        List<Contact> contacts = contactRepository.findAllByBand_BandId(bandId);
        band.updateContacts(contacts);

        BandDTO bandDTO = bandConverter.toBandDTO(band);

        // 악기 보유 현황 설정
        setInstrumentAvailability(bandDTO, bandMembers);

        return bandDTO;
    }

    @Override
    public List<BandMemberDTO> getBandMembers(Long bandId, Pageable pageable) {
        // 밴드 존재 여부 확인
        isExistBand(bandId);

        Page<BandMember> bandMembersPage = bandMemberRepository.findByBand_BandId(bandId, pageable);

        return bandMembersPage.getContent().stream()
                .map(bandConverter::toBandMemberDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void removeBandMember(Long bandId, UUID memberIdToRemove, UUID requesterId) {
        // 밴드 존재 여부 확인
        Band band = getBand(bandId);

        // 제거할 멤버가 밴드에 속해 있는지 확인
        Optional<BandMember> memberToRemoveOpt = bandMemberRepository
                .findByBand_BandIdAndMember_MemberId(bandId, memberIdToRemove);

        if (memberToRemoveOpt.isEmpty()) {
            log.warn("Member with ID: {} is not in band with ID: {}", memberIdToRemove, bandId);
            throw new ExceptionHandler(ErrorStatus.MEMBER_NOT_IN_BAND);
        }

        // 권한 확인: 요청자가 밴드 마스터이거나 자기 자신을 제거하려는 경우에만 허용
        boolean isBandMaster = band.getBandMasterId().equals(requesterId);
        boolean isRemovingSelf = memberIdToRemove.equals(requesterId);

        if (!isBandMaster && !isRemovingSelf) {
            log.warn("Unauthorized attempt to remove member. BandId: {}, MemberToRemove: {}, Requester: {}",
                    bandId, memberIdToRemove, requesterId);
            throw new ExceptionHandler(ErrorStatus.UNAUTHORIZED_ACCESS);
        }

        // 밴드 마스터는 탈퇴할 수 없음 (권한 이전이 필요)
        if (isRemovingSelf && isBandMaster) {
            log.warn("Band master cannot remove themselves. BandId: {}, MasterId: {}",
                    bandId, requesterId);
            throw new ExceptionHandler(ErrorStatus.BAND_MASTER_CANNOT_LEAVE);
        }

        // 멤버 제거
        BandMember memberToRemove = memberToRemoveOpt.get();
        bandMemberRepository.delete(memberToRemove);

        log.info("Member successfully removed from band. BandId: {}, MemberId: {}",
                bandId, memberIdToRemove);
    }

    @Override
    public PageBandDTO searchBands(String keyword, String sigun, Boolean jobOpening, Pageable pageable) {
        Page<Band> bandPage;

        // 검색 조건이 모두 비어있는 경우 (모든 밴드 조회)
        if (!StringUtils.hasText(keyword) && !StringUtils.hasText(sigun) && jobOpening == null) {
            bandPage = bandRepository.findAll(pageable);
        }
        // 검색 조건에 따른 조회
        else {
            // 키워드 준비 (검색에 사용할 형태로 변환)
            String searchKeyword = StringUtils.hasText(keyword) ? "%" + keyword.toLowerCase() + "%" : null;

            // 시군 준비
            String searchSigun = StringUtils.hasText(sigun) ? sigun : null;

            // 조건에 따른 검색 수행
            bandPage = bandRepository.searchBands(searchKeyword, searchSigun, jobOpening, pageable);
        }

        // 각 밴드에 대해 악기 보유 현황 설정
        List<BandDTO> bandDTOs = bandPage.getContent().stream()
                .map(band -> {
                    // 밴드의 모든 멤버 조회
                    List<BandMember> bandMembers = bandMemberRepository.getBandMembersByBand_BandId(band.getBandId());

                    // 밴드 엔티티에 멤버 정보 설정
                    band.updateBandMembers(bandMembers);

                    // BandDTO로 변환
                    BandDTO bandDTO = bandConverter.toBandDTO(band);

                    // 악기 보유 현황 설정
                    setInstrumentAvailability(bandDTO, bandMembers);

                    return bandDTO;
                })
                .collect(Collectors.toList());

        log.debug("Found {} bands matching the search criteria", bandPage.getTotalElements());
        return bandConverter.toPageBandDTO(bandPage, bandDTOs);
    }

    @Override
    public BandDTO updateBand(Long bandId, UpdateBandRequest updateBandRequest, UUID memberId) {
        // 밴드 존재 여부 확인
        Band band = getBand(bandId);

        // 요청자가 밴드 마스터인지 확인
        isBandMaster(bandId, memberId, band);

        // 필수 필드 검증
        if (updateBandRequest.getName() == null || updateBandRequest.getName().trim().isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.INVALID_REQUEST_PARAMETER);
        }

        // 밴드 정보 업데이트
        band.update(updateBandRequest);

        log.info("Band successfully updated. BandId: {}", bandId);
        return bandConverter.toBandDTO(band);
    }

    @Override
    public ContactDTO updateBandContact(Long bandId, Long contactId, CreateBandContactRequest request, UUID memberId) {
        // 밴드 존재 여부 확인
        Band band = getBand(bandId);

        // 요청자가 밴드 마스터인지 확인
        isBandMaster(bandId, memberId, band);

        // 연락처 존재 여부 확인
        Contact contact = contactRepository.findByContactIdAndBand_BandId(contactId, bandId)
                .orElseThrow(() -> {
                    log.warn("Contact not found with ID: {} for band ID: {}", contactId, bandId);
                    return new ExceptionHandler(ErrorStatus.CONTACT_NOT_FOUND);
                });

        // 연락처 정보 업데이트
        contact.update(request);
        Contact updatedContact = contactRepository.save(contact);

        log.info("Contact successfully updated. BandId: {}, ContactId: {}", bandId, contactId);
        return bandConverter.toContactDTO(updatedContact);
    }

    @Override
    public BandMemberDTO updateBandMemberInstrument(Long bandId, UUID memberId, Instrument instrument,
                                                    UUID requesterId) {
        // 밴드 존재 여부 확인
        Band band = getBand(bandId);

        // 요청자가 밴드 마스터인지 확인
        isBandMaster(bandId, memberId, band);

        // 해당 멤버가 밴드에 속해 있는지 확인
        BandMember bandMember = bandMemberRepository.findByBand_BandIdAndMember_MemberId(bandId, memberId)
                .orElseThrow(() -> {
                    log.warn("Member with ID: {} is not in band with ID: {}", memberId, bandId);
                    return new ExceptionHandler(ErrorStatus.MEMBER_NOT_IN_BAND);
                });

        // 악기 포지션 업데이트
        bandMember.updateInstrument(instrument);
        BandMember updatedBandMember = bandMemberRepository.save(bandMember);

        log.info("Band member instrument successfully updated. BandId: {}, MemberId: {}, Instrument: {}",
                bandId, memberId, instrument);
        return bandConverter.toBandMemberDTO(updatedBandMember);
    }

    @Override
    public ContactDTO createBandContact(Long bandId, CreateBandContactRequest request, UUID memberId) {
        // 밴드 존재 여부 확인
        Band band = getBand(bandId);

        // 요청자가 밴드 마스터인지 확인
        isBandMaster(bandId, memberId, band);

        // 입력 검증 - CUSTOM 유형이면 title이 필수
        if (request.getSns() != null && request.getSns().name().equals("CUSTOM") &&
                (request.getTitle() == null || request.getTitle().isEmpty())) {
            log.warn("Custom SNS type requires a title. BandId: {}", bandId);
            throw new ExceptionHandler(ErrorStatus.INVALID_REQUEST_PARAMETER);
        }

        // 새 연락처 생성 및 저장
        Contact newContact = new Contact(band, request.getSns(), request.getTitle(), request.getUrl());
        band.getContacts().add(newContact);

        bandRepository.save(band);

        log.info("New contact successfully added to band. BandId: {}, ContactId: {}",
                bandId, newContact.getContactId());
        return bandConverter.toContactDTO(newContact);
    }

    @Override
    public void deleteBandContact(Long bandId, Long contactId, UUID memberId) {
        // 밴드 존재 여부 확인
        Band band = getBand(bandId);

        // 요청자가 밴드 마스터인지 확인
        isBandMaster(bandId, memberId, band);

        // 연락처 존재 여부 및 밴드와의 관계 확인
        Contact contact = contactRepository.findByContactIdAndBand_BandId(contactId, bandId)
                .orElseThrow(() -> {
                    log.warn("Contact not found with ID: {} for band ID: {}", contactId, bandId);
                    return new ExceptionHandler(ErrorStatus.CONTACT_NOT_FOUND);
                });

        // 연락처 삭제
        contactRepository.delete(contact);

        log.info("Contact successfully deleted. BandId: {}, ContactId: {}", bandId, contactId);
    }

    public List<ContactDTO> getBandContacts(Long bandId) {
        // 밴드 존재 여부 확인
        isExistBand(bandId);

        // 밴드의 모든 연락처 정보 조회
        List<Contact> contacts = contactRepository.findAllByBand_BandId(bandId);

        log.debug("Found {} contacts for band with ID: {}", contacts.size(), bandId);
        return contacts.stream()
                .map(bandConverter::toContactDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BandDTO transferBandMaster(Long bandId, UUID newMasterId, UUID currentMasterId) {

        // 밴드 존재 여부 확인
        Band band = getBand(bandId);

        // 요청자가 밴드 마스터인지 확인
        isBandMaster(bandId, currentMasterId, band);

        // 새로운 마스터가 자기 자신인 경우 (불필요한 작업 방지)
        if (currentMasterId.equals(newMasterId)) {
            log.warn("Attempted to transfer band master to self. BandId: {}, MasterId: {}",
                    bandId, currentMasterId);
            throw new ExceptionHandler(ErrorStatus.INVALID_TRANSFER_REQUEST);
        }

        // 새로운 마스터가 밴드 멤버인지 확인
        if (!bandMemberRepository.existsByBand_BandIdAndMember_MemberId(bandId, newMasterId)) {
            log.warn("New master is not a band member. BandId: {}, NewMasterId: {}", bandId, newMasterId);
            throw new ExceptionHandler(ErrorStatus.MEMBER_NOT_IN_BAND);
        }

        // 밴드 마스터 권한 이전
        band.updateBandMaster(newMasterId);

        log.info("Band master successfully transferred. BandId: {}, OldMasterId: {}, NewMasterId: {}",
                bandId, currentMasterId, newMasterId);
        return bandConverter.toBandDTO(band);
    }

    // 밴드 존재 여부 확인
    private Band getBand(Long bandId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> {
                    log.warn("Band not found with ID: {}", bandId);
                    return new ExceptionHandler(ErrorStatus.BAND_NOT_FOUND);
                });
        return band;
    }

    private void isExistBand(Long bandId) {
        if (!bandRepository.existsById(bandId)) {
            log.warn("Band not found with ID: {}", bandId);
            throw new ExceptionHandler(ErrorStatus.BAND_NOT_FOUND);
        }
    }
}