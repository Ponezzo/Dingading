package com.mickey.dinggading.domain.song.service;

import com.mickey.dinggading.domain.memberrank.model.Instrument;
import com.mickey.dinggading.domain.memberrank.model.SongInstrumentPack;
import com.mickey.dinggading.domain.memberrank.model.Tier;
import com.mickey.dinggading.domain.song.converter.SongInstrumentPackConverter;
import com.mickey.dinggading.domain.song.repository.SongInstrumentPackRepository;
import com.mickey.dinggading.model.SongInstrumentPackDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 곡 팩 관련 비즈니스 로직을 담당하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class SongInstrumentPackService {

    private final SongInstrumentPackRepository songInstrumentPackRepository;
    private final SongInstrumentPackConverter songInstrumentPackConverter;

    /**
     * 특정 ID의 곡 팩 상세 정보 조회
     *
     * @param packId 조회할 팩 ID
     * @return 팩 상세 정보와 포함된 곡 목록을 포함한 DTO
     */
    @Transactional(readOnly = true)
    public SongInstrumentPackDTO getSongPack(Long packId) {
        SongInstrumentPack pack = songInstrumentPackRepository.findByIdWithSongs(packId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팩입니다. ID: " + packId));

        return songInstrumentPackConverter.toDto(pack, true);
    }

    /**
     * 모든 곡 팩 목록을 페이지 단위로 조회
     *
     * @param pageable 페이징 정보
     * @return 팩 DTO 목록
     */
    @Transactional(readOnly = true)
    public Page<SongInstrumentPackDTO> getAllPacks(Pageable pageable) {
        Page<SongInstrumentPack> packsPage = songInstrumentPackRepository.findAll(pageable);
        return songInstrumentPackConverter.toDtoList(packsPage, false);
    }

    /**
     * 특정 악기에 해당하는 모든 팩 조회
     *
     * @param instrumentStr 악기 타입 문자열
     * @param pageable      페이징 정보
     * @return 해당 악기의 모든 팩 DTO 목록
     */
    @Transactional(readOnly = true)
    public Page<SongInstrumentPackDTO> getPacksByInstrument(String instrumentStr, Pageable pageable) {
        Instrument instrument = Instrument.valueOf(instrumentStr);
        Page<SongInstrumentPack> packsPage = songInstrumentPackRepository.findBySongPackInstrument(instrument,
                pageable);
        return songInstrumentPackConverter.toDtoList(packsPage, false);
    }

    /**
     * 특정 티어에 해당하는 모든 팩 조회
     *
     * @param tierStr  티어 등급 문자열
     * @param pageable 페이징 정보
     * @return 해당 티어의 모든 팩 DTO 목록
     */
    @Transactional(readOnly = true)
    public Page<SongInstrumentPackDTO> getPacksByTier(String tierStr, Pageable pageable) {
        Tier tier = Tier.valueOf(tierStr);
        Page<SongInstrumentPack> packsPage = songInstrumentPackRepository.findBySongPackTier(tier, pageable);
        return songInstrumentPackConverter.toDtoList(packsPage, false);
    }

    /**
     * 랭크 매칭에 유효한 팩 목록 조회 (곡이 5개 포함된 팩)
     *
     * @param pageable 페이징 정보
     * @return 랭크 매칭에 유효한 팩 DTO 목록
     */
    @Transactional(readOnly = true)
    public Page<SongInstrumentPackDTO> getValidPacksForRankMatching(Pageable pageable) {
        Page<SongInstrumentPack> packsPage = songInstrumentPackRepository.findValidPacksForRankMatching(pageable);
        return songInstrumentPackConverter.toDtoList(packsPage, true);
    }

    /**
     * 새로운 곡 팩 생성
     *
     * @param packName      팩 이름
     * @param tierStr       티어 등급 문자열
     * @param instrumentStr 악기 타입 문자열
     * @return 생성된 팩의 ID
     */
    @Transactional
    public Long createPack(String packName, String tierStr, String instrumentStr) {
        Tier tier = Tier.valueOf(tierStr);
        Instrument instrument = Instrument.valueOf(instrumentStr);

        // 도메인 모델의 팩토리 메서드를 활용하여 엔티티 생성
        SongInstrumentPack pack = SongInstrumentPack.createPack(packName, tier, instrument);
        SongInstrumentPack savedPack = songInstrumentPackRepository.save(pack);

        return savedPack.getSongInstrumentPackId();
    }

    /**
     * 팩 정보 업데이트
     *
     * @param packId   업데이트할 팩 ID
     * @param packName 새 팩 이름
     */
    @Transactional
    public void updatePack(Long packId, String packName) {
        SongInstrumentPack pack = songInstrumentPackRepository.findById(packId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팩입니다. ID: " + packId));

        // 도메인 모델의 메서드를 활용하여 업데이트
        pack.updatePackInfo(packName);
    }

    /**
     * 특정 악기와 티어에 해당하는 팩 조회
     *
     * @param instrumentStr 악기 타입 문자열
     * @param tierStr       티어 등급 문자열
     * @return 조건에 맞는 팩 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<SongInstrumentPackDTO> getPacksByInstrumentAndTier(String instrumentStr, String tierStr) {
        Instrument instrument = Instrument.valueOf(instrumentStr);
        Tier tier = Tier.valueOf(tierStr);

        List<SongInstrumentPack> packs = songInstrumentPackRepository.findBySongPackInstrumentAndSongPackTier(
                instrument, tier);
        return songInstrumentPackConverter.toDtoList(packs, false);
    }
}