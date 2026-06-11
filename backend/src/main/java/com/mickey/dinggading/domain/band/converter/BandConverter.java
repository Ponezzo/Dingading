package com.mickey.dinggading.domain.band.converter;

import com.mickey.dinggading.domain.band.model.entity.Band;
import com.mickey.dinggading.domain.band.model.entity.BandMember;
import com.mickey.dinggading.domain.band.model.entity.Contact;
import com.mickey.dinggading.model.BandDTO;
import com.mickey.dinggading.model.BandMemberDTO;
import com.mickey.dinggading.model.ContactDTO;
import com.mickey.dinggading.model.PageBandDTO;
import com.mickey.dinggading.model.PageableDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class BandConverter {

    public BandDTO toBandDTO(Band band) {
        List<BandMemberDTO> bandMemberDTOs = band.getMembers().stream()
                .map(this::toBandMemberDTO)
                .collect(Collectors.toList());

        List<ContactDTO> contactDTOs = band.getContacts().stream()
                .map(this::toContactDTO)
                .collect(Collectors.toList());

        return BandDTO.builder()
                .bandId(band.getBandId())
                .name(band.getName())
                .bandMasterId(band.getBandMasterId())
                .description(band.getDescription())
                .sigun(band.getSigun())
                .tags(band.getTags())
                .profileUrl(band.getProfileUrl())
                .memberCount(band.getMembers().size())
                .maxSize(band.getMaxSize())
                .jobOpening(band.isJobOpening())
                .bandMember(bandMemberDTOs)
                .contact(contactDTOs)
                .build();
    }

    public PageBandDTO toPageBandDTO(Page<Band> bandPage, List<BandDTO> bandDTOs) {
        // PageableDTO의 protected 생성자를 우회하기 위해 빌더 패턴 사용
        PageableDTO pageableDTO = PageableDTO.builder()
                .page(bandPage.getNumber())
                .size(bandPage.getSize())
                .totalElements((int) bandPage.getTotalElements())
                .totalPages(bandPage.getTotalPages())
                .build();

        return PageBandDTO.builder()
                .content(bandDTOs)
                .pageable(pageableDTO)
                .build();
    }

    public BandMemberDTO toBandMemberDTO(BandMember bandMember) {
        return BandMemberDTO.builder()
                .bandMemberId(bandMember.getBandMemberId())
                .bandId(bandMember.getBand().getBandId())
                .memberId(bandMember.getMember().getMemberId())
                .instrument(bandMember.getInstrument())
                .build();
    }

    public ContactDTO toContactDTO(Contact contact) {
        return ContactDTO.builder()
                .contactId(contact.getContactId())
                .bandId(contact.getBand().getBandId())
                .sns(contact.getSns())
                .title(contact.getTitle())
                .url(contact.getUrl())
                .build();
    }
}
