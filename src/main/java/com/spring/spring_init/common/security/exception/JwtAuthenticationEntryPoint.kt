package com.spring.spring_init.common.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.spring_init.common.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {

        String jsonResponse = new ObjectMapper().writeValueAsString(
            new ErrorResponseDTO(
                AuthExceptionCode.UNAUTHORIZED_ACCESS.getCode(),
                AuthExceptionCode.UNAUTHORIZED_ACCESS.getMessage()
            )
        );
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(jsonResponse);
        response.getWriter();
    }
}
