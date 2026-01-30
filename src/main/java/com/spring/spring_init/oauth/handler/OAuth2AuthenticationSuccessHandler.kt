package com.spring.spring_init.oauth.handler

import com.spring.spring_init.common.security.jwt.TokenProvider
import com.spring.spring_init.oauth.PrincipalDetailsImpl
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler(
    private val tokenProvider: TokenProvider
) : SimpleUrlAuthenticationSuccessHandler() {

    @Value("\${common.full-domain}")
    private lateinit var domain: String

    companion object {
        private val log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler::class.java)
    }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        // 1. PrincipalDetailsImpl 꺼내기
        val principal = authentication.principal as PrincipalDetailsImpl
        val user = principal.user // 이건 DB에 저장된 User 객체

        val tokenByOauth = tokenProvider.getTokenByOauth(user)
        val accessToken = tokenByOauth.accessToken.replace("\\s+".toRegex(), "")

        // 프로젝트 구현 방식에 따라 알맞게 구현
        redirectStrategy.sendRedirect(
            request,
            response,
            "http://localhost:8080/complete?token=$accessToken"
        )
    }
}
