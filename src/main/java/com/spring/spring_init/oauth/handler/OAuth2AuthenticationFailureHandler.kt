package com.spring.spring_init.oauth.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class OAuth2AuthenticationFailureHandler : SimpleUrlAuthenticationFailureHandler() {

    companion object {
        private val log = LoggerFactory.getLogger(OAuth2AuthenticationFailureHandler::class.java)
    }

    @Value("\${common.full-domain}")
    private lateinit var domain: String

    @Throws(IOException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
//        response.sendRedirect("/oauth/authorize/kakao")
        //프로젝트 구현 방식에 따라 알맞게 구현
        redirectStrategy.sendRedirect(request, response, "$domain/fail")
    }
}
