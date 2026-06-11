package com.mickey.dinggading.domain.membermatching.repository;

import com.mickey.dinggading.domain.memberrank.model.MemberTierLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * MemberTierLog 엔티티에 대한 데이터 접근 인터페이스
 */
@Repository
public interface MemberTierLogRepository extends JpaRepository<MemberTierLog, Long> {

//    /**
//     * 회원 랭크 ID로 티어 변경 이력을 페이징 조회
//     *
//     * @param memberRankId 회원 랭크 ID
//     * @param pageable     페이징 정보
//     * @return 티어 변경 이력 페이지
//     */
//    Page<MemberTierLog> findByMemberRankMemberRankId(Long memberRankId, Pageable pageable);
//
//    /**
//     * 회원 ID로 모든 악기에 대한 티어 변경 이력을 페이징 조회
//     *
//     * @param memberId 회원 ID
//     * @param pageable 페이징 정보
//     * @return 티어 변경 이력 페이지
//     */
//    @Query("SELECT mtl FROM MemberTierLog mtl JOIN mtl.memberRank mr WHERE mr.memberId = :memberId")
//    Page<MemberTierLog> findByMemberId(@Param("memberId") UUID memberId, Pageable pageable);
//
//    /**
//     * 회원 ID와 악기로 티어 변경 이력을 페이징 조회
//     *
//     * @param memberId   회원 ID
//     * @param instrument 악기
//     * @param pageable   페이징 정보
//     * @return 티어 변경 이력 페이지
//     */
//    @Query("SELECT mtl FROM MemberTierLog mtl JOIN mtl.memberRank mr " +
//            "WHERE mr.memberId = :memberId AND mr.instrument = :instrument")
//    Page<MemberTierLog> findByMemberIdAndInstrument(@Param("memberId") UUID memberId,
//                                                    @Param("instrument") String instrument,
//                                                    Pageable pageable);
}