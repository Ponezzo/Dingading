package com.mickey.dinggading.domain.band.repository;

import com.mickey.dinggading.domain.band.model.entity.BandRecruitmentApplicant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BandRecruitmentApplicantRepository extends JpaRepository<BandRecruitmentApplicant, Long> {

    /**
     * 구인 공고 ID와 지원자 ID로 지원 정보 조회
     *
     * @param recruitmentId 구인 공고 ID
     * @param applicantId   지원자 ID
     * @return 지원 정보
     */
    Optional<BandRecruitmentApplicant> findByBandRecruitmentInstrumentBandRecruitmentBandRecruitmentIdAndApplicantMemberId(
            Long recruitmentId, UUID applicantId);

    /**
     * 특정 구인 악기 포지션의 승인된 지원자 수 조회
     *
     * @param instrumentId 구인 악기 포지션 ID
     * @param status       지원 상태
     * @return 승인된 지원자 수
     */
    int countByBandRecruitmentInstrumentBandRecruitmentInstrumentsIdAndStatus(
            Long instrumentId, BandRecruitmentApplicant.ApplicantStatus status);

    boolean existsByBandRecruitmentInstrumentBandRecruitmentInstrumentsIdAndApplicantMemberId(
            Long instrumentId, UUID memberId);

    List<BandRecruitmentApplicant> findByBandRecruitmentInstrumentBandRecruitmentBandRecruitmentId(Long recruitmentId);
}