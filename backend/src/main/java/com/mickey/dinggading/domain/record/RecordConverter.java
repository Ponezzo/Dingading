package com.mickey.dinggading.domain.record;

import com.mickey.dinggading.domain.record.model.Record;
import com.mickey.dinggading.model.RecordDTO;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RecordConverter {

    /**
     * Record 엔티티를 RecordDTO로 변환합니다.
     *
     * @param record Record 엔티티
     * @return RecordDTO
     */
    public RecordDTO toDto(Record record) {
        if (record == null) {
            return null;
        }

        UUID memberId = record.getMember().getMemberId();
        Long attemptId = record.getAttempt() != null ? record.getAttempt().getAttemptId() : null;

        return RecordDTO.builder()
                .recordId(record.getRecordId())
                .memberId(memberId)
                .attemptId(attemptId)
                .dtype(RecordDTO.DtypeEnum.valueOf(record.getDtype().name()))
                .title(record.getTitle())
                .recordUrl(record.getRecordUrl())
                .createdAt(record.getCreatedAt())
                .build();
    }
}