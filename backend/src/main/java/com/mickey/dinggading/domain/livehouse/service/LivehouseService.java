package com.mickey.dinggading.domain.livehouse.service;

import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.livehouse.dto.LivehouseDTO;
import com.mickey.dinggading.domain.livehouse.dto.LivehouseSessionDTO;
import com.mickey.dinggading.domain.livehouse.dto.ParticipantDTO;
import com.mickey.dinggading.domain.livehouse.entity.Livehouse;
import com.mickey.dinggading.domain.livehouse.entity.Participant;
import com.mickey.dinggading.domain.livehouse.repository.LivehouseRepository;
import com.mickey.dinggading.domain.livehouse.repository.ParticipantRepository;
import com.mickey.dinggading.domain.member.model.entity.Member;
import com.mickey.dinggading.domain.member.repository.MemberRepository;
import com.mickey.dinggading.exception.ExceptionHandler;
import com.mickey.dinggading.model.CreateLivehouseRequestDTO;
import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.ConnectionType;
import io.openvidu.java.client.MediaMode;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.RecordingMode;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LivehouseService {

    private final LivehouseRepository livehouseRepository;
    private final ParticipantRepository participantRepository;
    private final NicknameService nicknameService;
    private final MemberRepository memberRepository;

    private final OpenVidu openVidu;

    @Transactional(readOnly = true)
    public Page<LivehouseDTO> listLivehouses(Pageable pageable) {

        openViduFetch();

        List<String> activeSessions = openVidu.getActiveSessions().stream().map(Session::getSessionId).toList();
        return livehouseRepository.findBySessionIdInOrderByParticipantCountDesc(activeSessions, pageable)
                .map(LivehouseDTO::fromEntity);
    }

    private void openViduFetch() {
        try {
            openVidu.fetch();
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            throw new RuntimeException(e);
        }
    }

    public Page<LivehouseDTO> searchLivehouses(String keyword, Pageable pageable) {
        List<String> activeSessions = openVidu.getActiveSessions().stream().map(Session::getSessionId).toList();
        return livehouseRepository.searchByKeyword(activeSessions, keyword, pageable)
                .map(LivehouseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public LivehouseDTO getLivehouse(Long livehouseId) {
        Livehouse livehouse = livehouseRepository.findById(livehouseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "라이브하우스를 찾을 수 없습니다."));
        return LivehouseDTO.fromEntity(livehouse);
    }

    @Transactional
    public LivehouseSessionDTO createLivehouse(com.mickey.dinggading.model.CreateLivehouseRequestDTO requestDTO,
                                               UUID memberId) {
        try {
            Member member = memberRepository.findByMemberId(memberId)
                    .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

            List<String> activeSessions = openVidu.getActiveSessions().stream().map(Session::getSessionId).toList();
            Optional<Livehouse> livehouse = livehouseRepository.findBySessionIdInAndHostId(activeSessions,
                    member.getMemberId());

            if (livehouse.isPresent()) {
                throw new ResponseStatusException(HttpStatus.FOUND, "이미 방을 가진 사용자입니다.");
            }

            // 방을 openvidu에서 만들기
            Session session = createOpenViduSession();
            String sessionId = session.getSessionId();

            // 방 DB에 저장하기
            Livehouse newLivehouse = saveLivehouse(requestDTO, memberId, member, sessionId);

            // 방장을 openvidu 세션에 넣기
            Connection connection = createConnectionToken(session);

            // 방장을 DB에 저장하기
            Participant host = saveParticipant(newLivehouse, member, connection);

            return LivehouseSessionDTO.builder()
                    .livehouseId(newLivehouse.getLivehouseId())
                    .sessionId(sessionId)
                    .token(connection.getToken())
                    .participantId(host.getParticipantId())
                    .nickname(member.getNickname())
                    .build();

        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "OpenVidu 서버 오류", e);
        }
    }

    @NotNull
    private Participant saveParticipant(Livehouse newLivehouse, Member member, Connection connection) {
        // 호스트를 첫 번째 참여자로 등록
        Participant host = Participant.builder()
                .livehouse(newLivehouse)
                .nickname(member.getNickname())
                .connectionId(connection.getConnectionId())
                .isHost(true)
                .joinedAt(LocalDateTime.now())
                .build();

        participantRepository.save(host);
        return host;
    }

    @NotNull
    private Livehouse saveLivehouse(CreateLivehouseRequestDTO requestDTO, UUID memberId, Member member,
                                    String sessionId) {
        // 라이브하우스 엔티티 생성
        Livehouse newLivehouse = Livehouse.builder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .hostId(memberId)
                .hostNickname(member.getNickname())
                .sessionId(sessionId)
                .createdAt(LocalDateTime.now())
                .maxParticipants(requestDTO.getMaxParticipants())
                .status(Livehouse.LivehouseStatus.ACTIVE)
                .build();

        livehouseRepository.save(newLivehouse);
        return newLivehouse;
    }

    // LivehouseService.java의 joinLivehouse 메서드 수정
    @Transactional
    public LivehouseSessionDTO joinLivehouse(Long livehouseId, UUID memberId) {
        openViduFetch();

        // 멤버 찾기
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<String> activeSessions = openVidu.getActiveSessions().stream().map(Session::getSessionId).toList();

        // 세션이 살아있는 라이브 하우스인지 찾기
        Livehouse livehouse = livehouseRepository.findBySessionIdInAndLivehouseId(activeSessions, livehouseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "라이브하우스를 찾을 수 없습니다."));

        if (livehouse.getStatus() == Livehouse.LivehouseStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "종료된 라이브하우스입니다.");
        }

        if (livehouse.getParticipantCount() >= livehouse.getMaxParticipants()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "라이브하우스가 가득 찼습니다.");
        }

        try {
            Session session = openVidu.getActiveSession(livehouse.getSessionId());
            if (session == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "OpenVidu 세션을 찾을 수 없습니다.");
            }

            ConnectionProperties properties = new ConnectionProperties.Builder()
                    .type(ConnectionType.WEBRTC)
                    .role(OpenViduRole.PUBLISHER)
                    .data(String.format("{\"nickname\": \"%s\"}", member.getNickname()))
                    .build();

            // 세션에 대한 연결 생성
            Connection connection = session.createConnection(properties);
            String token = connection.getToken();

            // 기존 참가자 찾기
            Optional<Participant> existingParticipant = participantRepository.findByLivehouseAndNickname(livehouse,
                    member.getNickname());
            Participant participant;

            if (existingParticipant.isPresent()) {
                // 이미 참가 중인 경우 connectionId 업데이트
                participant = existingParticipant.get();
                participant.setConnectionId(connection.getConnectionId()); // connectionId 업데이트
                participantRepository.save(participant);
            } else {
                // 기존에 참가한 적 없다면 새로 참여자 등록
                participant = Participant.builder()
                        .livehouse(livehouse)
                        .nickname(member.getNickname())
                        .connectionId(connection.getConnectionId())
                        .isHost(false)
                        .joinedAt(LocalDateTime.now())
                        .build();
                participantRepository.save(participant);
            }

            return LivehouseSessionDTO.builder()
                    .livehouseId(livehouse.getLivehouseId())
                    .sessionId(livehouse.getSessionId())
                    .token(token)
                    .participantId(participant.getParticipantId())
                    .nickname(member.getNickname())
                    .build();

        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "OpenVidu 서버 오류: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void leaveLivehouse(Long livehouseId, Long participantId) {
        Livehouse livehouse = livehouseRepository.findById(livehouseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "라이브하우스를 찾을 수 없습니다."));

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "참여자를 찾을 수 없습니다."));

        if (!participant.getLivehouse().getLivehouseId().equals(livehouseId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 라이브하우스의 참여자가 아닙니다.");
        }

        // 방장이 나갈 경우 라이브하우스 종료
        if (participant.isHost()) {
            closeLivehouse(livehouseId, participantId);
            return;
        }

        // 참여자 삭제
        participantRepository.delete(participant);
    }

    @Transactional
    public void closeLivehouse(Long livehouseId, Long participantId) {
        Livehouse livehouse = livehouseRepository.findById(livehouseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "라이브하우스를 찾을 수 없습니다."));

        Participant participant = participantRepository.findByParticipantId(participantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "참여자를 찾을 수 없습니다."));

        if (!participant.isHost()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "라이브하우스 종료 권한이 없습니다.");
        }

        livehouse.close();

        participantRepository.delete(participant);
        livehouseRepository.delete(livehouse);
    }

    @Transactional(readOnly = true)
    public List<ParticipantDTO> listParticipants(Long livehouseId) {
        openViduFetch();
        log.info("session list -> {} ", openVidu.getActiveSessions().stream().map(Session::getSessionId).toList());
        Livehouse livehouse = livehouseRepository.findById(livehouseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "라이브하우스를 찾을 수 없습니다."));
        List<String> connectionList = openVidu.getActiveSession(livehouse.getSessionId())
                .getConnections()
                .stream()
                .map(Connection::getConnectionId)
                .toList();

        log.info("connection list -> {}", connectionList);

        return participantRepository.findByLivehouse(livehouse).stream()
                .map(ParticipantDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private Session createOpenViduSession() throws OpenViduJavaClientException, OpenViduHttpException {
        // 세션 속성 설정 - RecordingMode.MANUAL로 통일
        SessionProperties properties = new SessionProperties.Builder()
                .mediaMode(MediaMode.ROUTED)
                .recordingMode(RecordingMode.MANUAL)
                .build();

        // 세션 생성
        return openVidu.createSession(properties);
    }

    private Connection createConnectionToken(Session session)
            throws OpenViduJavaClientException, OpenViduHttpException {
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "세션을 찾을 수 없습니다.");
        }

        ConnectionProperties properties = new ConnectionProperties.Builder()
                .type(ConnectionType.WEBRTC)
                .role(OpenViduRole.PUBLISHER)
                .build();

        return session.createConnection(properties);
    }
}