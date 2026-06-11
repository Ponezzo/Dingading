package com.mickey.dinggading.domain.band.model.entity;

import com.mickey.dinggading.base.BaseEntity;
import com.mickey.dinggading.domain.memberrank.model.Song;
import com.mickey.dinggading.model.CreateBandRecruitmentRequest;
import com.mickey.dinggading.model.RecruitmentStatus;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "BandRecruitment")
public class BandRecruitment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "band_recruitment_id")
    private Long bandRecruitmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_id", nullable = false)
    private Band band;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "audition_date", nullable = false)
    private LocalDateTime auditionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audition_song_id", nullable = false)
    private Song auditionSong;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RecruitmentStatus status;

    @OneToMany(mappedBy = "bandRecruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BandRecruitmentInstrument> instruments = new ArrayList<>();

    public static BandRecruitment create(CreateBandRecruitmentRequest request, Band band, Song auditionSong) {
        return BandRecruitment.builder()
                .band(band)
                .title(request.getTitle())
                .description(request.getDescription())
                .auditionDate(request.getAuditionDate())
                .auditionSong(auditionSong)
                .status(RecruitmentStatus.READY) // 초기 상태는 항상 READY
                .build();
    }

    public void updateStatus(RecruitmentStatus status) {
        this.status = status;
    }

    public void update(CreateBandRecruitmentRequest request, Song auditionSong) {
        if (request.getTitle() != null) {
            this.title = request.getTitle();
        }
        if (request.getDescription() != null) {
            this.description = request.getDescription();
        }
        if (request.getAuditionDate() != null) {
            this.auditionDate = request.getAuditionDate();
        }
        if (auditionSong != null) {
            this.auditionSong = auditionSong;
        }
    }

    public void addInstrument(BandRecruitmentInstrument instrument) {
        this.instruments.add(instrument);
        instrument.setBandRecruitment(this);
    }

    public void removeInstrument(BandRecruitmentInstrument instrument) {
        this.instruments.remove(instrument);
        instrument.setBandRecruitment(null);
    }
}