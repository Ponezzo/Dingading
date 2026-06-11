package com.mickey.dinggading.domain.member.repository;

import com.mickey.dinggading.domain.member.model.entity.Follow;
import com.mickey.dinggading.domain.member.model.entity.Member;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowingUserAndFollowedUser(Member followingUser, Member followedUser);

    @Query("SELECT f.followedUser FROM Follow f WHERE f.followingUser.memberId = :memberId")
    Page<Member> findFollowingsByMemberId(@Param("memberId") UUID memberId, Pageable pageable);

    @Query("SELECT f.followingUser FROM Follow f WHERE f.followedUser.memberId = :memberId")
    Page<Member> findFollowersByMemberId(@Param("memberId") UUID memberId, Pageable pageable);

    Optional<Follow> findByFollowingUserAndFollowedUser(Member followingUser, Member followedUser);

    @Query("SELECT COUNT(f1) > 0 FROM Follow f1, Follow f2 " +
            "WHERE f1.followingUser = :memberA AND f1.followedUser = :memberB " +
            "AND f2.followingUser = :memberB AND f2.followedUser = :memberA")
    boolean areFollowingEachOther(@Param("memberA") Member memberA, @Param("memberB") Member memberB);
}