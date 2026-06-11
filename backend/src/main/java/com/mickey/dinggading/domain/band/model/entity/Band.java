package com.mickey.dinggading.domain.band.model.entity;

import com.mickey.dinggading.base.BaseEntity;
import com.mickey.dinggading.model.CreateBandRequest;
import com.mickey.dinggading.model.UpdateBandRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Band extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "band_id")
    private Long bandId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "band_master_id", nullable = false)
    private UUID bandMasterId;

    @Column(name = "description")
    private String description;

    @Column(name = "sigun", nullable = false)
    private String sigun;

    @Column(name = "tags")
    private String tags;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "max_size")
    private Integer maxSize;

    @Column(name = "job_opening", nullable = false)
    private boolean jobOpening;

    @Builder.Default
    @OneToMany(mappedBy = "band", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "band", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BandMember> members = new ArrayList<>();

    public Band(CreateBandRequest request, UUID masterId) {
        if (masterId != null) {
            this.bandMasterId = masterId;
        }
        if (request.getName() != null) {
            this.name = request.getName();
        }
        if (request.getDescription() != null) {
            this.description = request.getDescription();
        }
        if (request.getSigun() != null) {
            this.sigun = request.getSigun();
        }
        if (request.getTags() != null) {
            this.tags = request.getTags();
        }
        if (request.getProfileUrl() != null) {
            this.profileUrl = request.getProfileUrl();
        }
        if (request.getMaxSize() != null) {
            this.maxSize = request.getMaxSize();
        }
        if (request.getJobOpening() != null) {
            this.jobOpening = request.getJobOpening();
        }
        this.contacts = new ArrayList<>();
        this.members = new ArrayList<>();
    }

    public void updateBandMembers(List<BandMember> bandMemberList) {
        this.members.clear();
        this.members.addAll(bandMemberList);
    }

    public void updateBandMaster(UUID newMasterId) {
        this.bandMasterId = newMasterId;
    }

    public void updateContacts(List<Contact> contactList) {
        this.contacts.clear();
        this.contacts.addAll(contactList);
    }

    public void update(UpdateBandRequest request) {
        if (request.getName() != null) {
            this.name = request.getName();
        }
        if (request.getDescription() != null) {
            this.description = request.getDescription();
        }
        if (request.getSigun() != null) {
            this.sigun = request.getSigun();
        }
        if (request.getTags() != null) {
            this.tags = request.getTags();
        }
        if (request.getProfileUrl() != null) {
            this.profileUrl = request.getProfileUrl();
        }
        if (request.getMaxSize() != null) {
            this.maxSize = request.getMaxSize();
        }
        if (request.getJobOpening() != null) {
            this.jobOpening = request.getJobOpening();
        }
    }
}