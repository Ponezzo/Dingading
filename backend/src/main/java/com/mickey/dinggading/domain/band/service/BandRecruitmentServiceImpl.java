package com.mickey.dinggading.domain.band.service;

import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.band.converter.RecruitmentConverter;
import com.mickey.dinggading.domain.band.model.entity.Band;
import com.mickey.dinggading.domain.band.model.entity.BandMember;
import com.mickey.dinggading.domain.band.model.entity.BandRecruitment;
import com.mickey.dinggading.domain.band.model.entity.BandRecruitmentApplicant;
import com.mickey.dinggading.domain.band.model.entity.BandRecruitmentInstrument;
import com.mickey.dinggading.domain.band.repository.BandMemberRepository;
import com.mickey.dinggading.domain.band.repository.BandRecruitmentApplicantRepository;
import com.mickey.dinggading.domain.band.repository.BandRecruitmentInstrumentRepository;
import com.mickey.dinggading.domain.band.repository.BandRecruitmentRepository;
import com.mickey.dinggading.domain.band.repository.BandRepository;
import com.mickey.dinggading.domain.member.model.entity.Member;
import com.mickey.dinggading.domain.member.repository.MemberRepository;
import com.mickey.dinggading.domain.memberrank.model.MemberRank;
import com.mickey.dinggading.domain.memberrank.model.Song;
import com.mickey.dinggading.domain.memberrank.repository.MemberRankRepository;
import com.mickey.dinggading.domain.song.repository.SongRepository;
import com.mickey.dinggading.exception.ExceptionHandler;
import com.mickey.dinggading.model.AddRecruitmentInstrumentRequest;
import com.mickey.dinggading.model.ApplicantDTO;
import com.mickey.dinggading.model.ApplyRecruitmentRequest;
import com.mickey.dinggading.model.CreateBandRecruitmentRequest;
import com.mickey.dinggading.model.Instrument;
import com.mickey.dinggading.model.RecruitmentDTO;
import com.mickey.dinggading.model.RecruitmentInstrumentDTO;
import com.mickey.dinggading.model.RecruitmentStatus;
import com.mickey.dinggading.model.Tier;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BandRecruitmentServiceImpl implements BandRecruitmentService {

    private final BandRepository bandRepository;
    private final SongRepository songRepository;
    private final BandMemberRepository bandMemberRepository;
    private final BandRecruitmentRepository bandRecruitmentRepository;
    private final RecruitmentConverter recruitmentConverter;
    private final BandRecruitmentInstrumentRepository bandRecruitmentInstrumentRepository;
    private final MemberRepository memberRepository;
    private final BandRecruitmentApplicantRepository bandRecruitmentApplicantRepository;
    private final MemberRankRepository memberRankRepository;

    /**
     * 밴드 구인 공고 등록
     *
     * @param bandId   밴드 ID
     * @param request  구인 공고 생성 요청 객체
     * @param memberId 요청자 ID (밴드 마스터여야 함)
     * @return 생성된 구인 공고 DTO
     */
    @Override
    public RecruitmentDTO createBandRecruitment(Long bandId, CreateBandRecruitmentRequest request, UUID memberId) {
        log.info("Creating band recruitment. BandId: {}, MemberId: {}", bandId, memberId);

        // 밴드 존재 여부 및 권한 확인
        Band band = validateBandAndMemberAccess(bandId, memberId);

        // 밴드에 이미 활성화된 구인 공고가 있는지 확인
        validateNoActiveRecruitment(bandId);

        // 오디션 곡 존재 여부 확인
        Song auditionSong = getSong(request.getAuditionSongId());

        // BandRecruitment 엔티티 생성 및 저장 (팩토리 메서드 사용)
        BandRecruitment bandRecruitment = BandRecruitment.create(request, band, auditionSong);
        bandRecruitment = bandRecruitmentRepository.save(bandRecruitment);

        log.info("Band recruitment created successfully. RecruitmentId: {}", bandRecruitment.getBandRecruitmentId());

        // 저장된 엔티티를 DTO로 변환하여 반환
        return recruitmentConverter.toBandRecruitmentDTO(bandRecruitment);
    }

    @Override
    public RecruitmentDTO updateBandRecruitment(Long bandId, Long recruitmentId, CreateBandRecruitmentRequest request,
                                                UUID memberId) {
        log.info("Updating band recruitment. BandId: {}, RecruitmentId: {}, MemberId: {}", bandId, recruitmentId,
                memberId);

        // 밴드 존재 여부 및 권한 확인
        Band band = validateBandAndMemberAccess(bandId, memberId);

        // 구인 공고 존재 여부 확인 및 밴드 소유권 검증
        BandRecruitment bandRecruitment = validateRecruitmentAccess(recruitmentId, bandId);

        // 구인 공고 상태 확인 (COMPLETED 상태일 경우 수정 불가)
        validateRecruitmentActive(bandRecruitment);

        // 오디션 곡 존재 여부 확인
        Song auditionSong = getSong(request.getAuditionSongId());

        // BandRecruitment 엔티티 업데이트 및 저장 (update 메서드 사용)
        bandRecruitment.update(request, auditionSong);
        bandRecruitment = bandRecruitmentRepository.save(bandRecruitment);

        log.info("Band recruitment updated successfully. RecruitmentId: {}", bandRecruitment.getBandRecruitmentId());

        // 저장된 엔티티를 DTO로 변환하여 반환
        return recruitmentConverter.toBandRecruitmentDTO(bandRecruitment);
    }

    @Override
    public Page<RecruitmentDTO> getAllRecruitments(Pageable pageable) {
        log.info("Fetching all recruitments with page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<BandRecruitment> recruitmentPage = bandRecruitmentRepository.findAll(pageable);

        // 엔티티 Page를 DTO Page로 변환
        return recruitmentPage.map(recruitmentConverter::toBandRecruitmentDTO);
    }

    @Override
    public RecruitmentDTO getBandRecruitment(Long bandId) {
        log.info("Getting active recruitment for band ID: {}", bandId);

        // 밴드 존재 여부 확인
        if (!bandRepository.existsById(bandId)) {
            log.warn("Band not found with ID: {}", bandId);
            throw new ExceptionHandler(ErrorStatus.BAND_NOT_FOUND);
        }

        // 밴드의 활성화된 구인 공고 조회
        BandRecruitment recruitment = bandRecruitmentRepository.findByBandBandIdAndStatus(bandId,
                        RecruitmentStatus.RECRUITING)
                .orElseThrow(() -> {
                    log.warn("No active recruitment found for band ID: {}", bandId);
                    return new ExceptionHandler(ErrorStatus.RECRUITMENT_NOT_FOUND);
                });

        log.info("Found active recruitment for band ID: {}, recruitment ID: {}", bandId,
                recruitment.getBandRecruitmentId());

        // 엔티티를 DTO로 변환하여 반환
        return recruitmentConverter.toBandRecruitmentDTO(recruitment);
    }

    /**
     * 구인 공고 상태 변경
     */
    @Override
    public RecruitmentDTO updateRecruitmentStatus(Long bandId, Long recruitmentId, RecruitmentStatus status,
                                                  UUID memberId) {
        log.info("Updating recruitment status. BandId: {}, RecruitmentId: {}, Status: {}, MemberId: {}",
                bandId, recruitmentId, status, memberId);

        // 밴드 존재 여부 및 권한 확인
        Band band = validateBandAndMemberAccess(bandId, memberId);

        // 구인 공고 존재 여부 확인 및 밴드 소유권 검증
        BandRecruitment bandRecruitment = validateRecruitmentAccess(recruitmentId, bandId);

        // 상태가 동일한 경우 확인 (불필요한 업데이트 방지)
        if (bandRecruitment.getStatus() == status) {
            log.info("Recruitment status is already {}: RecruitmentId: {}",
                    status, recruitmentId);
            return recruitmentConverter.toBandRecruitmentDTO(bandRecruitment);
        }

        // 구인 공고 상태 업데이트 및 저장
        bandRecruitment.updateStatus(status);
        bandRecruitment = bandRecruitmentRepository.save(bandRecruitment);

        // 저장된 엔티티를 DTO로 변환하여 반환
        return recruitmentConverter.toBandRecruitmentDTO(bandRecruitment);
    }

    @Override
    public void deleteBandRecruitment(Long bandId, Long recruitmentId, UUID memberId) {
        log.info("Deleting recruitment. BandId: {}, RecruitmentId: {}, MemberId: {}",
                bandId, recruitmentId, memberId);

        // 밴드 존재 여부 및 권한 확인
        Band band = validateBandAndMemberAccess(bandId, memberId);

        // 구인 공고 존재 여부 확인 및 밴드 소유권 검증
        BandRecruitment bandRecruitment = validateRecruitmentAccess(recruitmentId, bandId);

        try {
            // 구인 공고 삭제
            bandRecruitmentRepository.delete(bandRecruitment);
            log.info("Recruitment deleted successfully. RecruitmentId: {}", recruitmentId);
        } catch (Exception e) {
            log.error("Error deleting recruitment: {}", e.getMessage());
            throw new ExceptionHandler(ErrorStatus.RECRUITMENT_DELETE_FAILED);
        }
    }

    // 지원자 승인
    @Override
    public void acceptApplicant(Long bandId, Long recruitmentId, UUID applicantId, UUID memberId) {
        log.info("Accepting applicant. BandId: {}, RecruitmentId: {}, ApplicantId: {}, MemberId: {}",
                bandId, recruitmentId, applicantId, memberId);

        // 밴드 존재 여부 및 권한 확인
        Band band = validateBandAndMemberAccess(bandId, memberId);

        // 구인 공고 존재 여부 확인 및 밴드 소유권 검증
        BandRecruitment bandRecruitment = validateRecruitmentAccess(recruitmentId, bandId);

        // 구인 공고 상태 확인 (COMPLETED 상태일 경우 승인 불가)
        validateRecruitmentActive(bandRecruitment);

        // 지원자 존재 여부 확인
        Member applicant = memberRepository.findById(applicantId)
                .orElseThrow(() -> {
                    log.warn("Applicant not found with ID: {}", applicantId);
                    return new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND);
                });

        // 해당 지원자의 지원 정보 조회
        BandRecruitmentApplicant applicantInfo = bandRecruitmentApplicantRepository.findByBandRecruitmentInstrumentBandRecruitmentBandRecruitmentIdAndApplicantMemberId(
                        recruitmentId, applicantId)
                .orElseThrow(() -> {
                    log.warn("Application not found for applicant: {} in recruitment: {}", applicantId, recruitmentId);
                    return new ExceptionHandler(ErrorStatus.RECRUITMENT_APPLICANT_NOT_FOUND);
                });

        // 지원 상태가 이미 처리되었는지 확인 (PENDING 상태만 승인 가능)
        if (applicantInfo.getStatus() != BandRecruitmentApplicant.ApplicantStatus.PENDING) {
            log.warn("Application status is already processed: {}", applicantInfo.getStatus());
            throw new ExceptionHandler(ErrorStatus.RECRUITMENT_APPLICANT_ALREADY_PROCESSED);
        }

        // 해당 포지션의 모집 인원 확인
        BandRecruitmentInstrument instrument = applicantInfo.getBandRecruitmentInstrument();
        int acceptedCount = bandRecruitmentApplicantRepository.countByBandRecruitmentInstrumentBandRecruitmentInstrumentsIdAndStatus(
                instrument.getBandRecruitmentInstrumentsId(),
                BandRecruitmentApplicant.ApplicantStatus.ACCEPTED);

        if (acceptedCount >= instrument.getMaxSize()) {
            log.warn("Max size exceeded for instrument position: {}", instrument.getBandRecruitmentInstrumentsId());
            throw new ExceptionHandler(ErrorStatus.RECRUITMENT_MAX_SIZE_EXCEEDED);
        }

        // 트랜잭션으로 상태 변경과 밴드 멤버 추가를 함께 수행
        try {
            // 1. 지원자 상태를 '승인됨'으로 업데이트
            applicantInfo.accept();
            bandRecruitmentApplicantRepository.save(applicantInfo);

            // 2. 지원자를 밴드 멤버로 추가
            addApplicantToBand(band, applicant, instrument.getInstrument());

            log.info("Applicant accepted and added to band successfully. ApplicantId: {}, BandId: {}, Instrument: {}",
                    applicantId, bandId, instrument.getInstrument());
        } catch (Exception e) {
            log.error("Error accepting applicant: {}", e.getMessage(), e);
            throw new ExceptionHandler(ErrorStatus.APPLICANT_ACCEPT_FAILED);
        }
    }

    /**
     * 승인된 지원자를 밴드 멤버로 추가
     *
     * @param band       밴드 엔티티
     * @param applicant  지원자 엔티티
     * @param instrument 악기 타입
     * @return 생성된 밴드 멤버 엔티티
     */
    private BandMember addApplicantToBand(Band band, Member applicant, Instrument instrument) {
        // 이미 밴드 멤버인지 한번 더 확인
        boolean isAlreadyMember = bandMemberRepository.existsByBandBandIdAndMemberMemberId(
                band.getBandId(), applicant.getMemberId());

        if (isAlreadyMember) {
            log.warn("Applicant is already a band member. MemberId: {}, BandId: {}",
                    applicant.getMemberId(), band.getBandId());
            throw new ExceptionHandler(ErrorStatus.RECRUITMENT_ALREADY_BAND_MEMBER);
        }

        // 밴드 멤버 생성 및 저장
        BandMember bandMember = new BandMember(band, applicant, instrument);

        return bandMemberRepository.save(bandMember);
    }

    // 악기 포지션 추가
    @Override
    public RecruitmentInstrumentDTO addRecruitmentInstrument(Long bandId, Long recruitmentId,
                                                             AddRecruitmentInstrumentRequest request, UUID memberId) {
        log.info("Adding instrument to recruitment. BandId: {}, RecruitmentId: {}, Instrument: {}, MemberId: {}",
                bandId, recruitmentId, request.getInstrument(), memberId);

        // 밴드 존재 여부 및 권한 확인
        Band band = validateBandAndMemberAccess(bandId, memberId);

        // 구인 공고 존재 여부 확인 및 밴드 소유권 검증
        BandRecruitment bandRecruitment = validateRecruitmentAccess(recruitmentId, bandId);

        // 구인 공고 상태 확인 (COMPLETED 상태일 경우 추가 불가)
        validateRecruitmentActive(bandRecruitment);

        // 동일한 악기 중복 확인
        if (isDuplicateInstrument(bandRecruitment, request.getInstrument())) {
            log.warn("Duplicate instrument: {} for recruitment: {}",
                    request.getInstrument(), recruitmentId);
            throw new ExceptionHandler(ErrorStatus.RECRUITMENT_INSTRUMENT_ALREADY_EXISTS);
        }

        // BandRecruitmentInstrument 엔티티 생성 및 저장
        BandRecruitmentInstrument instrument = new BandRecruitmentInstrument(request.getInstrument(),
                request.getRequiredTier(), request.getMaxSize());

        // BandRecruitment와 연결
        bandRecruitment.addInstrument(instrument);

        // 저장
        instrument = bandRecruitmentInstrumentRepository.save(instrument);

        log.info("Instrument added successfully. InstrumentId: {}, RecruitmentId: {}",
                instrument.getBandRecruitmentInstrumentsId(), recruitmentId);

        // 저장된 엔티티를 DTO로 변환하여 반환
        return recruitmentConverter.toRecruitmentInstrumentDTO(instrument);
    }

    // 구인 공고 지원
    @Override
    public ApplicantDTO applyRecruitment(Long recruitmentId, ApplyRecruitmentRequest request, UUID memberId) {
        log.info("Applying to recruitment. RecruitmentId: {}, InstrumentId: {}, MemberId: {}",
                recruitmentId, request.getBandRecruitmentInstrumentsId(), memberId);

        // 구인 공고 존재 여부 확인
        BandRecruitment bandRecruitment = bandRecruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    log.warn("Recruitment not found with ID: {}", recruitmentId);
                    return new ExceptionHandler(ErrorStatus.RECRUITMENT_NOT_FOUND);
                });

        // 구인 공고 상태 확인 (COMPLETED 상태일 경우 지원 불가)
        validateRecruitmentActive(bandRecruitment);

        // 악기 포지션 존재 여부 확인
        BandRecruitmentInstrument instrument = bandRecruitmentInstrumentRepository.findById(
                        request.getBandRecruitmentInstrumentsId())
                .orElseThrow(() -> {
                    log.warn("Instrument position not found with ID: {}", request.getBandRecruitmentInstrumentsId());
                    return new ExceptionHandler(ErrorStatus.RECRUITMENT_INSTRUMENT_NOT_FOUND);
                });

        // 악기 포지션이 해당 구인 공고의 것인지 확인
        if (!instrument.getBandRecruitment().getBandRecruitmentId().equals(recruitmentId)) {
            log.warn(
                    "Instrument position does not belong to the recruitment. InstrumentId: {}, RecruitmentId: {}, ActualRecruitmentId: {}",
                    instrument.getBandRecruitmentInstrumentsId(), recruitmentId,
                    instrument.getBandRecruitment().getBandRecruitmentId());
            throw new ExceptionHandler(ErrorStatus.INVALID_RECRUITMENT_INSTRUMENT);
        }

        // 지원자 본인 정보 조회
        Member applicant = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("Applicant not found with ID: {}", memberId);
                    return new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND);
                });

        // 이미 해당 포지션에 지원했는지 확인
        boolean alreadyApplied = bandRecruitmentApplicantRepository.existsByBandRecruitmentInstrumentBandRecruitmentInstrumentsIdAndApplicantMemberId(
                instrument.getBandRecruitmentInstrumentsId(), memberId);

        if (alreadyApplied) {
            log.warn("Applicant already applied to this position. MemberId: {}, InstrumentId: {}",
                    memberId, instrument.getBandRecruitmentInstrumentsId());
            throw new ExceptionHandler(ErrorStatus.RECRUITMENT_APPLICANT_ALREADY_EXISTS);
        }

        // 지원자의 악기 티어가 요구 티어를 충족하는지 확인
        validateApplicantTier(applicant, instrument);

        // 지원자가 이미 밴드 멤버인지 확인
        validateNotAlreadyBandMember(applicant, bandRecruitment.getBand().getBandId());

        // BandRecruitmentApplicant 엔티티 생성 및 저장
        BandRecruitmentApplicant applicantEntity = BandRecruitmentApplicant.createApplicant(
                applicant, instrument, LocalDateTime.now());

        // 악기 포지션과 연결
        instrument.addApplicant(applicantEntity);

        // 저장
        applicantEntity = bandRecruitmentApplicantRepository.save(applicantEntity);

        log.info("Application submitted successfully. ApplicantId: {}, RecruitmentId: {}, InstrumentId: {}",
                applicantEntity.getBandRecruitmentApplicantId(), recruitmentId,
                instrument.getBandRecruitmentInstrumentsId());

        Tier tier = getMemberTier(applicant, instrument.getInstrument());

        // 저장된 엔티티를 DTO로 변환하여 반환
        return recruitmentConverter.toApplicantDTO(applicantEntity, tier);
    }

    @Override
    public void cancelRecruitmentApplication(Long recruitmentId, UUID applicantId, UUID requesterId) {
        log.info("Canceling application. RecruitmentId: {}, ApplicantId: {}, RequesterId: {}",
                recruitmentId, applicantId, requesterId);

        // 구인 공고 존재 여부 확인
        BandRecruitment bandRecruitment = bandRecruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    log.warn("Recruitment not found with ID: {}", recruitmentId);
                    return new ExceptionHandler(ErrorStatus.RECRUITMENT_NOT_FOUND);
                });

        // 지원 정보 조회
        BandRecruitmentApplicant applicantInfo = bandRecruitmentApplicantRepository
                .findByBandRecruitmentInstrumentBandRecruitmentBandRecruitmentIdAndApplicantMemberId(recruitmentId,
                        applicantId)
                .orElseThrow(() -> {
                    log.warn("Application not found for applicant: {} in recruitment: {}", applicantId, recruitmentId);
                    return new ExceptionHandler(ErrorStatus.RECRUITMENT_APPLICANT_NOT_FOUND);
                });

        // 권한 확인 (본인이거나 밴드 마스터만 취소 가능)
        Band band = bandRecruitment.getBand();
        boolean isBandMaster = band.getBandMasterId().equals(requesterId);
        boolean isApplicant = applicantId.equals(requesterId);

        if (!isBandMaster && !isApplicant) {
            log.warn("Unauthorized attempt to cancel application. ApplicantId: {}, RequesterId: {}",
                    applicantId, requesterId);
            throw new ExceptionHandler(ErrorStatus.UNAUTHORIZED_ACCESS);
        }

        // 이미 처리된 지원인지 확인 (취소는 PENDING 상태만 가능)
        if (applicantInfo.getStatus() != BandRecruitmentApplicant.ApplicantStatus.PENDING && !isBandMaster) {
            log.warn("Cannot cancel processed application with status: {}", applicantInfo.getStatus());
            throw new ExceptionHandler(ErrorStatus.RECRUITMENT_APPLICANT_ALREADY_PROCESSED);
        }

        try {
            // 지원 정보 삭제
            bandRecruitmentApplicantRepository.delete(applicantInfo);
            log.info("Application canceled successfully. ApplicantId: {}, RecruitmentId: {}",
                    applicantId, recruitmentId);
        } catch (Exception e) {
            log.error("Error canceling application: {}", e.getMessage());
            throw new ExceptionHandler(ErrorStatus.RECRUITMENT_APPLICANT_DELETE_FAILED);
        }
    }

    @Override
    public List<RecruitmentInstrumentDTO> getRecruitmentInstruments(Long bandId, Long recruitmentId) {
        log.info("Getting instruments for band ID: {} and recruitment ID: {}", bandId, recruitmentId);

        // 밴드 존재 여부 확인
        if (!bandRepository.existsById(bandId)) {
            log.warn("Band not found with ID: {}", bandId);
            throw new ExceptionHandler(ErrorStatus.BAND_NOT_FOUND);
        }

        // 구인 공고 존재 여부 확인 및 밴드 소유권 검증
        BandRecruitment recruitment = validateRecruitmentAccess(recruitmentId, bandId);

        // 구인 공고의 모집 악기 목록 조회
        List<BandRecruitmentInstrument> instruments = bandRecruitmentInstrumentRepository.findByBandRecruitmentBandRecruitmentId(
                recruitmentId);

        // 엔티티 목록을 DTO 목록으로 변환하여 반환
        return instruments.stream()
                .map(recruitmentConverter::toRecruitmentInstrumentDTO)
                .collect(Collectors.toList());
    }

    // 구인 공고 지원자 목록 조회
    @Override
    public List<ApplicantDTO> getRecruitmentApplicants(Long bandId, Long recruitmentId, UUID memberId) {
        log.info("Getting applicants for band ID: {} and recruitment ID: {}, requested by: {}",
                bandId, recruitmentId, memberId);

        // 밴드 존재 여부 확인
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> {
                    log.warn("Band not found with ID: {}", bandId);
                    return new ExceptionHandler(ErrorStatus.BAND_NOT_FOUND);
                });

        // 구인 공고 존재 여부 확인 및 밴드 소유권 검증
        BandRecruitment bandRecruitment = validateRecruitmentAccess(recruitmentId, bandId);

        // 요청자가 밴드 멤버인지 확인 - 비회원도 가능
//        validateBandMemberAccess(band, memberId);

        // 구인 공고의 모든 지원자 목록 조회
        List<BandRecruitmentApplicant> applicants = bandRecruitmentApplicantRepository
                .findByBandRecruitmentInstrumentBandRecruitmentBandRecruitmentId(recruitmentId);

        log.info("Found {} applicants for recruitment ID: {}", applicants.size(), recruitmentId);

        // 엔티티 목록을 DTO 목록으로 변환하여 반환
        return applicants.stream()
                .map(applicant -> {
                    Tier tier = getMemberTier(applicant.getApplicant(),
                            applicant.getBandRecruitmentInstrument().getInstrument());
                    return recruitmentConverter.toApplicantDTO(applicant, tier);
                })
                .collect(Collectors.toList());
    }

    // 지원자 거절
    @Override
    public void rejectApplicant(Long bandId, Long recruitmentId, UUID applicantId, UUID memberId) {
        log.info("Rejecting applicant. BandId: {}, RecruitmentId: {}, ApplicantId: {}, MemberId: {}",
                bandId, recruitmentId, applicantId, memberId);

        // 밴드 존재 여부 및 권한 확인
        Band band = validateBandAndMemberAccess(bandId, memberId);

        // 구인 공고 존재 여부 확인 및 밴드 소유권 검증
        BandRecruitment bandRecruitment = validateRecruitmentAccess(recruitmentId, bandId);

        // 구인 공고 상태 확인 (COMPLETED 상태일 경우 거절 불가)
        validateRecruitmentActive(bandRecruitment);

        // 지원자 존재 여부 확인
        Member applicant = memberRepository.findById(applicantId)
                .orElseThrow(() -> {
                    log.warn("Applicant not found with ID: {}", applicantId);
                    return new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND);
                });

        // 해당 지원자의 지원 정보 조회
        BandRecruitmentApplicant applicantInfo = bandRecruitmentApplicantRepository.findByBandRecruitmentInstrumentBandRecruitmentBandRecruitmentIdAndApplicantMemberId(
                        recruitmentId, applicantId)
                .orElseThrow(() -> {
                    log.warn("Application not found for applicant: {} in recruitment: {}", applicantId, recruitmentId);
                    return new ExceptionHandler(ErrorStatus.RECRUITMENT_APPLICANT_NOT_FOUND);
                });

        // 지원 상태가 이미 처리되었는지 확인 (PENDING 상태만 거절 가능)
        if (applicantInfo.getStatus() != BandRecruitmentApplicant.ApplicantStatus.PENDING) {
            log.warn("Application status is already processed: {}", applicantInfo.getStatus());
            throw new ExceptionHandler(ErrorStatus.RECRUITMENT_APPLICANT_ALREADY_PROCESSED);
        }

        // 지원자 상태를 '거절됨'으로 업데이트
        applicantInfo.reject();
        bandRecruitmentApplicantRepository.save(applicantInfo);

        // 선택적: 거절된 지원자에게 알림 전송
        sendRejectionNotification(applicantInfo);

        log.info("Applicant rejected successfully. ApplicantId: {}, RecruitmentId: {}",
                applicantId, recruitmentId);
    }

    // 공고에서 악기 포지션 제거
    @Override
    public void removeRecruitmentInstrument(Long bandId, Long recruitmentId, Long instrumentId, UUID memberId) {
        log.info("Removing instrument from recruitment. BandId: {}, RecruitmentId: {}, InstrumentId: {}, MemberId: {}",
                bandId, recruitmentId, instrumentId, memberId);

        // 밴드 존재 여부 및 권한 확인
        Band band = validateBandAndMemberAccess(bandId, memberId);

        // 구인 공고 존재 여부 확인 및 밴드 소유권 검증
        BandRecruitment bandRecruitment = validateRecruitmentAccess(recruitmentId, bandId);

        // 구인 공고 상태 확인 (COMPLETED 상태일 경우 제거 불가)
        validateRecruitmentActive(bandRecruitment);

        // 악기 포지션 존재 여부 확인
        BandRecruitmentInstrument instrument = bandRecruitmentInstrumentRepository.findById(instrumentId)
                .orElseThrow(() -> {
                    log.warn("Instrument position not found with ID: {}", instrumentId);
                    return new ExceptionHandler(ErrorStatus.RECRUITMENT_INSTRUMENT_NOT_FOUND);
                });

        // 악기 포지션이 해당 구인 공고의 것인지 확인
        if (!instrument.getBandRecruitment().getBandRecruitmentId().equals(recruitmentId)) {
            log.warn(
                    "Instrument position does not belong to the recruitment. InstrumentId: {}, RecruitmentId: {}, ActualRecruitmentId: {}",
                    instrument.getBandRecruitmentInstrumentsId(), recruitmentId,
                    instrument.getBandRecruitment().getBandRecruitmentId());
            throw new ExceptionHandler(ErrorStatus.INVALID_RECRUITMENT_INSTRUMENT);
        }

        // 해당 포지션에 지원자가 있는지 확인 (선택적 검증)
//        validateNoActiveApplicants(instrument);

        try {
            // 구인 공고에서 악기 포지션 제거
            bandRecruitment.removeInstrument(instrument);

            // 악기 포지션 삭제
            bandRecruitmentInstrumentRepository.delete(instrument);

            log.info("Instrument position removed successfully. InstrumentId: {}, RecruitmentId: {}",
                    instrumentId, recruitmentId);
        } catch (Exception e) {
            log.error("Error removing instrument position: {}", e.getMessage());
            throw new ExceptionHandler(ErrorStatus.RECRUITMENT_INSTRUMENT_DELETE_FAILED);
        }
    }

    private Tier getMemberTier(Member member, Instrument instrument) {
        try {
            // MemberRank 테이블에서 해당 멤버의 특정 악기 티어 조회
            MemberRank memberRank = memberRankRepository.findByMemberMemberIdAndInstrument(
                            member.getMemberId(), instrument)
                    .orElse(null);

            // 조회 결과가 있으면 해당 티어 반환, 없으면 UNRANKED
            if (memberRank != null) {
                // MemberRank의 Tier 타입을 BandRecruitment의 Tier 타입으로 변환
                String tierName = memberRank.getTier().name();
                return Tier.valueOf(tierName);
            }
        } catch (Exception e) {
            log.warn("Error retrieving tier for member: {} and instrument: {}: {}",
                    member.getMemberId(), instrument, e.getMessage());
        }

        // 티어 정보가 없거나 오류 발생 시 UNRANKED 반환
        return Tier.UNRANKED;
    }

    private Band validateBandAndMemberAccess(Long bandId, UUID memberId) {
        // 밴드 존재 여부 확인
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> {
                    log.warn("Band not found with ID: {}", bandId);
                    return new ExceptionHandler(ErrorStatus.BAND_NOT_FOUND);
                });

        // 요청자가 밴드 마스터인지 확인
        if (!band.getBandMasterId().equals(memberId)) {
            log.warn("Unauthorized access. MemberId: {} is not the band master of BandId: {}",
                    memberId, bandId);
            throw new ExceptionHandler(ErrorStatus.UNAUTHORIZED_ACCESS);
        }

        return band;
    }

    /**
     * 지원자 티어 검증 (선택적) 지원자의 티어가 포지션에서 요구하는 최소 티어를 충족하는지 확인
     *
     * @param applicant  지원자
     * @param instrument 악기 포지션
     */
    private void validateApplicantTier(Member applicant, BandRecruitmentInstrument instrument) {
        // MemberRank에서 지원자의 해당 악기 티어 정보 조회
        MemberRank memberRank;
        try {
            memberRank = memberRankRepository.findByMemberMemberIdAndInstrument(
                            applicant.getMemberId(), instrument.getInstrument())
                    .orElseThrow(() -> {
                        log.warn("Member rank not found for applicant: {} and instrument: {}",
                                applicant.getMemberId(), instrument.getInstrument());
                        return new ExceptionHandler(ErrorStatus.MEMBER_RANK_NOT_FOUND);
                    });
        } catch (Exception e) {
            log.error("Error retrieving member rank: {}", e.getMessage());
            throw new ExceptionHandler(ErrorStatus.MEMBER_RANK_NOT_FOUND);
        }

        // 지원자의 티어와 요구 티어 비교
        com.mickey.dinggading.domain.memberrank.model.Tier applicantTier = memberRank.getTier();
        Tier requiredTier = instrument.getRequiredTier();

        // 티어 비교: 지원자 티어의 ordinal 값이 요구 티어의 ordinal 값보다 작다면 요구 조건 미충족
        if (applicantTier.ordinal() < requiredTier.ordinal()) {
            log.warn("Applicant tier too low. Applicant: {}, Required: {}, Actual: {}",
                    applicant.getMemberId(), requiredTier, applicantTier);
            throw new ExceptionHandler(ErrorStatus.INSUFFICIENT_TIER);
        }

        log.info("Applicant tier validated. MemberId: {}, Instrument: {}, RequiredTier: {}, ApplicantTier: {}",
                applicant.getMemberId(), instrument.getInstrument(), requiredTier, applicantTier);
    }

    private BandRecruitment validateRecruitmentAccess(Long recruitmentId, Long bandId) {
        // 구인 공고 존재 여부 확인
        BandRecruitment recruitment = bandRecruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    log.warn("Recruitment not found with ID: {}", recruitmentId);
                    return new ExceptionHandler(ErrorStatus.RECRUITMENT_NOT_FOUND);
                });

        // 구인 공고가 해당 밴드의 것인지 확인
        if (!recruitment.getBand().getBandId().equals(bandId)) {
            log.warn(
                    "Recruitment does not belong to band. RecruitmentId: {}, RecruitmentBandId: {}, RequestedBandId: {}",
                    recruitment.getBandRecruitmentId(), recruitment.getBand().getBandId(), bandId);
            throw new ExceptionHandler(ErrorStatus.UNAUTHORIZED_ACCESS);
        }

        return recruitment;
    }

    /**
     * 구인 공고가 활성 상태인지 확인 (RECRUITING 상태여야 함)
     */
    private void validateRecruitmentActive(BandRecruitment recruitment) {
        if (RecruitmentStatus.COMPLETED.equals(recruitment.getStatus())) {
            log.warn("Cannot update completed recruitment. RecruitmentId: {}", recruitment.getBandRecruitmentId());
            throw new ExceptionHandler(ErrorStatus.RECRUITMENT_ALREADY_COMPLETED);
        } else if (RecruitmentStatus.READY.equals(recruitment.getStatus())) {
            throw new ExceptionHandler(ErrorStatus.RECRUITMENT_NOT_STARTED);
        }
    }

    private void validateNotAlreadyBandMember(Member applicant, Long bandId) {
        boolean isAlreadyMember = bandMemberRepository.existsByBandBandIdAndMemberMemberId(
                bandId, applicant.getMemberId());

        if (isAlreadyMember) {
            log.warn("Applicant is already a band member. MemberId: {}, BandId: {}",
                    applicant.getMemberId(), bandId);
            throw new ExceptionHandler(ErrorStatus.RECRUITMENT_ALREADY_BAND_MEMBER);
        }
    }

    private boolean isDuplicateInstrument(BandRecruitment bandRecruitment, Instrument instrument) {
        return bandRecruitment.getInstruments().stream()
                .anyMatch(i -> i.getInstrument() == instrument);
    }

    /**
     * 활성화된 지원자가 없는지 확인 (선택적 검증) 이미 승인된 지원자가 있는 경우 포지션 제거를 막을 수 있음
     *
     * @param instrument 악기 포지션
     */
    private void validateNoActiveApplicants(BandRecruitmentInstrument instrument) {
        // 선택적 검증: 승인된 지원자가 있는 경우 제거 불가
        long acceptedCount = instrument.getApplicants().stream()
                .filter(applicant -> BandRecruitmentApplicant.ApplicantStatus.ACCEPTED == applicant.getStatus())
                .count();

        if (acceptedCount > 0) {
            log.warn("Cannot remove instrument position with accepted applicants. InstrumentId: {}, AcceptedCount: {}",
                    instrument.getBandRecruitmentInstrumentsId(), acceptedCount);
            throw new ExceptionHandler(ErrorStatus.RECRUITMENT_INSTRUMENT_HAS_ACCEPTED_APPLICANTS);
        }
    }

    /**
     * 거절된 지원자에게 알림 전송 (선택적 구현)
     *
     * @param applicantInfo 지원 정보
     */
    private void sendRejectionNotification(BandRecruitmentApplicant applicantInfo) {
        // 실제 알림 전송 로직은 요구사항에 따라 구현
        // 예: 푸시 알림, 이메일, 앱 내 알림 등

        try {
            log.info("Sending rejection notification to applicant: {}",
                    applicantInfo.getApplicant().getMemberId());

            // 알림 메시지 생성
            String message = String.format(
                    "%s 밴드의 %s 포지션 지원이 거절되었습니다.",
                    applicantInfo.getBandRecruitmentInstrument().getBandRecruitment().getBand().getName(),
                    applicantInfo.getBandRecruitmentInstrument().getInstrument().name()
            );

            // 알림 엔티티 생성 및 저장 로직 (구현 필요)
            // notificationService.send(applicantInfo.getApplicant().getMemberId(), message);

        } catch (Exception e) {
            // 알림 전송 실패는 전체 프로세스에 영향을 주지 않도록 처리
            log.error("Failed to send rejection notification: {}", e.getMessage());
        }
    }

    /**
     * 밴드 멤버 접근 권한 확인 요청자가 밴드 마스터이거나 밴드 멤버인지 확인
     *
     * @param band     밴드 엔티티
     * @param memberId 요청자 ID
     */
    private void validateBandMemberAccess(Band band, UUID memberId) {
        // 밴드 마스터인 경우 즉시 통과
        if (band.getBandMasterId().equals(memberId)) {
            return;
        }

        // 밴드 멤버인지 확인
        boolean isBandMember = bandMemberRepository.existsByBandBandIdAndMemberMemberId(
                band.getBandId(), memberId);

        if (!isBandMember) {
            log.warn("Unauthorized access to band applicants. MemberId: {}, BandId: {}",
                    memberId, band.getBandId());
            throw new ExceptionHandler(ErrorStatus.UNAUTHORIZED_ACCESS);
        }
    }

    private void validateNoActiveRecruitment(Long bandId) {
        boolean hasActiveRecruitment = bandRecruitmentRepository.existsByBandBandIdAndStatus(bandId,
                RecruitmentStatus.RECRUITING);
        if (hasActiveRecruitment) {
            log.warn("Band already has an active recruitment. BandId: {}", bandId);
            throw new ExceptionHandler(ErrorStatus.RECRUITMENT_ALREADY_EXISTS);
        }
    }

    // 오디션 곡 존재 여부 확인
    private Song getSong(Long songId) {
        return songRepository.findById(songId)
                .orElseThrow(() -> {
                    log.warn("Song not found with ID: {}", songId);
                    return new ExceptionHandler(ErrorStatus.SONG_NOT_FOUND);
                });
    }

    // 구인 공고 존재 여부 확인
    private BandRecruitment getRecruitment(Long recruitmentId) {
        return bandRecruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    log.warn("Recruitment not found with ID: {}", recruitmentId);
                    return new ExceptionHandler(ErrorStatus.RECRUITMENT_NOT_FOUND);
                });
    }

}