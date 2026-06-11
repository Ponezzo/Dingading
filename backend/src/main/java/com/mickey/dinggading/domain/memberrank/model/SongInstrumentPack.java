package com.mickey.dinggading.domain.memberrank.model;

import com.mickey.dinggading.base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "song_instrument_pack")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class SongInstrumentPack extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_instrument_pack_id")
    private Long songInstrumentPackId;

    @Column(name = "pack_name", nullable = false, length = 255)
    private String packName;

    @Enumerated(EnumType.STRING)
    @Column(name = "song_pack_tier", nullable = false)
    private Tier songPackTier;

    @Enumerated(EnumType.STRING)
    @Column(name = "song_pack_instrument", nullable = false)
    private Instrument songPackInstrument;

    @OneToMany(mappedBy = "songInstrumentPack", cascade = CascadeType.ALL)
    private List<SongByInstrument> songByInstruments = new ArrayList<>();

    @OneToMany(mappedBy = "songInstrumentPack", cascade = CascadeType.ALL)
    private List<RankMatching> rankMatchings = new ArrayList<>();

    // 생성자
    private SongInstrumentPack(String packName, Tier tier, Instrument instrument) {
        this.packName = packName;
        this.songPackTier = tier;
        this.songPackInstrument = instrument;
    }

    // 팩토리 메소드
    public static SongInstrumentPack createPack(String packName, Tier tier, Instrument instrument) {
        if (packName == null || packName.isEmpty()) {
            throw new IllegalArgumentException("팩 이름은 필수입니다.");
        }
        if (tier == null) {
            throw new IllegalArgumentException("티어는 필수입니다.");
        }
        if (instrument == null) {
            throw new IllegalArgumentException("악기는 필수입니다.");
        }

        return new SongInstrumentPack(packName, tier, instrument);
    }

    // 곡 추가 메소드
    public void addSong(SongByInstrument song) {
        if (song == null) {
            throw new IllegalArgumentException("추가할 곡이 null입니다.");
        }

        // 곡의 티어와 악기가 팩의 것과 일치하는지 확인
        if (song.getTier() != this.songPackTier || song.getInstrument() != this.songPackInstrument) {
            throw new IllegalArgumentException(
                    "곡의 티어(" + song.getTier() + ")와 악기(" + song.getInstrument() + ")가 " +
                            "팩의 티어(" + this.songPackTier + ")와 악기(" + this.songPackInstrument + ")와 일치하지 않습니다."
            );
        }

        // 팩에 이미 5개의 곡이 있는지 확인
        if (songByInstruments.size() >= 5) {
            throw new IllegalStateException("한 팩에는 최대 5개의 곡만 추가할 수 있습니다.");
        }

        // 이미 같은 곡이 있는지 확인
        boolean songExists = songByInstruments.stream()
                .anyMatch(s -> s.getSong().getSongId().equals(song.getSong().getSongId()));

        if (songExists) {
            throw new IllegalStateException("이 팩에 이미 동일한 곡이 존재합니다.");
        }

        // 양방향 연관관계 설정
        songByInstruments.add(song);
        song.setSongInstrumentPack(this);
    }

    public boolean hasSongByInstrument(SongByInstrument songByInstrument) {
        return songByInstruments.contains(songByInstrument);
    }

    // 팩 정보 업데이트
    public void updatePackInfo(String packName) {
        if (packName == null || packName.isEmpty()) {
            throw new IllegalArgumentException("팩 이름은 필수입니다.");
        }
        this.packName = packName;
    }

    // 팩에 포함된 곡 수 확인
    public int getSongCount() {
        return songByInstruments.size();
    }

    // 랭크 매칭에 적합한 팩인지 확인 (5개의 곡이 포함되어야 함)
    public boolean isValidForRankMatching() {
        return songByInstruments.size() == 5;
    }

    // 특정 티어와 악기에 맞는 팩인지 확인
    public boolean matchesTierAndInstrument(Tier tier, Instrument instrument) {
        return this.songPackTier == tier && this.songPackInstrument == instrument;
    }

    // 팩에서 곡 제거
    public void removeSong(SongByInstrument song) {
        songByInstruments.remove(song);
    }
}