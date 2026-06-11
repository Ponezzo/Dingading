package com.mickey.dinggading.domain.memberrank.model;

import com.mickey.dinggading.base.BaseEntity;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "song_by_instrument")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class SongByInstrument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_by_instrument_id")
    private Long songByInstrumentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_instrument_pack_id", nullable = false)
    private SongInstrumentPack songInstrumentPack;

    @Column(name = "song_by_instrument_ex_filename", nullable = false, length = 255)
    private String songByInstrumentExFilename;

    @Column(name = "song_by_instrument_filename", nullable = false)
    private String songByInstrumentFilename;

    @Column(name = "song_by_instrument_analysis_json", nullable = false)
    private String songByInstrumentAnalysisJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "instrument", nullable = false)
    private Instrument instrument;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false)
    private Tier tier;

    @OneToMany(mappedBy = "songByInstrument", cascade = CascadeType.ALL)
    private List<Attempt> attempts = new ArrayList<>();

    // 생성자
    private SongByInstrument(Song song,
                             Instrument instrument,
                             Tier tier,
                             String songByInstrumentExFilename,
                             String songByInstrumentFilename,
                             String songByInstrumentAnalysisJson,
                             SongInstrumentPack pack) {
        this.song = song;
        this.instrument = instrument;
        this.tier = tier;
        this.songByInstrumentExFilename = songByInstrumentExFilename;
        this.songByInstrumentFilename = songByInstrumentFilename;
        this.songByInstrumentAnalysisJson = songByInstrumentAnalysisJson;
        this.songInstrumentPack = pack;
    }

    // 팩토리 메소드
    public static SongByInstrument createSongByInstrument(Song song,
                                                          Instrument instrument,
                                                          Tier tier,
                                                          String songByInstrumentExFilename,
                                                          String songByInstrumentFilename,
                                                          String songByInstrumentAnalysisJson,
                                                          SongInstrumentPack pack) {
        if (song == null) {
            throw new IllegalArgumentException("곡은 필수입니다.");
        }
        if (instrument == null) {
            throw new IllegalArgumentException("악기는 필수입니다.");
        }
        if (tier == null) {
            throw new IllegalArgumentException("티어는 필수입니다.");
        }
        if (songByInstrumentExFilename == null || songByInstrumentExFilename.isEmpty()) {
            throw new IllegalArgumentException("악기 URL은 필수입니다.");
        }

        // 팩의 tier와 instrument가 일치하는지 확인
        if (pack.getSongPackTier() != tier || pack.getSongPackInstrument() != instrument) {
            throw new IllegalArgumentException("선택한 팩의 티어와 악기가 파라미터와 일치하지 않습니다.");
        }

        return SongByInstrument.builder()
                .song(song)
                .instrument(instrument)
                .tier(tier)
                .songByInstrumentFilename(songByInstrumentFilename)
                .songByInstrumentExFilename(songByInstrumentExFilename)
                .songByInstrumentAnalysisJson(songByInstrumentAnalysisJson)
                .songInstrumentPack(pack)
                .build();

    }

    // 악기 연주 URL 업데이트
    public void updateInstrumentUrl(String instrumentUrl) {
        if (instrumentUrl == null || instrumentUrl.isEmpty()) {
            throw new IllegalArgumentException("악기 URL은 필수입니다.");
        }
        this.songByInstrumentExFilename = instrumentUrl;
    }

    // 티어 난이도 변경
    public void changeTier(Tier tier) {
        if (tier == null) {
            throw new IllegalArgumentException("티어는 필수입니다.");
        }

        // 현재 속한 팩의 티어와 일치하는지 확인
        // 주의: 이는 설계 결정사항임. 팩의 티어와 곡의 티어가 항상 일치해야 하는지에 따라 달라짐
        if (this.songInstrumentPack != null && this.songInstrumentPack.getSongPackTier() != tier) {
            throw new IllegalStateException("곡의 티어 변경은 속한 팩의 티어와 일치해야 합니다.");
        }

        this.tier = tier;
    }

    // 특정 악기와 티어에 맞는 곡인지 확인
    public boolean matchesInstrumentAndTier(Instrument instrument, Tier tier) {
        return this.instrument == instrument && this.tier == tier;
    }

    // 연관관계 편의 메소드
    public void setSongInstrumentPack(SongInstrumentPack pack) {
        if (this.songInstrumentPack != null) {
            this.songInstrumentPack.getSongByInstruments().remove(this);
        }

        this.songInstrumentPack = pack;
        pack.getSongByInstruments().add(this);
    }
}