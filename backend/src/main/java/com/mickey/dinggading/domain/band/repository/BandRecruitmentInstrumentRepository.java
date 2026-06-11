package com.mickey.dinggading.domain.band.repository;

import com.mickey.dinggading.domain.band.model.entity.BandRecruitmentInstrument;
import com.mickey.dinggading.model.Instrument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BandRecruitmentInstrumentRepository extends JpaRepository<BandRecruitmentInstrument, Long> {
    List<BandRecruitmentInstrument> findByBandRecruitmentBandRecruitmentId(Long recruitmentId);

    List<BandRecruitmentInstrument> findByInstrument(Instrument instrument);

    Optional<BandRecruitmentInstrument> findByBandRecruitmentBandRecruitmentIdAndInstrument(Long recruitmentId,
                                                                                            Instrument instrument);
}
