package com.llite.reservation.OAuth;

import com.llite.reservation.Jwt.JwtToken;
import com.llite.reservation.Jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        JwtToken jwtToken = jwtTokenProvider.createToken(authentication);

        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/login/oauth2/code/google")
                .queryParam("accessToken", jwtToken.getAccessToken())
                .queryParam("refreshToken", jwtToken.getRefreshToken())
                .build().toUriString();

        log.info("Social Login Success! Redirect to: {}", targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
