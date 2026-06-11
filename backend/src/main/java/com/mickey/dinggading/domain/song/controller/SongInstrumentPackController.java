package com.mickey.dinggading.domain.song.controller;

import com.mickey.dinggading.api.SongInstrumentPackApi;
import com.mickey.dinggading.domain.song.service.SongInstrumentPackService;
import com.mickey.dinggading.model.SongInstrumentPackDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 곡 팩 관련 API 요청을 처리하는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
public class SongInstrumentPackController implements SongInstrumentPackApi {

    private final SongInstrumentPackService songInstrumentPackService;

    /**
     * 특정 곡 팩 상세 정보 조회
     *
     * @param songInstrumentPackId 곡 팩 ID
     * @return 곡 팩 상세 정보 응답
     */
    @Override
    public ResponseEntity<?> getSongPack(@PathVariable("song_instrument_pack_id") Long songInstrumentPackId) {
        try {
            SongInstrumentPackDTO dto = songInstrumentPackService.getSongPack(songInstrumentPackId);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 모든 곡 팩 목록 조회
     *
     * @param pageable 페이징 정보
     * @return 곡 팩 목록 페이지 응답
     */
    @Override
    public ResponseEntity<?> getSongPacks(Pageable pageable) {
        Page<SongInstrumentPackDTO> dtos = songInstrumentPackService.getAllPacks(pageable);
        return ResponseEntity.ok(dtos);
    }

    /**
     * 특정 악기에 해당하는 곡 팩 목록 조회
     *
     * @param instrument 악기 종류 (VOCAL, GUITAR, DRUM, BASS)
     * @param pageable   페이징 정보
     * @return 곡 팩 목록 페이지 응답
     */
    @Override
    public ResponseEntity<?> getSongPacksByInstrument(@PathVariable("instrument") String instrument,
                                                      Pageable pageable) {
        try {
            Page<SongInstrumentPackDTO> dtos = songInstrumentPackService.getPacksByInstrument(instrument, pageable);
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("잘못된 악기 종류입니다: " + instrument);
        }
    }

    /**
     * 특정 티어에 해당하는 곡 팩 목록 조회
     *
     * @param tier     티어 등급 (UNRANKED, IRON, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND)
     * @param pageable 페이징 정보
     * @return 곡 팩 목록 페이지 응답
     */
    @Override
    public ResponseEntity<?> getSongPacksByTier(@PathVariable("tier") String tier, Pageable pageable) {
        try {
            Page<SongInstrumentPackDTO> dtos = songInstrumentPackService.getPacksByTier(tier, pageable);
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("잘못된 티어 등급입니다: " + tier);
        }
    }
}