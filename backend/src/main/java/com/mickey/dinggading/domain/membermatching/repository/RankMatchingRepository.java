package com.mickey.dinggading.domain.membermatching.repository;

import com.mickey.dinggading.domain.memberrank.model.Instrument;
import com.mickey.dinggading.domain.memberrank.model.RankMatching;
import com.mickey.dinggading.domain.memberrank.model.RankType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * RankMatching 엔티티에 대한 데이터 접근 인터페이스
 */
@Repository
public interface RankMatchingRepository extends JpaRepository<RankMatching, Long> {

    /**
     * 회원 ID로 랭크 매칭 목록을 조회
     *
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 랭크 매칭 페이지
     */
    @Query("SELECT rm FROM RankMatching rm JOIN rm.memberRank mr JOIN mr.member m WHERE m.memberId = :memberId")
    Page<RankMatching> findByMemberId(@Param("memberId") UUID memberId, Pageable pageable);

    /**
     * 회원 ID와 악기로 랭크 매칭 목록을 조회
     *
     * @param memberId   회원 ID
     * @param instrument 악기
     * @param pageable   페이징 정보
     * @return 랭크 매칭 페이지
     */
    @Query("SELECT rm FROM RankMatching rm JOIN rm.memberRank mr JOIN mr.member m WHERE m.memberId = :memberId AND rm.instrument = :instrument")
    Page<RankMatching> findByMemberIdAndInstrument(@Param("memberId") UUID memberId,
                                                   @Param("instrument") Instrument instrument,
                                                   Pageable pageable);

    /**
     * 회원 ID, 악기, 랭크 유형으로 랭크 매칭 목록을 조회
     *
     * @param memberId   회원 ID
     * @param instrument 악기
     * @param rankType   랭크 유형
     * @param pageable   페이징 정보
     * @return 랭크 매칭 페이지
     */
    @Query("SELECT rm FROM RankMatching rm JOIN rm.memberRank mr JOIN mr.member m WHERE m.memberId = :memberId AND rm.instrument = :instrument AND rm.rankType = :rankType")
    Page<RankMatching> findByMemberIdAndInstrumentAndRankType(@Param("memberId") UUID memberId,
                                                              @Param("instrument") Instrument instrument,
                                                              @Param("rankType") RankType rankType,
                                                              Pageable pageable);

    /**
     * 특정 회원이 현재 진행 중인 랭크 매칭을 조회
     *
     * @param memberId   회원 ID
     * @param instrument 악기
     * @return 진행 중인 랭크 매칭 목록
     */
    @Query("SELECT rm FROM RankMatching rm "
            + "JOIN rm.memberRank mr "
            + "JOIN mr.member m "
            + "WHERE m.memberId = :memberId "
            + "AND rm.instrument = :instrument "
            + "AND rm.status = 'IN_PROGRESS' "
            + "OR rm.status = 'ANALYZING' "
            + "ORDER BY rm.createdAt DESC ")
    List<RankMatching> findOngoingMatchingsByMemberIdAndInstrument(@Param("memberId") UUID memberId,
                                                                   @Param("instrument") Instrument instrument);

    /**
     * 특정 회원의 최근 랭크 매칭을 조회
     *
     * @param memberId   회원 ID
     * @param instrument 악기
     * @return 최근 랭크 매칭 Optional
     */
    @Query("SELECT rm FROM RankMatching rm JOIN rm.memberRank mr JOIN mr.member m WHERE m.memberId = :memberId AND rm.instrument = :instrument")
    Optional<RankMatching> findLatestMatchingByMemberIdAndInstrument(@Param("memberId") UUID memberId,
                                                                     @Param("instrument") Instrument instrument);

    /**
     * 만료일이 지난 진행 중인 랭크 매칭 목록을 조회
     *
     * @param currentDate 현재 날짜
     * @return 만료된 랭크 매칭 목록
     */
    @Query("SELECT rm FROM RankMatching rm WHERE rm.status = 'IN_PROGRESS' AND rm.expireDate < :currentDate")
    List<RankMatching> findExpiredMatchings(@Param("currentDate") LocalDate currentDate);

}