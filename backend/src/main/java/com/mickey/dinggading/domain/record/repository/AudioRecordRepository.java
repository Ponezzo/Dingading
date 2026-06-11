package com.mickey.dinggading.domain.record.repository;

import com.mickey.dinggading.domain.record.model.ChallengeType;
import com.mickey.dinggading.domain.record.model.Record;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioRecordRepository extends JpaRepository<Record, Long> {

    /**
     * 특정 도전에 대한 녹음을 조회합니다.
     *
     * @param attemptId 도전 ID
     * @return 녹음 정보
     */
    @Query("SELECT r FROM Record r WHERE r.attempt.attemptId = :attemptId")
    Optional<Record> findByAttemptRecordId(@Param("attemptId") Long attemptId);

    /**
     * 특정 회원의 녹음 목록을 조회합니다.
     *
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 회원별 녹음 목록
     */
    Page<Record> findByMember_MemberId(UUID memberId, Pageable pageable);

    /**
     * 특정 녹음 유형의 녹음 목록을 조회합니다.
     *
     * @param dtype    녹음 유형
     * @param pageable 페이징 정보
     * @return 유형별 녹음 목록
     */
    Page<Record> findByDtype(ChallengeType dtype, Pageable pageable);

    /**
     * 특정 회원과 녹음 유형에 맞는 녹음 목록을 조회합니다.
     *
     * @param memberId 회원 ID
     * @param dtype    녹음 유형
     * @param pageable 페이징 정보
     * @return 필터링된 녹음 목록
     */
    Page<Record> findByMember_MemberIdAndDtype(UUID memberId, ChallengeType dtype, Pageable pageable);
}