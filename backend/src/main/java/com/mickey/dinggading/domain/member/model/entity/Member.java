package com.mickey.dinggading.domain.member.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mickey.dinggading.base.BaseEntity;
import com.mickey.dinggading.domain.memberrank.model.Instrument;
import com.mickey.dinggading.domain.memberrank.model.MemberRank;
import com.mickey.dinggading.domain.record.model.Record;
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
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "member_id")
    //@Column(name = "member_id", columnDefinition = "BINARY(16)")
    private UUID memberId;

    @Column(name = "username", unique = true, nullable = false)
    // "Google OAuth 로 받아온 이메일이 로그인 아이디"
    @Comment("사용자 로그인 아이디")
    private String username;

    @Column(name = "nickname", nullable = false)
    @Comment("사용자 닉네임 (자동 생성)")
    private String nickname;

    @Column(name = "favorite_band_id")
    @Comment("즐겨찾기 된 밴드")
    private Long favoriteBandId;

    @Column(name = "profile_img_url", nullable = true)
    @Comment("프로필 이미지")
    private String profileImgUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberRank> memberRanks = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Record> records = new ArrayList<>();

    @OneToMany(mappedBy = "followingUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> following = new ArrayList<>();

    @OneToMany(mappedBy = "followedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> sentNotifications = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> receivedNotifications = new ArrayList<>();

    public static Member createMember(String username, String nickname, String profileUrl) {
        // TODO: 최적화 포인트 -> Event를 발생시켜서 의존성 분리

        Member member = Member.builder()
                .username(username)
                .nickname(nickname)
                .favoriteBandId(null) // null 가능
                .profileImgUrl(profileUrl)
                .memberRanks(new ArrayList<>())
                .records(new ArrayList<>())
                .following(new ArrayList<>())
                .followers(new ArrayList<>())
                .sentNotifications(new ArrayList<>())
                .receivedNotifications(new ArrayList<>())
                .build();

        for (Instrument instrument : Instrument.values()) {
            MemberRank memberRank = MemberRank.createMemberRank(instrument);
            memberRank.updateMember(member);
        }

        return member;
    }

    // Update methods
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public void updateFavoriteBand(Long favoriteBandId) {
        this.favoriteBandId = favoriteBandId;
    }

}