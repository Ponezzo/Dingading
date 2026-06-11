package com.mickey.dinggading.domain.member.controller;

import com.mickey.dinggading.api.MemberApi;
import com.mickey.dinggading.domain.member.model.entity.Member;
import com.mickey.dinggading.domain.member.model.entity.Token;
import com.mickey.dinggading.domain.member.repository.MemberRepository;
import com.mickey.dinggading.model.AuthToken;
import com.mickey.dinggading.util.GenerateRandomNickname;
import com.mickey.dinggading.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController implements MemberApi {

    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;

    @PostMapping("/api/users")
    public ResponseEntity<AuthToken> testLogin() {
        String nickname = GenerateRandomNickname.generateRandomNickname();
        Member member = Member.createMember(nickname, nickname, null);
        Member save = memberRepository.save(member);
        Token accessToken = jwtUtil.createAccessToken(save);

        AuthToken authToken = AuthToken.builder()
                .accessToken(accessToken.getAccessToken())
                .memberId(save.getMemberId())
                .build();
        return ResponseEntity.ok(authToken);
    }

}
