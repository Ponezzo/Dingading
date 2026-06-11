package com.mickey.dinggading.domain.memberrank.service;

import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.member.repository.MemberRepository;
import com.mickey.dinggading.domain.memberrank.converter.MemberRankConverter;
import com.mickey.dinggading.domain.memberrank.model.Instrument;
import com.mickey.dinggading.domain.memberrank.model.MemberRank;
import com.mickey.dinggading.domain.memberrank.repository.MemberRankRepository;
import com.mickey.dinggading.exception.BaseException;
import com.mickey.dinggading.model.MemberRankDTO;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRankService {

    private final MemberRankRepository memberRankRepository;
    private final MemberRepository memberRepository;
    private final MemberRankConverter memberRankConverter;

    /**
     * 현재 로그인한 회원의 모든 악기별 랭크 정보를 조회
     *
     * @param memberId 현재 로그인한 회원 ID
     * @return 회원의 악기별 랭크 정보 DTO 리스트
     */
    public List<MemberRankDTO> getMyRanks(UUID memberId) {
        log.info("Fetching rank information for member with ID: {}", memberId);

        // 회원 존재 여부 확인
        if (!memberRepository.existsById(memberId)) {
            log.error("Member not found with ID: {}", memberId);
            throw new BaseException(ErrorStatus.MEMBER_NOT_FOUND);
        }

        // 회원의 악기별 랭크 정보 조회
        List<MemberRank> memberRanks = memberRankRepository.findByMemberMemberId(memberId);

        log.info("Found {} rank records for member with ID: {}", memberRanks.size(), memberId);

        // 엔티티를 DTO로 변환하여 반환
        return memberRankConverter.toDTOList(memberRanks);
    }

    /**
     * 현재 로그인한 회원의 특정 악기에 대한 랭크 정보를 조회
     *
     * @param memberId   현재 로그인한 회원 ID
     * @param instrument 조회할 악기 타입
     * @return 회원의 특정 악기에 대한 랭크 정보 DTO
     */
    public MemberRankDTO getMyRankByInstrument(UUID memberId, Instrument instrument) {
        log.info("Fetching rank information for member ID: {} and instrument: {}", memberId, instrument);

        // 회원 존재 여부 확인
        if (!memberRepository.existsById(memberId)) {
            log.error("Member not found with ID: {}", memberId);
            throw new BaseException(ErrorStatus.MEMBER_NOT_FOUND);
        }

        // 회원의 특정 악기에 대한 랭크 정보 조회
        MemberRank memberRank = memberRankRepository.findByMemberMemberIdAndInstrument(memberId, instrument)
                .orElseThrow(() -> {
                    log.error("Rank not found for member ID: {} and instrument: {}", memberId, instrument);
                    return new BaseException(ErrorStatus.RANK_NOT_FOUND);
                });

        log.info("Found rank record for member ID: {} and instrument: {}", memberId, instrument);

        // 엔티티를 DTO로 변환하여 반환
        return memberRankConverter.toDTO(memberRank);
    }
}