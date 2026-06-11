package com.mickey.dinggading.domain.band.model.entity;

import com.mickey.dinggading.base.BaseEntity;
import com.mickey.dinggading.model.CreateBandContactRequest;
import com.mickey.dinggading.model.Sns;
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
@Table(name = "Contact")
public class Contact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private Long contactId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_id", nullable = false)
    private Band band;

    @Enumerated(EnumType.STRING)
    @Column(name = "sns", nullable = false)
    private Sns sns;

    @Column(name = "title")
    private String title;

    @Column(name = "URL", nullable = false)
    private String url;

    public Contact(Band band, Sns sns, String title, String url) {
        this.band = band;
        this.sns = sns;
        this.title = title;
        this.url = url;

        if (band != null && !band.getContacts().contains(this)) {
            band.getContacts().add(this);
        }
    }

    public void update(CreateBandContactRequest request) {
        if (request.getSns() != null) {
            this.sns = request.getSns();
        }
        if (request.getTitle() != null) {
            this.title = request.getTitle();
        }
        if (request.getUrl() != null) {
            this.url = request.getUrl();
        }
    }
}