package com.mickey.dinggading.domain.member.repository;

import com.mickey.dinggading.domain.member.model.entity.Member;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByMemberId(UUID memberId);

    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username);
}