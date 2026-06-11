package com.mickey.dinggading.util;

import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.exception.ExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityUtil {

    private final JWTUtil jwtUtil;
    private final HttpServletRequest request;

    public UUID getCurrentMemberId() {
        // 요청 헤더에서 Authorization 값을 가져옴

        String authorizationHeader = request.getHeader("Authorization");
        log.info("Authorization header: {}", authorizationHeader);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.error("Authorization 헤더가 없거나 Bearer 토큰이 아닙니다.");
            throw new ExceptionHandler(ErrorStatus._UNAUTHORIZED);
        }
        // JWTUtil을 사용하여 토큰에서 memberId 추출
        return jwtUtil.getMemberId(authorizationHeader);
    }
}
