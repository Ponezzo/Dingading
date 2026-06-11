package com.mickey.dinggading.domain.song.service;

import com.mickey.dinggading.domain.memberrank.model.Instrument;
import com.mickey.dinggading.domain.memberrank.model.SongByInstrument;
import com.mickey.dinggading.domain.song.converter.SongByInstrumentConverter;
import com.mickey.dinggading.domain.song.repository.SongByInstrumentRepository;
import com.mickey.dinggading.domain.song.repository.SongInstrumentPackRepository;
import com.mickey.dinggading.domain.song.repository.SongRepository;
import com.mickey.dinggading.infra.minio.MinioService;
import com.mickey.dinggading.model.SongByInstrumentDTO;
import com.mickey.dinggading.model.SongByInstrumentURLResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 악기별 곡 버전 관련 비즈니스 로직을 담당하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class SongByInstrumentService {

    private final SongByInstrumentRepository songByInstrumentRepository;
    private final SongRepository songRepository;
    private final SongInstrumentPackRepository songInstrumentPackRepository;
    private final SongByInstrumentConverter songByInstrumentConverter;
    private final MinioService minioService;

    /**
     * 특정 ID의 악기별 곡 버전 상세 정보 조회
     *
     * @param songByInstrumentId 조회할 악기별 곡 버전 ID
     * @return 악기별 곡 버전 상세 정보
     */
    @Transactional(readOnly = true)
    public SongByInstrumentDTO getSongByInstrument(Long songByInstrumentId) {
        SongByInstrument songByInstrument = songByInstrumentRepository.findByIdWithDetails(songByInstrumentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 악기별 곡 버전입니다. ID: " + songByInstrumentId));

        return songByInstrumentConverter.toDto(songByInstrument, true);
    }

    /**
     * 특정 곡의 모든 악기별 버전 조회
     *
     * @param songId 조회할 곡 ID
     * @return 해당 곡의 모든 악기별 버전 목록
     */
    @Transactional(readOnly = true)
    public List<SongByInstrumentDTO> getSongInstruments(Long songId) {
        // 곡 존재 여부 확인
        songRepository.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 곡입니다. ID: " + songId));

        List<SongByInstrument> songByInstruments = songByInstrumentRepository.findBySongSongId(songId);
        return songByInstrumentConverter.toDtoList(songByInstruments, true);
    }

    /**
     * 악기별 곡 버전의 URL 업데이트
     *
     * @param songByInstrumentId 업데이트할 악기별 곡 버전 ID
     * @param instrumentUrl      새 악기별 연주 URL
     */
    @Transactional
    public void updateInstrumentUrl(Long songByInstrumentId, String instrumentUrl) {
        SongByInstrument songByInstrument = songByInstrumentRepository.findById(songByInstrumentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 악기별 곡 버전입니다. ID: " + songByInstrumentId));

        // 도메인 모델의 메서드를 활용하여 URL 업데이트
        songByInstrument.updateInstrumentUrl(instrumentUrl);
    }

    /**
     * 특정 팩에 속한 모든 곡 버전 조회
     *
     * @param packId 조회할 팩 ID
     * @return 해당 팩에 속한 모든 곡 버전 목록
     */
    @Transactional(readOnly = true)
    public List<SongByInstrumentDTO> getSongsByPack(Long packId) {
        // 팩 존재 여부 확인
        songInstrumentPackRepository.findById(packId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팩입니다. ID: " + packId));

        List<SongByInstrument> songByInstruments = songByInstrumentRepository.findBySongInstrumentPackSongInstrumentPackId(
                packId);
        return songByInstrumentConverter.toDtoList(songByInstruments, true);
    }

    @Transactional(readOnly = true)
    public SongByInstrumentURLResponseDTO getSongByInstrumentUrl(Long songByInstrumentId) {
        SongByInstrument songByInstrument = songByInstrumentRepository.findById(songByInstrumentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 악기별 곡 버전입니다. ID: " + songByInstrumentId));

        String url = minioService.generatePresignedUrlMyBucket(songByInstrument.getSongByInstrumentExFilename());

        return songByInstrumentConverter.toSongByInstrumentURLResponseDTO(songByInstrument, url);
    }

    @Transactional(readOnly = true)
    public SongByInstrumentURLResponseDTO getSongByInstrumentUrl(Long songId, Instrument instrument) {
        // 결과 리스트 조회
        List<SongByInstrument> songByInstrumentList = songByInstrumentRepository
                .findBySongSongIdAndInstrument(songId, instrument);

        // 결과가 없는 경우 예외 처리
        if (songByInstrumentList.isEmpty()) {
            throw new EntityNotFoundException("해당하는 악기와 곡이 존재하지 않습니다. 곡 ID: " + songId + ", 악기: " + instrument);
        }

        // 결과가 있는 경우 첫 번째 항목 사용
        SongByInstrument songByInstrument = songByInstrumentList.get(0);
        String url = minioService.generatePresignedUrlMyBucket(songByInstrument.getSongByInstrumentExFilename());

        return songByInstrumentConverter.toSongByInstrumentURLResponseDTO(songByInstrument, url);
    }
}