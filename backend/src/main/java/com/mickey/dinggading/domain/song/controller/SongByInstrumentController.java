package com.mickey.dinggading.domain.song.controller;

import com.mickey.dinggading.api.SongByInstrumentApi;
import com.mickey.dinggading.domain.memberrank.model.Instrument;
import com.mickey.dinggading.domain.song.service.SongByInstrumentService;
import com.mickey.dinggading.model.SongByInstrumentDTO;
import com.mickey.dinggading.model.SongByInstrumentURLResponseDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 악기별 곡 버전 관련 API 요청을 처리하는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
public class SongByInstrumentController implements SongByInstrumentApi {

    private final SongByInstrumentService songByInstrumentService;

    /**
     * POST /api/songs/instruments/{song_by_instrument_id} : 악기 별 곡 접속 URL 조회 특정 악기별 곡의 mp3파일을 조회합니다. 연주하려는 악기의 음성이 제거된
     * 음원의 URL이 조회됩니다.
     *
     * @param songByInstrumentId 악기별 곡 ID (required)
     * @return SongByInstrumentURLResponseDTO 곡 ID에 따른 접속 URL 응답 (status code 200) or 요청한 리소스를 찾을 수 없습니다. (status code
     * 404)
     */
    @Override
    public ResponseEntity<?> requestSongByInstrumentUrl(Long songByInstrumentId) {
        SongByInstrumentURLResponseDTO result = songByInstrumentService.getSongByInstrumentUrl(songByInstrumentId);
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/songs/{song_id}/by-instrument/{instrument} : 곡 ID와 악기별 곡 접속 URL 조회 특정 곡 ID와 악기 종류에 해당하는 악기별 곡의 mp3파일을
     * 조회합니다. 연주하려는 악기의 음성이 제거된 음원의 URL이 조회됩니다.
     *
     * @param songId     곡 ID (required)
     * @param instrument 악기 종류 (VOCAL, GUITAR, DRUM, BASS) (required)
     * @return SongByInstrumentURLResponseDTO 곡 ID에 따른 접속 URL 응답 (status code 200) or 요청한 리소스를 찾을 수 없습니다. (status code
     * 404)
     */
    @Override
    public ResponseEntity<?> getSongByInstrumentUrlBySongIdAndInstrument(Long songId, String instrument) {
        Instrument instrumentEnum = Instrument.valueOf(instrument);
        SongByInstrumentURLResponseDTO result = songByInstrumentService.getSongByInstrumentUrl(songId, instrumentEnum);
        return ResponseEntity.ok(result);
    }

    /**
     * 특정 악기별 곡 버전 상세 정보 조회
     *
     * @param songByInstrumentId 악기별 곡 ID
     * @return 악기별 곡 상세 정보 응답
     */
    @Override
    public ResponseEntity<?> getSongByInstrument(@PathVariable("song_by_instrument_id") Long songByInstrumentId) {
        try {
            SongByInstrumentDTO dto = songByInstrumentService.getSongByInstrument(songByInstrumentId);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 특정 곡의 모든 악기별 버전 목록 조회
     *
     * @param songId 곡 ID
     * @return 곡의 악기별 버전 목록 응답
     */
    @Override
    public ResponseEntity<?> getSongInstruments1(@PathVariable("song_id") Long songId) {
        try {
            List<SongByInstrumentDTO> dtos = songByInstrumentService.getSongInstruments(songId);
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}