package com.mickey.dinggading.domain.oauth.service;

import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.member.model.entity.Member;
import com.mickey.dinggading.domain.member.model.entity.Token;
import com.mickey.dinggading.domain.member.repository.MemberRepository;
import com.mickey.dinggading.domain.member.service.NotificationService;
import com.mickey.dinggading.domain.oauth.MemberPrincipal;
import com.mickey.dinggading.exception.ExceptionHandler;
import com.mickey.dinggading.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final NotificationService notificationService;
    private final Environment environment;

    public SseEmitter subscribe(UUID memberId) {
        return notificationService.subscribe(memberId);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication)
            throws IOException {

        log.info("onAuthenticationSuccess 호출");
        MemberPrincipal userPrincipal = (MemberPrincipal) authentication.getPrincipal();
        Member member = memberRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 사용자 로그인 시 알림 설정
        subscribe(member.getMemberId());

        // 토큰 생성
        Token jwtToken = jwtUtil.createAccessToken(member);
        log.info("jwtToken: {}", jwtToken);

        String redirectUri = environment.getProperty("oauth2.frontRedirectUri") == null ?
                "https://j12e107.p.ssafy.io/login" : environment.getProperty("oauth2.frontRedirectUri");

        response.setHeader("Authorization", "Bearer " + jwtToken.getAccessToken());
        log.info("uri {}", redirectUri);

        // 토큰과 사용자 정보를 함께 전달
        // URL 파라미터 인코딩
        String redirectUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", jwtToken.getAccessToken())
                .queryParam("loginId", member.getUsername())
                .queryParam("nickname", member.getNickname())
                .queryParam("profileImg", member.getProfileImgUrl() != null ? member.getProfileImgUrl() : "")
                .build()
                .encode()  // 한 번만 인코딩
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
