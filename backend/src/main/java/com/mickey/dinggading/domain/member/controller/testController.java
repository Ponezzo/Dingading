package com.mickey.dinggading.domain.member.controller;

import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.member.model.entity.Member;
import com.mickey.dinggading.domain.member.model.entity.Token;
import com.mickey.dinggading.domain.member.repository.MemberRepository;
import com.mickey.dinggading.exception.ExceptionHandler;
import com.mickey.dinggading.infra.rabbitmq.dto.MessageDTO;
import com.mickey.dinggading.util.GenerateRandomNickname;
import com.mickey.dinggading.util.JWTUtil;
import com.mickey.dinggading.util.SecurityUtil;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class testController {

    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final RabbitTemplate rabbitTemplate;
    private final SecurityUtil securityUtil;

    @Value("${rabbitmq.audio-analysis-response-exchange.name}")
    private String audioAnalysisResponseExchangeName;

    @Value("${rabbitmq.audio-analysis-response.routing.key}")
    private String audioAnalysisResponseRoutingKey;

    /**
     * 테스트용 RabbitMQ 메시지 발송 엔드포인트 실제 FastAPI 서비스의 응답을 시뮬레이션합니다.
     */
    @PostMapping("/send-rabbitmq-message")
    public ResponseEntity<?> sendRabbitMQMessage(@RequestBody MessageDTO messageDto) {
        log.info("테스트용 RabbitMQ 메시지 발송: {}", messageDto);

        // 메시지 전송
        rabbitTemplate.convertAndSend(
                audioAnalysisResponseExchangeName,
                audioAnalysisResponseRoutingKey,
                messageDto
        );

        // 응답
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "RabbitMQ 메시지가 성공적으로 발송되었습니다.");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * 테스트용 RabbitMQ 메시지 발송 엔드포인트 (역방향 - 응답 처리용) 이 엔드포인트는 직접 JsonMessageConsumer를 테스트하기 위해 사용됩니다.
     */
    @PostMapping("/trigger-consumer")
    public ResponseEntity<?> triggerConsumer(@RequestBody Map<String, Object> request) {
        log.info("테스트용 JsonMessageConsumer 트리거: {}", request);

        UUID senderId;
        try {
            senderId = UUID.fromString(request.get("senderId").toString());
        } catch (Exception e) {
            log.error("Invalid senderId format", e);
            return ResponseEntity.badRequest().body("유효하지 않은 senderId 형식입니다.");
        }

        // MessageDTO 생성
        MessageDTO messageDto = new MessageDTO(
                request.get("content"),
                senderId,
                Long.parseLong((String) request.get("attemptId")),
                (String) request.get("recoredFilename"),
                (String) request.get("originalFilename"),
                Integer.parseInt((String) request.get("beat_score")),
                Integer.parseInt((String) request.get("tune_score")),
                Integer.parseInt((String) request.get("tone_score")),
                LocalDateTime.now()
        );

        // 메시지 전송
        rabbitTemplate.convertAndSend(
                audioAnalysisResponseExchangeName,
                audioAnalysisResponseRoutingKey,
                messageDto
        );

        // 응답
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Consumer 트리거 메시지가 성공적으로 발송되었습니다.");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * 테스트용 멤버 생성 엔드포인트
     */
    @PostMapping("/create-member")
    public ResponseEntity<?> createTestMember() {
        Member member = Member.createMember(UUID.randomUUID().toString(),
                GenerateRandomNickname.generateRandomNickname(), "");
        Member savedMember = memberRepository.save(member);
        return ResponseEntity.ok().body(savedMember);
    }

    private Member getMember2(UUID targetUserId) {
        return memberRepository.findById(targetUserId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    /**
     * 테스트용 토큰 생성 엔드포인트
     */
    @PostMapping("/create-token")
    public ResponseEntity<?> createTestToken(@RequestBody Map<String, String> request) {
        String memberIdStr = request.get("memberId");
        UUID memberId = UUID.fromString(memberIdStr);

        // getMember 메서드 사용
        Member member = getMember2(memberId);

        Token token = jwtUtil.createAccessToken(member);

        return ResponseEntity.ok(Map.of("token", token.getAccessToken()));
    }

}