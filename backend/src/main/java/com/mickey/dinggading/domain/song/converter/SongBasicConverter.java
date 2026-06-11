package com.mickey.dinggading.domain.song.converter;

import com.mickey.dinggading.domain.memberrank.model.Song;
import com.mickey.dinggading.model.SongBasicDTO;
import org.springframework.stereotype.Component;

@Component
public class SongBasicConverter {

    public SongBasicDTO toDto(Song song) {
        if (song == null) {
            return null;
        }

        return SongBasicDTO.builder()
                .songId(song.getSongId())
                .title(song.getTitle())
                .youtubeUrl(song.getYoutubeUrl())
                .build();
    }
}