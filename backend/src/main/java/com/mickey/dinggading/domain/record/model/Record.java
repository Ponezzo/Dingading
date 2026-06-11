package com.mickey.dinggading.domain.record.model;

import com.mickey.dinggading.base.BaseEntity;
import com.mickey.dinggading.domain.member.model.entity.Member;
import com.mickey.dinggading.domain.memberrank.model.Attempt;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Record extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id", nullable = false)
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id")
    private Attempt attempt;

    @Enumerated(EnumType.STRING)
    @Column(name = "dtype", nullable = false)
    private ChallengeType dtype;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "record_url", nullable = false, length = 255)
    private String recordUrl;

    /**
     * 새로운 녹음 레코드를 생성합니다.
     *
     * @param member    녹음을 생성한 회원
     * @param attempt   연관된 도전 (없을 수 있음)
     * @param dtype     녹음 유형 (CHALLENGE, LIVE_HOUSE, PRACTICE)
     * @param title     녹음 제목
     * @param recordUrl 녹음 파일 URL
     * @return 생성된 Record 엔티티
     */
    public static Record createRecord(Member member, Attempt attempt, ChallengeType dtype, String title,
                                      String recordUrl) {
        validateRecordInput(member, dtype, title, recordUrl);

        return Record.builder()
                .member(member)
                .attempt(attempt)
                .dtype(dtype)
                .title(title)
                .recordUrl(recordUrl)
                .build();
    }

    /**
     * 녹음 입력값을 검증합니다.
     */
    private static void validateRecordInput(Member member, ChallengeType dtype, String title, String recordUrl) {
        if (member == null) {
            throw new IllegalArgumentException("회원 정보가 없습니다.");
        }
        if (dtype == null) {
            throw new IllegalArgumentException("녹음 유형이 없습니다.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("녹음 제목이 없습니다.");
        }
        if (recordUrl == null || recordUrl.isBlank()) {
            throw new IllegalArgumentException("녹음 URL이 없습니다.");
        }
    }

    /**
     * 녹음 정보를 수정합니다.
     *
     * @param title 수정할 제목
     * @return 수정된 Record 엔티티
     */
    public Record updateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("녹음 제목이 없습니다.");
        }
        this.title = title;
        return this;
    }

    /**
     * 녹음 URL을 업데이트합니다.
     *
     * @param recordUrl 새 녹음 파일 URL
     * @return 수정된 Record 엔티티
     */
    public Record updateRecordUrl(String recordUrl) {
        if (recordUrl == null || recordUrl.isBlank()) {
            throw new IllegalArgumentException("녹음 URL이 없습니다.");
        }
        this.recordUrl = recordUrl;
        return this;
    }
}