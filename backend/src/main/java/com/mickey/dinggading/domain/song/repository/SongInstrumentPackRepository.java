package com.mickey.dinggading.domain.song.repository;

import com.mickey.dinggading.domain.memberrank.model.Instrument;
import com.mickey.dinggading.domain.memberrank.model.SongInstrumentPack;
import com.mickey.dinggading.domain.memberrank.model.Tier;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SongInstrumentPackRepository extends JpaRepository<SongInstrumentPack, Long> {
    // 티어와 악기로 팩 찾기
    Optional<SongInstrumentPack> findBySongPackTierAndSongPackInstrument(Tier tier, Instrument instrument);

    // 티어로 팩 찾기
    Page<SongInstrumentPack> findAllBySongPackTier(Tier tier, Pageable pageable);

    // 악기로 팩 찾기
    Page<SongInstrumentPack> findAllBySongPackInstrument(Instrument instrument, Pageable pageable);

    /**
     * 악기와 티어로 곡 팩 목록을 조회
     *
     * @param instrument 악기
     * @param tier       티어
     * @return 곡 팩 목록
     */
    @Query("SELECT sip FROM SongInstrumentPack sip WHERE sip.songPackInstrument = :instrument AND sip.songPackTier = :tier")
    List<SongInstrumentPack> findByInstrumentAndTier(
            @Param("instrument") Instrument instrument,
            @Param("tier") Tier tier);

    /**
     * 악기와 티어 목록으로 곡 팩 목록을 조회
     *
     * @param instrument 악기
     * @param tiers      티어 목록
     * @return 곡 팩 목록
     */
    @Query("SELECT sip FROM SongInstrumentPack sip WHERE sip.songPackInstrument = :instrument AND sip.songPackTier IN :tiers")
    List<SongInstrumentPack> findByInstrumentAndTierIn(@Param("instrument") Instrument instrument,
                                                       @Param("tiers") List<Tier> tiers);

    /**
     * 특정 악기에 해당하는 모든 팩 조회 (페이징)
     *
     * @param instrument 악기 타입
     * @param pageable   페이징 정보
     * @return 해당 악기의 모든 팩 목록
     */
    Page<SongInstrumentPack> findBySongPackInstrument(Instrument instrument, Pageable pageable);

    /**
     * 특정 티어에 해당하는 모든 팩 조회 (페이징)
     *
     * @param tier     티어 등급
     * @param pageable 페이징 정보
     * @return 해당 티어의 모든 팩 목록
     */
    Page<SongInstrumentPack> findBySongPackTier(Tier tier, Pageable pageable);

    /**
     * 특정 악기와 티어에 해당하는 모든 팩 조회
     *
     * @param instrument 악기 타입
     * @param tier       티어 등급
     * @return 조건에 맞는 모든 팩 목록
     */
    List<SongInstrumentPack> findBySongPackInstrumentAndSongPackTier(Instrument instrument, Tier tier);

    /**
     * ID로 팩을 연관된 곡 정보와 함께 조회
     *
     * @param id 조회할 팩 ID
     * @return 연관 정보가 포함된 SongInstrumentPack 엔티티
     */
    @Query("SELECT p FROM SongInstrumentPack p LEFT JOIN FETCH p.songByInstruments WHERE p.songInstrumentPackId = :id")
    Optional<SongInstrumentPack> findByIdWithSongs(@Param("id") Long id);

    /**
     * 랭크 매칭에 유효한 팩만 조회 (곡이 5개 포함된 팩)
     *
     * @param pageable 페이징 정보
     * @return 랭크 매칭에 유효한 팩 목록
     */
    @Query("SELECT p FROM SongInstrumentPack p WHERE SIZE(p.songByInstruments) = 5")
    Page<SongInstrumentPack> findValidPacksForRankMatching(Pageable pageable);
}