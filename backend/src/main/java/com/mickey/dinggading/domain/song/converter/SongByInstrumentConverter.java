package com.mickey.dinggading.domain.song.converter;

import com.mickey.dinggading.domain.memberrank.model.SongByInstrument;
import com.mickey.dinggading.model.SongBasicDTO;
import com.mickey.dinggading.model.SongByInstrumentDTO;
import com.mickey.dinggading.model.SongByInstrumentDTO.InstrumentEnum;
import com.mickey.dinggading.model.SongByInstrumentDTO.TierEnum;
import com.mickey.dinggading.model.SongByInstrumentURLResponseDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class SongByInstrumentConverter {
    private final SongBasicConverter songBasicConverter;

    public SongByInstrumentConverter(SongBasicConverter songBasicConverter) {
        this.songBasicConverter = songBasicConverter;
    }

    public SongByInstrumentDTO toDto(SongByInstrument entity) {
        if (entity == null) {
            return null;
        }

        SongByInstrumentDTO dto = SongByInstrumentDTO.builder()
                .songByInstrumentId(entity.getSongByInstrumentId())
                .songId(entity.getSong().getSongId())
                .songInstrumentPackId(entity.getSongInstrumentPack().getSongInstrumentPackId())
                .songByInstrumentFilename(entity.getSongByInstrumentFilename())
                .songByInstrumentExFilename(entity.getSongByInstrumentExFilename())
                .songByInstrumentAnalysisJson(entity.getSongByInstrumentAnalysisJson())
                .instrument(InstrumentEnum.valueOf(entity.getInstrument().name()))
                .tier(TierEnum.valueOf(entity.getTier().name()))
                .build();

        // Song 정보를 기본적인 형태로 포함
        if (entity.getSong() != null) {
            dto.setSong(songBasicConverter.toDto(entity.getSong()));
        }

        return dto;
    }

    /**
     * SongByInstrument 엔티티를 SongByInstrumentDTO로 변환
     *
     * @param songByInstrument 변환할 SongByInstrument 엔티티
     * @param includeSong      곡 정보 포함 여부 (순환 참조 방지)
     * @return 변환된 SongByInstrumentDTO
     */
    public SongByInstrumentDTO toDto(SongByInstrument songByInstrument, boolean includeSong) {
        if (songByInstrument == null) {
            return null;
        }

        SongByInstrumentDTO.SongByInstrumentDTOBuilder builder = SongByInstrumentDTO.builder()
                .songByInstrumentId(songByInstrument.getSongByInstrumentId())
                .songId(songByInstrument.getSong().getSongId())
                .songInstrumentPackId(songByInstrument.getSongInstrumentPack().getSongInstrumentPackId())
                .songByInstrumentFilename(songByInstrument.getSongByInstrumentFilename())
                .songByInstrumentExFilename(songByInstrument.getSongByInstrumentExFilename())
                .songByInstrumentAnalysisJson(songByInstrument.getSongByInstrumentAnalysisJson())
                .instrument(InstrumentEnum.valueOf(songByInstrument.getInstrument().name()))
                .tier(TierEnum.valueOf(songByInstrument.getTier().name()));

        // 순환 참조 방지를 위해 includeSong 파라미터 사용
        if (includeSong && songByInstrument.getSong() != null) {
            SongBasicDTO songBasicDTO = SongBasicDTO.builder()
                    .songId(songByInstrument.getSong().getSongId())
                    .title(songByInstrument.getSong().getTitle())
                    .youtubeUrl(songByInstrument.getSong().getYoutubeUrl())
                    .build();
            builder.song(songBasicDTO);
        }

        return builder.build();
    }

    /**
     * SongByInstrument 엔티티 목록을 SongByInstrumentDTO 목록으로 변환
     *
     * @param songByInstruments 변환할 SongByInstrument 엔티티 목록
     * @param includeSong       곡 정보 포함 여부
     * @return 변환된 SongByInstrumentDTO 목록
     */
    public List<SongByInstrumentDTO> toDtoList(List<SongByInstrument> songByInstruments, boolean includeSong) {
        if (songByInstruments == null) {
            return List.of();
        }

        return songByInstruments.stream()
                .map(sbi -> toDto(sbi, includeSong))
                .collect(Collectors.toList());
    }

    public SongByInstrumentURLResponseDTO toSongByInstrumentURLResponseDTO(
            SongByInstrument songByInstrument,
            String url
    ) {

        SongBasicDTO songBasicDTO = null;
        if (songByInstrument.getSong() != null) {
            songBasicDTO = songBasicConverter.toDto(songByInstrument.getSong());
        }

        return SongByInstrumentURLResponseDTO.builder()
                .song(songBasicDTO)
                .songByInstrumentId(songByInstrument.getSongByInstrumentId())
                .songByInstrumentUrl(url)
                .build();
    }
}