package com.spring.spring_init.common.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.spring_init.common.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException {

        String jsonResponse = new ObjectMapper().writeValueAsString(
            new ErrorResponseDTO(
                AuthExceptionCode.ACCESS_DENIED.getCode(),
                AuthExceptionCode.ACCESS_DENIED.getMessage()
            )
        );
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
