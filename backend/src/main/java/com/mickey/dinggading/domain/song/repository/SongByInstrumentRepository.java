package com.mickey.dinggading.domain.song.repository;

import com.mickey.dinggading.domain.memberrank.model.Instrument;
import com.mickey.dinggading.domain.memberrank.model.SongByInstrument;
import com.mickey.dinggading.domain.memberrank.model.Tier;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SongByInstrumentRepository extends JpaRepository<SongByInstrument, Long> {
    List<SongByInstrument> findBySong_SongId(Long songId);

    // 특정 곡에 특정 악기 버전이 이미 존재하는지 확인
    boolean existsBySong_SongIdAndInstrument(Long songId, Instrument instrument);

    // 특정 곡과 특정 티어와 악기 조합이 이미 존재하는지 확인
    boolean existsBySong_SongIdAndInstrumentAndTier(Long songId, Instrument instrument, Tier tier);

    /**
     * 특정 곡의 모든 악기별 버전 조회
     *
     * @param songId 곡 ID
     * @return 해당 곡의 모든 악기별 버전 목록
     */
    List<SongByInstrument> findBySongSongId(Long songId);

    /**
     * 특정 곡의 특정 악기 버전 조회
     *
     * @param songId     곡 ID
     * @param instrument 악기 타입
     * @return 해당 곡의 해당 악기 버전 목록
     */
    List<SongByInstrument> findBySongSongIdAndInstrument(Long songId, Instrument instrument);

    /**
     * 특정 곡의 특정 티어 버전 조회
     *
     * @param songId 곡 ID
     * @param tier   티어 등급
     * @return 해당 곡의 해당 티어 버전 목록
     */
    List<SongByInstrument> findBySongSongIdAndTier(Long songId, Tier tier);

    /**
     * ID로 곡 버전을 연관 정보(Song, SongInstrumentPack)와 함께 조회
     *
     * @param id 조회할 SongByInstrument ID
     * @return 연관 정보가 포함된 SongByInstrument 엔티티
     */
    @Query("SELECT sbi FROM SongByInstrument sbi JOIN FETCH sbi.song JOIN FETCH sbi.songInstrumentPack WHERE sbi.songByInstrumentId = :id")
    Optional<SongByInstrument> findByIdWithDetails(@Param("id") Long id);

    /**
     * 특정 팩에 속한 모든 곡 버전 조회
     *
     * @param packId 팩 ID
     * @return 해당 팩에 속한 모든 곡 버전 목록
     */
    List<SongByInstrument> findBySongInstrumentPackSongInstrumentPackId(Long packId);

    /**
     * 특정 악기와 티어에 해당하는 모든 곡 버전 조회
     *
     * @param instrument 악기 타입
     * @param tier       티어 등급
     * @return 조건에 맞는 모든 곡 버전 목록
     */
    List<SongByInstrument> findByInstrumentAndTier(Instrument instrument, Tier tier);

}