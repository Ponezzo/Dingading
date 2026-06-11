package com.mickey.dinggading.domain.oauth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final Environment environment;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        log.error("OAuth 인증 실패: {}", exception.getMessage());

        String errorMessage;
        Throwable cause = exception.getCause();

        if (cause != null) {
            errorMessage = cause.getMessage();
        } else {
            errorMessage = exception.getMessage();
        }

        String redirectUri = environment.getProperty("oauth2.redirectUri") == null ?
                "https://j12e107.p.ssafy.io/login" : environment.getProperty("oauth2.redirectUri");

        log.error("Error Message: {}", errorMessage);

        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("error", encodedMessage)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
