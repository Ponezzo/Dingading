package com.mickey.dinggading.domain.band.repository;

import com.mickey.dinggading.domain.band.model.entity.BandMember;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BandMemberRepository extends JpaRepository<BandMember, Long> {

    List<BandMember> getBandMembersByBand_BandId(Long bandId);

    void deleteAllByBand_BandId(Long bandId);

    Page<BandMember> findByBand_BandId(Long bandId, Pageable pageable);

    Optional<BandMember> findByBand_BandIdAndMember_MemberId(Long bandId, UUID memberIdToRemove);

    boolean existsByBand_BandIdAndMember_MemberId(Long bandId, UUID newMasterId);

    List<BandMember> findAllByBand_BandIdIn(List<Long> bandIds);

    @Query("SELECT bm FROM BandMember bm JOIN FETCH bm.member WHERE bm.band.bandId = :bandId")
    List<BandMember> findAllWithMemberByBandId(@Param("bandId") Long bandId);

    boolean existsByBandBandIdAndMemberMemberId(Long bandId, UUID memberId);
}