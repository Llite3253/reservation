package com.llite.reservation.Service;

import com.llite.reservation.Dto.Reqeust.JoinRequest;
import com.llite.reservation.Dto.Reqeust.LoginRequest;
import com.llite.reservation.Entity.Member;
import com.llite.reservation.Entity.RefreshToken;
import com.llite.reservation.Jwt.JwtToken;
import com.llite.reservation.Jwt.JwtTokenProvider;
import com.llite.reservation.Repository.MemberRepository;
import com.llite.reservation.Repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public JwtToken login(LoginRequest reqeust) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(reqeust.getUsername(), reqeust.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JwtToken jwtToken = jwtTokenProvider.createToken(authentication);

        RefreshToken refreshToken = new RefreshToken(authentication.getName(), jwtToken.getRefreshToken());
        refreshTokenRepository.save(refreshToken);

        return jwtToken;
    }

    @Transactional
    public JwtToken reissue(String refreshToken) {
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        RefreshToken redisToken = refreshTokenRepository.findById(authentication.getName())
                .orElseThrow(() -> new RuntimeException("토큰의 유저 정보가 일치하지 않습니다."));

        if(!redisToken.getRefreshToken().equals(refreshToken)) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        JwtToken newJwtToken = jwtTokenProvider.createToken(authentication);

        redisToken.updateRefreshToken(newJwtToken.getRefreshToken());
        refreshTokenRepository.save(redisToken);

        return newJwtToken;
    }

    @Transactional
    public void join(JoinRequest request) {
        if(memberRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Member member = Member.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .role("USER")
                .build();

        memberRepository.save(member);
    }
}
