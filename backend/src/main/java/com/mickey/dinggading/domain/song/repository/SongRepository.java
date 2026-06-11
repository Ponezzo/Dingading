package com.mickey.dinggading.domain.song.repository;

import com.mickey.dinggading.domain.memberrank.model.Instrument;
import com.mickey.dinggading.domain.memberrank.model.Song;
import com.mickey.dinggading.domain.memberrank.model.Tier;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    // 특정 악기가 포함된 곡 찾기
    @Query("SELECT DISTINCT s FROM Song s JOIN s.songByInstruments sbi WHERE sbi.instrument = :instrument")
    Page<Song> findAllByInstrument(@Param("instrument") Instrument instrument, Pageable pageable);

    // 특정 티어가 포함된 곡 찾기
    @Query("SELECT DISTINCT s FROM Song s JOIN s.songByInstruments sbi WHERE sbi.tier = :tier")
    Page<Song> findAllByTier(@Param("tier") Tier tier, Pageable pageable);

    // 제목으로 검색
    Page<Song> findByTitleContaining(String title, Pageable pageable);

    /**
     * 특정 곡을 연관된 SongByInstrument 정보와 함께 조회
     *
     * @param songId 조회할 곡 ID
     * @return 연관 정보가 포함된 Song 엔티티
     */
    @Query("SELECT s FROM Song s LEFT JOIN FETCH s.songByInstruments WHERE s.songId = :songId")
    Optional<Song> findByIdWithInstruments(@Param("songId") Long songId);

    /**
     * 특정 악기와 티어에 해당하는 곡 목록을 페이징하여 조회
     *
     * @param instrument 악기 타입
     * @param tier       티어 등급
     * @param pageable   페이징 정보
     * @return 조건에 맞는 곡 목록
     */
    @Query("SELECT DISTINCT s FROM Song s JOIN s.songByInstruments sbi " +
            "WHERE sbi.instrument = :instrument AND sbi.tier = :tier")
    Page<Song> findByInstrumentAndTier(@Param("instrument") Instrument instrument,
                                       @Param("tier") Tier tier,
                                       Pageable pageable);

}