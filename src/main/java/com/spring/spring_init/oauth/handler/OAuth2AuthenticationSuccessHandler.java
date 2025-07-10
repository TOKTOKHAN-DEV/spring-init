package com.spring.spring_init.oauth.handler;

import com.spring.spring_init.common.security.jwt.TokenProvider;
import com.spring.spring_init.common.security.jwt.TokenResponseDto;
import com.spring.spring_init.oauth.PrincipalDetailsImpl;
import com.spring.spring_init.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${common.full-domain}")
    private String domain;

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {

        // 1. PrincipalDetailsImpl 꺼내기
        PrincipalDetailsImpl principal = (PrincipalDetailsImpl) authentication.getPrincipal();
        User user = principal.getUser(); // 이건 DB에 저장된 User 객체

        TokenResponseDto tokenByOauth = tokenProvider.getTokenByOauth(user);

        String accessToken = tokenByOauth.getAccessToken().replaceAll("\\s+", "");

        //프로젝트 구현 방식에 따라 알맞게 구현
        getRedirectStrategy()
            .sendRedirect(
                request,
                response,
                "http://localhost:8080/complete?"+"token="+accessToken
            );
    }
}
