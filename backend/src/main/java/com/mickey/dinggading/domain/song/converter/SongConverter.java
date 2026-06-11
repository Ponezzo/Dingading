package com.mickey.dinggading.domain.song.converter;

import com.mickey.dinggading.domain.memberrank.model.Song;
import com.mickey.dinggading.model.ChallengePageableDTO;
import com.mickey.dinggading.model.SongBasicDTO;
import com.mickey.dinggading.model.SongByInstrumentDTO;
import com.mickey.dinggading.model.SongDTO;
import com.mickey.dinggading.model.SongPageDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class SongConverter {
    private final SongByInstrumentConverter songByInstrumentConverter;

    public SongConverter(SongByInstrumentConverter songByInstrumentConverter) {
        this.songByInstrumentConverter = songByInstrumentConverter;
    }

    private static ChallengePageableDTO getChallengePageableDTO(Page<Song> byInstrumentAndTier) {
        // ChallengePageableDTO 생성
        Pageable pageable = byInstrumentAndTier.getPageable();
        return ChallengePageableDTO.builder()
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .offset((int) pageable.getOffset())
                .paged(true)
                .unpaged(false)
                .build();
    }

    public SongDTO toDto(Song song) {
        if (song == null) {
            return null;
        }

        SongDTO dto = SongDTO.builder()
                .songId(song.getSongId())
                .title(song.getTitle())
                .artist(song.getArtist())
                .description(song.getDescription())
                .youtubeUrl(song.getYoutubeUrl())
                .build();

        if (song.getSongByInstruments() != null && !song.getSongByInstruments().isEmpty()) {
            List<SongByInstrumentDTO> instrumentDtos = song.getSongByInstruments().stream()
                    .map(songByInstrumentConverter::toDto)
                    .collect(Collectors.toList());
            dto.setInstruments(instrumentDtos);
        }

        return dto;
    }

    /**
     * Song 엔티티를 SongDTO로 변환
     *
     * @param song               변환할 Song 엔티티
     * @param includeInstruments 악기별 버전 정보 포함 여부
     * @return 변환된 SongDTO
     */
    public SongDTO toDto(Song song, boolean includeInstruments) {
        if (song == null) {
            return null;
        }

        SongDTO.SongDTOBuilder builder = SongDTO.builder()
                .songId(song.getSongId())
                .title(song.getTitle())
                .description(song.getDescription())
                .youtubeUrl(song.getYoutubeUrl());

        if (includeInstruments && song.getSongByInstruments() != null) {
            List<SongByInstrumentDTO> instrumentDTOs = song.getSongByInstruments().stream()
                    .map(sbi -> songByInstrumentConverter.toDto(sbi, false)) // 순환 참조 방지를 위해 false
                    .collect(Collectors.toList());
            builder.instruments(instrumentDTOs);
        }

        return builder.build();
    }

    /**
     * Song 엔티티를 SongBasicDTO로 변환 (간단한 정보만 포함)
     *
     * @param song 변환할 Song 엔티티
     * @return 변환된 SongBasicDTO
     */
    public SongBasicDTO toBasicDto(Song song) {
        if (song == null) {
            return null;
        }

        return SongBasicDTO.builder()
                .songId(song.getSongId())
                .title(song.getTitle())
                .youtubeUrl(song.getYoutubeUrl())
                .build();
    }

    /**
     * Song 엔티티 목록을 SongDTO 목록으로 변환
     *
     * @param songs              변환할 Song 엔티티 목록
     * @param includeInstruments 악기별 버전 정보 포함 여부
     * @return 변환된 SongDTO 목록
     */
    public List<SongDTO> toDtoList(List<Song> songs, boolean includeInstruments) {
        if (songs == null) {
            return List.of();
        }

        return songs.stream()
                .map(song -> toDto(song, includeInstruments))
                .collect(Collectors.toList());
    }

    public SongPageDTO toPageDto(Page<Song> byInstrumentAndTier) {
        // Page가 null인 경우 처리
        if (byInstrumentAndTier == null) {
            return null;
        }

        // SongDTO 리스트 생성
        List<SongDTO> content = byInstrumentAndTier.getContent().stream()
                .map(this::toSongDto)  // Song 객체를 SongDTO로 변환하는 메서드 필요
                .collect(Collectors.toList());

        ChallengePageableDTO pageable = getChallengePageableDTO(byInstrumentAndTier);

        // SongPageDTO 생성 및 반환
        return SongPageDTO.builder()
                .content(content)
                .pageable(pageable)
                .last(byInstrumentAndTier.isLast())
                .first(byInstrumentAndTier.isFirst())
                .totalElements((int) byInstrumentAndTier.getTotalElements())
                .totalPages(byInstrumentAndTier.getTotalPages())
                .size(byInstrumentAndTier.getSize())
                .number(byInstrumentAndTier.getNumber())
                .numberOfElements(byInstrumentAndTier.getNumberOfElements())
                .empty(byInstrumentAndTier.isEmpty())
                .build();
    }

    // Song 객체를 SongDTO로 변환하는 메서드 (구현 필요)
    private SongDTO toSongDto(Song song) {
        List<SongByInstrumentDTO> songInstruments = songByInstrumentConverter
                .toDtoList(song.getSongByInstruments(), false);

        return SongDTO.builder()
                .songId(song.getSongId())
                .title(song.getTitle())
                .artist(song.getArtist())
                .description(song.getDescription())
                .youtubeUrl(song.getYoutubeUrl())
                .songFilename(song.getSongFilename())
                .instruments(songInstruments)
                .build();
    }
}