package com.mickey.dinggading.domain.band.service;

import com.mickey.dinggading.model.AddBandMemberRequest;
import com.mickey.dinggading.model.BandDTO;
import com.mickey.dinggading.model.BandMemberDTO;
import com.mickey.dinggading.model.ContactDTO;
import com.mickey.dinggading.model.CreateBandContactRequest;
import com.mickey.dinggading.model.CreateBandRequest;
import com.mickey.dinggading.model.Instrument;
import com.mickey.dinggading.model.PageBandDTO;
import com.mickey.dinggading.model.UpdateBandRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface BandService {

    // 밴드 관련 기능
    BandDTO createBand(CreateBandRequest request, UUID memberId);

    List<BandMemberDTO> addBandMember(UUID memberId, Long bandId, AddBandMemberRequest addBandMemberRequest);

    void deleteBand(Long bandId, UUID currentMemberId);

    PageBandDTO getAllBands(Pageable pageable);

    BandDTO getBandById(Long bandId);

    List<BandMemberDTO> getBandMembers(Long bandId, Pageable pageable);

    void removeBandMember(Long bandId, UUID memberIdToRemove, UUID requesterId);

    PageBandDTO searchBands(String keyword, String sigun, Boolean jobOpening, Pageable pageable);

    BandDTO updateBand(Long bandId, UpdateBandRequest updateBandRequest, UUID requesterId);

    ContactDTO updateBandContact(Long bandId, Long contactId, CreateBandContactRequest request, UUID memberId);

    BandMemberDTO updateBandMemberInstrument(Long bandId, UUID memberId, Instrument instrument, UUID requesterId);

    ContactDTO createBandContact(Long bandId, CreateBandContactRequest request, UUID memberId);

    void deleteBandContact(Long bandId, Long contactId, UUID memberId);

    List<ContactDTO> getBandContacts(Long bandId);

    BandDTO transferBandMaster(Long bandId, UUID newMasterId, UUID currentMasterId);
}