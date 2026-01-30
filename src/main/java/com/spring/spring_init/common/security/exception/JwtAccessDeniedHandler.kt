package com.spring.spring_init.common.security.exception

import com.fasterxml.jackson.databind.ObjectMapper
import com.spring.spring_init.common.dto.ErrorResponseDTO
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class JwtAccessDeniedHandler : AccessDeniedHandler {

    companion object {
        private val log = LoggerFactory.getLogger(JwtAccessDeniedHandler::class.java)
    }

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        log.info("durlt")
        val jsonResponse = ObjectMapper().writeValueAsString(
            ErrorResponseDTO(
                errorCode = AuthExceptionCode.ACCESS_DENIED.code,
                message = AuthExceptionCode.ACCESS_DENIED.message
            )
        )
        response.contentType = "application/json; charset=UTF-8"
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.writer.write(jsonResponse)
        response.writer.flush()
    }
}
