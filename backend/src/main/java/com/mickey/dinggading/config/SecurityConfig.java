package com.mickey.dinggading.config;

import com.mickey.dinggading.domain.member.repository.MemberRepository;
import com.mickey.dinggading.domain.oauth.service.CustomOAuth2UserService;
import com.mickey.dinggading.domain.oauth.service.OAuth2AuthenticationFailureHandler;
import com.mickey.dinggading.domain.oauth.service.OAuth2AuthenticationSuccessHandler;
import com.mickey.dinggading.util.JWTAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MemberRepository memberRepository)
            throws Exception {
        http
                // CORS 설정 (Customizer 사용)
                .cors(customizer -> customizer.configurationSource(corsConfigurationSource()))

                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())

                // 세션 정책 설정 (JWT는 세션을 사용하지 않음)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // JWT 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 엔드포인트 권한 설정
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/actuator/**").permitAll()  // Actuator 엔드포인트 허용
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated())

                // OAuth 관련
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                );

        return http.build();
    }

    // CORS 설정 소스
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001", "http://localhost:8080",
                "http://localhost:18513",
                "http://localhost:18512", "http://127.0.0.1:5500",
                "https://j12e107.p.ssafy.io"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}


