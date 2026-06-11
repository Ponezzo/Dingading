package com.mickey.dinggading.domain.band.repository;

import com.mickey.dinggading.domain.band.model.entity.Band;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BandRepository extends JpaRepository<Band, Long> {

    Band findBandsByBandId(Long bandId);

    Page<Band> findAll(Pageable pageable);

    @Query("SELECT b FROM Band b WHERE " +
            "(:keyword IS NULL OR LOWER(b.name) LIKE :keyword) AND " +
            "(:sigun IS NULL OR b.sigun = :sigun) AND " +
            "(:jobOpening IS NULL OR b.jobOpening = :jobOpening)")
    Page<Band> searchBands(
            @Param("keyword") String keyword, @Param("sigun") String sigun, @Param("jobOpening") Boolean jobOpening,
            Pageable pageable);

}