package com.mickey.dinggading.domain.membermatching.repository;

import com.mickey.dinggading.domain.memberrank.model.Attempt;
import com.mickey.dinggading.domain.memberrank.model.GameType;
import com.mickey.dinggading.domain.memberrank.model.Instrument;
import com.mickey.dinggading.domain.memberrank.model.RankType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Attempt 엔티티에 대한 데이터 접근 인터페이스
 */
@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {

    // JPQL로 패치 조인 작성
    @Query("SELECT a FROM Attempt a " +
            "JOIN FETCH a.songByInstrument sbi " +
            "JOIN FETCH sbi.song s " +
            "WHERE a.attemptId = :attemptId")
    Optional<Attempt> findByIdWithSongDetails(@Param("attemptId") Long attemptId);

    /**
     * 회원 ID로 게임 유형(PRACTICE/RANK)에 따른 시도 목록을 페이징 조회
     *
     * @param memberId 회원 ID
     * @param gameType 게임 유형
     * @param pageable 페이징 정보
     * @return 시도 페이지
     */
    @Query("SELECT a FROM Attempt a\n"
            + "JOIN a.rankMatching rm\n"
            + "JOIN rm.memberRank mr\n"
            + "JOIN mr.member m\n"
            + "WHERE m.memberId = :memberId\n"
            + "AND a.gameType = :gameType\n")
    Page<Attempt> findByMemberIdAndGameType(
            @Param("memberId") UUID memberId,
            @Param("gameType") GameType gameType,
            Pageable pageable);

    /**
     * 회원 ID, 게임 유형, 악기로 시도 목록을 페이징 조회
     *
     * @param memberId   회원 ID
     * @param gameType   게임 유형
     * @param instrument 악기
     * @param pageable   페이징 정보
     * @return 시도 페이지
     */
    @Query("SELECT a FROM Attempt a\n"
            + "JOIN a.rankMatching rm\n"
            + "JOIN rm.memberRank mr\n"
            + "JOIN mr.member m\n"
            + "WHERE m.memberId = :memberId\n"
            + "AND a.gameType = :gameType\n"
            + "AND rm.instrument = :instrument\n")
    Page<Attempt> findByMemberIdAndGameTypeAndInstrument(@Param("memberId") UUID memberId,
                                                         @Param("gameType") GameType gameType,
                                                         @Param("instrument") Instrument instrument,
                                                         Pageable pageable);

    /**
     * 회원 ID, 게임 유형, 악기, 랭크 유형으로 시도 목록을 페이징 조회
     *
     * @param memberId   회원 ID
     * @param gameType   게임 유형
     * @param instrument 악기
     * @param rankType   랭크 유형
     * @param pageable   페이징 정보
     * @return 시도 페이지
     */
    @Query("SELECT a FROM Attempt a\n"
            + "JOIN a.rankMatching rm\n"
            + "JOIN rm.memberRank mr\n"
            + "JOIN mr.member m\n"
            + "WHERE m.memberId = :memberId\n"
            + "AND a.gameType = :gameType\n"
            + "AND rm.instrument = :instrument\n"
            + "AND a.rankType = :rankType\n")
    Page<Attempt> findByMemberIdAndGameTypeAndInstrumentAndRankType(@Param("memberId") UUID memberId,
                                                                    @Param("gameType") GameType gameType,
                                                                    @Param("instrument") Instrument instrument,
                                                                    @Param("rankType") RankType rankType,
                                                                    Pageable pageable);

}