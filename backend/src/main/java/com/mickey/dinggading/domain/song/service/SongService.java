package com.mickey.dinggading.domain.song.service;

import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.memberrank.model.Instrument;
import com.mickey.dinggading.domain.memberrank.model.Song;
import com.mickey.dinggading.domain.memberrank.model.Tier;
import com.mickey.dinggading.domain.song.converter.SongByInstrumentConverter;
import com.mickey.dinggading.domain.song.converter.SongConverter;
import com.mickey.dinggading.domain.song.repository.SongRepository;
import com.mickey.dinggading.exception.ExceptionHandler;
import com.mickey.dinggading.model.SongByInstrumentDTO;
import com.mickey.dinggading.model.SongDTO;
import com.mickey.dinggading.model.SongPageDTO;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SongService {
    private final SongRepository songRepository;
    private final SongConverter songConverter;
    private final SongByInstrumentConverter songByInstrumentConverter;

    /**
     * 모든 곡 목록을 페이지네이션으로 조회합니다.
     */
    public Page<SongDTO> getSongs(Pageable pageable) {
        log.info("모든 곡 목록 조회. 페이지: {}, 사이즈: {}", pageable.getPageNumber(), pageable.getPageSize());
        return songRepository.findAll(pageable)
                .map(songConverter::toDto);
    }

    /**
     * 특정 곡의 상세 정보를 조회합니다.
     */
    public SongDTO getSong(Long songId) {
        log.info("곡 상세 정보 조회. 곡 ID: {}", songId);
        Song song = findSongById(songId);
        return songConverter.toDto(song);
    }

    /**
     * 악기별 곡 목록을 조회합니다.
     */
    public Page<SongDTO> getSongsByInstrument(String instrumentStr, Pageable pageable) {
        log.info("악기별 곡 목록 조회. 악기: {}, 페이지: {}, 사이즈: {}",
                instrumentStr, pageable.getPageNumber(), pageable.getPageSize());

        Instrument instrument = parseInstrument(instrumentStr);
        return songRepository.findAllByInstrument(instrument, pageable)
                .map(songConverter::toDto);
    }

    /**
     * 티어별 곡 목록을 조회합니다.
     */
    public Page<SongDTO> getSongsByTier(String tierStr, Pageable pageable) {
        log.info("티어별 곡 목록 조회. 티어: {}, 페이지: {}, 사이즈: {}",
                tierStr, pageable.getPageNumber(), pageable.getPageSize());

        Tier tier = parseTier(tierStr);
        return songRepository.findAllByTier(tier, pageable)
                .map(songConverter::toDto);
    }

    /**
     * 특정 곡의 악기별 버전 목록을 조회합니다.
     */
    public List<SongByInstrumentDTO> getSongInstruments(Long songId) {
        log.info("곡의 악기별 버전 목록 조회. 곡 ID: {}", songId);
        Song song = findSongById(songId);

        return song.getSongByInstruments().stream()
                .map(songByInstrumentConverter::toDto)
                .collect(Collectors.toList());
    }

    // 헬퍼 메서드 - ID로 곡 찾기
    private Song findSongById(Long songId) {
        return songRepository.findById(songId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.SONG_NOT_FOUND));
    }

    // 헬퍼 메서드 - 악기 문자열 파싱
    private Instrument parseInstrument(String instrumentStr) {
        try {
            return Instrument.valueOf(instrumentStr);
        } catch (IllegalArgumentException e) {
            log.error("유효하지 않은 악기: {}", instrumentStr);
            throw new ExceptionHandler(ErrorStatus.INVALID_INSTRUMENT);
        }
    }

    // 헬퍼 메서드 - 티어 문자열 파싱
    private Tier parseTier(String tierStr) {
        try {
            return Tier.valueOf(tierStr);
        } catch (IllegalArgumentException e) {
            log.error("유효하지 않은 티어: {}", tierStr);
            throw new ExceptionHandler(ErrorStatus.INVALID_TIER);
        }
    }

    public SongPageDTO getSongsByInstrumentAndTier(Instrument instrument, Tier tier, Pageable pageable) {
        Page<Song> byInstrumentAndTier = songRepository.findByInstrumentAndTier(instrument, tier, pageable);

        return songConverter.toPageDto(byInstrumentAndTier);
    }
}