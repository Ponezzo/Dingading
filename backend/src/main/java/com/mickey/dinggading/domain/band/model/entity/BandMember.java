package com.mickey.dinggading.domain.band.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mickey.dinggading.base.BaseEntity;
import com.mickey.dinggading.domain.member.model.entity.Member;
import com.mickey.dinggading.model.Instrument;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BandMember")
public class BandMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "band_member_id")
    private Long bandMemberId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_id", nullable = false)
    private Band band;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "instrument")
    private Instrument instrument;

    public BandMember(Band band, Member member, Instrument instrument) {
        this.band = band;
        this.member = member;
        this.instrument = instrument;
        if (band != null) {
            band.getMembers().add(this); // 양방향 연관관계 설정
        }
    }

    // 악기 업데이트 메소드
    public void updateInstrument(Instrument instrument) {
        this.instrument = instrument;
    }
}