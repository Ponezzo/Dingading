package com.mickey.dinggading.domain.memberrank.model;

import com.mickey.dinggading.base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "song")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Song extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private Long songId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "artist", nullable = false, length = 255)
    private String artist;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "youtube_url", nullable = false, length = 255)
    private String youtubeUrl;

    @Column(name = "song_filename", nullable = false, length = 255)
    private String songFilename;

    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SongByInstrument> songByInstruments = new ArrayList<>();

    // 곡 정보 업데이트
    public void updateSongInfo(String title, String description, String youtubeUrl) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("곡 제목은 필수입니다.");
        }
        if (youtubeUrl == null || youtubeUrl.isEmpty()) {
            throw new IllegalArgumentException("유튜브 URL은 필수입니다.");
        }

        this.title = title;
        this.description = description;
        this.youtubeUrl = youtubeUrl;
    }

    // 특정 악기의 모든 버전 조회
    public List<SongByInstrument> getVersionsByInstrument(Instrument instrument) {
        return songByInstruments.stream()
                .filter(sbi -> sbi.getInstrument() == instrument)
                .collect(Collectors.toList());
    }

    // 특정 티어의 모든 버전 조회
    public List<SongByInstrument> getVersionsByTier(Tier tier) {
        return songByInstruments.stream()
                .filter(sbi -> sbi.getTier() == tier)
                .collect(Collectors.toList());
    }

    // 특정 악기 버전 제거
    public void removeInstrumentVersion(SongByInstrument songByInstrument) {
        songByInstruments.remove(songByInstrument);
    }
}