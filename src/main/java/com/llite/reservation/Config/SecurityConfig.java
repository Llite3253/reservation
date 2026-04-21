package com.llite.reservation.Config;

import com.llite.reservation.Jwt.*;
import com.llite.reservation.OAuth.OAuth2SuccessHandler;
import com.llite.reservation.Service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler, JwtExceptionFilter jwtExceptionFilter) throws Exception {
        http
                // 기본 설정 비활성화
                .httpBasic((basic) -> basic.disable()) // UI를 사용하는 기본 인증 비활성화 (api를 사용하기 때문에 UI를 사용안함)
                .csrf((csrf) -> csrf.disable()) // CSRF 보안 비활성화 (api 서버는 인증 정보(OAuth, Jwt)를 포함하여 전송하기 때문에 불필요

                // 세션 미사용 설정
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling((exception) -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))

                .authorizeHttpRequests((auth) -> auth
                        // 1. 누구나 접속 가능한 주소 정의
                        .requestMatchers("/", "/login", "/join", "/test").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/member/test").hasRole("USER")

                        // 2. 나머지는 무조건 인증(로그인)해야 접속
                        .anyRequest().authenticated()
                )

                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfo) -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2SuccessHandler)
                )

                // JWT 필터 등록
                // 기존의 UsernamePasswordAuthenticationFilter 앞에 우리가 만든 필터를 꺼워 넣음
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
