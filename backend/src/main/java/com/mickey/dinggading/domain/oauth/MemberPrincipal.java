package com.mickey.dinggading.domain.oauth;

import com.mickey.dinggading.domain.member.model.entity.Member;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@AllArgsConstructor
@Slf4j
/*
    Spring Security 에서 인증된 사용자의 정보를 담는 클래스
*/
public class MemberPrincipal implements OAuth2User, UserDetails {
    private final UUID memberId;
    private final String username;
    private final String nickname;
    private final Collection<? extends GrantedAuthority> authorities;

    @Setter
    private Map<String, Object> attributes;

    public static MemberPrincipal create(Member member) {
        List<GrantedAuthority> authorities = Collections.
                singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        return new MemberPrincipal(
                member.getMemberId(),
                member.getUsername(),
                member.getNickname(),
                authorities,
                null
        );
    }

    public static MemberPrincipal create(Member user, Map<String, Object> attributes) {
        MemberPrincipal userPrincipal = MemberPrincipal.create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getName() {
        return nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
