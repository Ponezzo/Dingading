package com.mickey.dinggading.domain.band.model.entity;

import com.mickey.dinggading.base.BaseEntity;
import com.mickey.dinggading.domain.memberrank.model.Song;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "BandRecruitmentSong")
public class BandRecruitmentSong extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "band_recruitment_song_id")
    private Long bandRecruitmentSongId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_recruitment_id")
    private BandRecruitment bandRecruitment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id")
    private Song song;
}