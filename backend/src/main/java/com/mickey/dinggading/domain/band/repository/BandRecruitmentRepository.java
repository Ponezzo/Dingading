package com.mickey.dinggading.domain.band.repository;

import com.mickey.dinggading.domain.band.model.entity.BandRecruitment;
import com.mickey.dinggading.model.RecruitmentStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface BandRecruitmentRepository extends CrudRepository<BandRecruitment, Long> {
    /**
     * 특정 상태의 구인 공고만 페이징 처리하여 조회
     *
     * @param status   구인 공고 상태
     * @param pageable 페이징 정보
     * @return 페이징 처리된 특정 상태의 구인 공고 목록
     */
    Page<BandRecruitment> findByStatus(RecruitmentStatus status, Pageable pageable);

    /**
     * 제목에 특정 키워드가 포함된 구인 공고를 페이징 처리하여 조회
     *
     * @param keyword  검색 키워드
     * @param pageable 페이징 정보
     * @return 페이징 처리된 검색 결과 구인 공고 목록
     */
    Page<BandRecruitment> findByTitleContaining(String keyword, Pageable pageable);

    Optional<BandRecruitment> findByBandBandIdAndStatus(Long bandId, RecruitmentStatus status);

    boolean existsByBandBandIdAndStatus(Long bandId, RecruitmentStatus status);

    Page<BandRecruitment> findAll(Pageable pageable);

}
