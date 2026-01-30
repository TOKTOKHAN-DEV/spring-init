package com.spring.spring_init.common.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.spring_init.common.dto.ErrorResponseDTO;
import com.spring.spring_init.common.security.exception.AuthExceptionCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String token = getTokenFromHeader(request);

        try {
            if (token != null && tokenProvider.validateToken(token)) {
                Authentication authentication = tokenProvider.createAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            String jsonResponse = new ObjectMapper().writeValueAsString(
                new ErrorResponseDTO(
                    AuthExceptionCode.TOKEN_EXPIRED.getCode(),
                    AuthExceptionCode.TOKEN_EXPIRED.getMessage()
                )
            );
            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(444);
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        } catch (JwtException e) {
            String jsonResponse = new ObjectMapper().writeValueAsString(
                new ErrorResponseDTO(
                    AuthExceptionCode.UNAUTHORIZED_ACCESS.getCode(),
                    AuthExceptionCode.UNAUTHORIZED_ACCESS.getMessage()
                )
            );
            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        }
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}
