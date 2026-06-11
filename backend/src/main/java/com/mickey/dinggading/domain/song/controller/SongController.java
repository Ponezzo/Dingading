package com.mickey.dinggading.domain.song.controller;

import com.mickey.dinggading.api.SongApi;
import com.mickey.dinggading.domain.memberrank.model.Instrument;
import com.mickey.dinggading.domain.memberrank.model.Tier;
import com.mickey.dinggading.domain.song.service.SongService;
import com.mickey.dinggading.model.SongByInstrumentDTO;
import com.mickey.dinggading.model.SongDTO;
import com.mickey.dinggading.model.SongPageDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SongController implements SongApi {

    private final SongService songService;

    /**
     * GET /api/songs/{song_id} : 곡 상세 정보 조회 특정 곡의 상세 정보를 조회합니다.
     *
     * @param songId 곡 ID (required)
     * @return 곡 상세 정보 응답 (status code 200)
     */
    @Override
    public ResponseEntity<?> getSong(Long songId) {
        log.info("곡 상세 정보 조회 요청. 곡 ID: {}", songId);

        SongDTO result = songService.getSong(songId);

        return ResponseEntity.ok(result);
    }

    /**
     * GET /songs/{songId}/instruments : 특정 곡의 악기별 연주 정보 조회 특정 곡의 악기별 연주 정보를 조회합니다.
     *
     * @param songId 곡 ID (required)
     * @return 악기별 곡 목록 응답 (status code 200)
     */
    @Override
    public ResponseEntity<?> getSongInstruments(Long songId) {
        log.info("곡의 악기별 버전 목록 조회 요청. 곡 ID: {}", songId);

        List<SongByInstrumentDTO> results = songService.getSongInstruments(songId);

        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/songs : 곡 목록 조회 전체 곡 목록을 조회합니다. size, page를 쿼리 파라미터로 전달하여 페이징 처리가 가능합니다.
     *
     * @param pageable 페이징 정보
     * @return 곡 목록 페이지 응답 (status code 200)
     */
    @Override
    public ResponseEntity<?> getSongs(Pageable pageable) {
        log.info("전체 곡 목록 조회 요청. 페이지: {}, 사이즈: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<SongDTO> results = songService.getSongs(pageable);

        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/songs/by-instrument/{instrument}/by-tier/{tier} : 악기, 티어 별 곡 목록 조회 특정 악기, 특정 티어에 해당하는 곡 목록을 조회합니다. size,
     * page를 쿼리 파라미터로 전달하여 페이징 처리가 가능합니다.
     *
     * @param instrument 악기 종류 (VOCAL, GUITAR, DRUM, BASS) (required)
     * @param tier       티어 등급 (UNRANKED, IRON, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND) (required)
     * @param pageable
     * @return SongPageDTO 곡 목록 페이지 응답 (status code 200) or 잘못된 요청입니다. (status code 400)
     */
    @Override
    public ResponseEntity<?> getSongsByInstrumentAndTier(String instrument, String tier, Pageable pageable) {
        log.info("악기, 티어 별 곡 목록 조회, 악기: {}, 티어: {}", instrument, tier);
        Tier targetTier = Tier.valueOf(tier);
        Instrument targetInstrument = Instrument.valueOf(instrument);

        SongPageDTO results = songService.getSongsByInstrumentAndTier(targetInstrument, targetTier, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/songs/by-instrument/{instrument} : 악기별 곡 목록 조회 특정 악기에 해당하는 곡 목록을 조회합니다. size, page를 쿼리 파라미터로 전달하여 페이징
     * 처리가 가능합니다.
     *
     * @param instrument 악기 종류 (VOCAL, GUITAR, DRUM, BASS) (required)
     * @param pageable   페이징 정보
     * @return 곡 목록 페이지 응답 (status code 200)
     */
    @Override
    public ResponseEntity<?> getSongsByInstrument(String instrument, Pageable pageable) {
        log.info("악기별 곡 목록 조회 요청. 악기: {}, 페이지: {}, 사이즈: {}",
                instrument, pageable.getPageNumber(), pageable.getPageSize());

        Page<SongDTO> results = songService.getSongsByInstrument(instrument, pageable);

        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/songs/by-tier/{tier} : 티어별 곡 목록 조회 특정 티어에 해당하는 곡 목록을 조회합니다. size, page를 쿼리 파라미터로 전달하여 페이징 처리가 가능합니다.
     *
     * @param tier     티어 등급 (UNRANKED, IRON, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND) (required)
     * @param pageable 페이징 정보
     * @return 곡 목록 페이지 응답 (status code 200)
     */
    @Override
    public ResponseEntity<?> getSongsByTier(String tier, Pageable pageable) {
        log.info("티어별 곡 목록 조회 요청. 티어: {}, 페이지: {}, 사이즈: {}",
                tier, pageable.getPageNumber(), pageable.getPageSize());

        Page<SongDTO> results = songService.getSongsByTier(tier, pageable);

        return ResponseEntity.ok(results);
    }
}