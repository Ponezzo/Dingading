package com.mickey.dinggading.util;

import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.member.model.entity.Member;
import com.mickey.dinggading.domain.member.repository.MemberRepository;
import com.mickey.dinggading.domain.oauth.MemberPrincipal;
import com.mickey.dinggading.exception.ExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 토큰 검증
            jwtUtil.checkTokenValidation(authorization);

            // 사용자 정보 가져오기
            UUID memberId = jwtUtil.getMemberId(authorization);
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

            // SecurityContext에 인증 정보 설정
            MemberPrincipal principal = MemberPrincipal.create(member);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            // 인증 실패 시 처리할 내용
            // 에러 로깅만 하고 필터 체인은 계속 진행
            log.error("JWT 인증 실패: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}