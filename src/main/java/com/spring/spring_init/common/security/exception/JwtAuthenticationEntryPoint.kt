package com.spring.spring_init.common.security.exception

import com.fasterxml.jackson.databind.ObjectMapper
import com.spring.spring_init.common.dto.ErrorResponseDTO
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val jsonResponse = ObjectMapper().writeValueAsString(
            ErrorResponseDTO(
                errorCode = AuthExceptionCode.UNAUTHORIZED_ACCESS.code,
                message = AuthExceptionCode.UNAUTHORIZED_ACCESS.message
            )
        )
        response.contentType = "application/json; charset=UTF-8"
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.writer.write(jsonResponse)
        response.writer.flush()
    }
}
