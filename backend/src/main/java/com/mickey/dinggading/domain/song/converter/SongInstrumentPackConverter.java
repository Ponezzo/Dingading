package com.mickey.dinggading.domain.song.converter;

import com.mickey.dinggading.domain.memberrank.model.SongInstrumentPack;
import com.mickey.dinggading.model.SongInstrumentPackDTO;
import com.mickey.dinggading.model.SongInstrumentPackDTO.SongPackInstrumentEnum;
import com.mickey.dinggading.model.SongInstrumentPackDTO.SongPackTierEnum;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

/**
 * SongInstrumentPack 엔티티와 DTO 간의 변환을 담당하는 컨버터 클래스
 */
@Component
public class SongInstrumentPackConverter {

    private final SongByInstrumentConverter songByInstrumentConverter;

    public SongInstrumentPackConverter(SongByInstrumentConverter songByInstrumentConverter) {
        this.songByInstrumentConverter = songByInstrumentConverter;
    }

    /**
     * SongInstrumentPack 엔티티를 SongInstrumentPackDTO로 변환
     *
     * @param pack         변환할 SongInstrumentPack 엔티티
     * @param includeSongs 포함된 곡 정보 포함 여부
     * @return 변환된 SongInstrumentPackDTO
     */
    public SongInstrumentPackDTO toDto(SongInstrumentPack pack, boolean includeSongs) {
        if (pack == null) {
            return null;
        }

        SongInstrumentPackDTO.SongInstrumentPackDTOBuilder builder = SongInstrumentPackDTO.builder()
                .songInstrumentPackId(pack.getSongInstrumentPackId())
                .packName(pack.getPackName())
                .songPackTier(SongPackTierEnum.valueOf(pack.getSongPackTier().name()))
                .songPackInstrument(SongPackInstrumentEnum.valueOf(pack.getSongPackInstrument().name()));

        if (includeSongs && pack.getSongByInstruments() != null) {
            builder.songs(
                    songByInstrumentConverter.toDtoList(pack.getSongByInstruments(), true)
            );
        }

        return builder.build();
    }

    /**
     * SongInstrumentPack 엔티티 목록을 SongInstrumentPackDTO 목록으로 변환
     *
     * @param packs        변환할 SongInstrumentPack 엔티티 목록
     * @param includeSongs 포함된 곡 정보 포함 여부
     * @return 변환된 SongInstrumentPackDTO 목록
     */
    public Page<SongInstrumentPackDTO> toDtoList(Page<SongInstrumentPack> packs, boolean includeSongs) {
        if (packs == null) {
            return Page.empty();
        }

        List<SongInstrumentPackDTO> dtoList = packs.stream()
                .map(pack -> toDto(pack, includeSongs))
                .toList();

        return new PageImpl<>(dtoList, packs.getPageable(), packs.getTotalElements());
    }

    /**
     * SongInstrumentPack 엔티티 목록을 SongInstrumentPackDTO 목록으로 변환
     *
     * @param packs        변환할 SongInstrumentPack 엔티티 목록
     * @param includeSongs 포함된 곡 정보 포함 여부
     * @return 변환된 SongInstrumentPackDTO 목록
     */
    public List<SongInstrumentPackDTO> toDtoList(List<SongInstrumentPack> packs, boolean includeSongs) {
        if (packs == null) {
            return List.of();
        }

        return packs.stream()
                .map(pack -> toDto(pack, includeSongs))
                .collect(Collectors.toList());
    }

    /**
     * Page<SongInstrumentPack>을 List<SongInstrumentPackDTO>로 변환
     *
     * @param packsPage    변환할 SongInstrumentPack 페이지
     * @param includeSongs 포함된 곡 정보 포함 여부
     * @return 변환된 SongInstrumentPackDTO 목록
     */
    public Page<SongInstrumentPackDTO> toDtoPage(
            Page<SongInstrumentPack> packsPage,
            boolean includeSongs
    ) {
        if (packsPage == null) {
            return Page.empty();
        }

        List<SongInstrumentPackDTO> dtoList = packsPage.getContent().stream()
                .map(pack -> toDto(pack, includeSongs))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, packsPage.getPageable(), packsPage.getTotalElements());
    }
}