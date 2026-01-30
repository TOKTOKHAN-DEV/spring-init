package com.spring.spring_init.common.security.config

import com.spring.spring_init.common.security.exception.JwtAccessDeniedHandler
import com.spring.spring_init.common.security.exception.JwtAuthenticationEntryPoint
import com.spring.spring_init.common.security.jwt.JwtTokenFilter
import com.spring.spring_init.oauth.handler.OAuth2AuthenticationFailureHandler
import com.spring.spring_init.oauth.handler.OAuth2AuthenticationSuccessHandler
import com.spring.spring_init.oauth.service.CustomOauth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
class SecurityConfig(
    private val jwtTokenFilter: JwtTokenFilter,
    private val jwtAccessDeniedHandler: JwtAccessDeniedHandler,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val corsProperties: CorsProperties,
    private val customOauth2UserService: CustomOauth2UserService,
    private val successHandler: OAuth2AuthenticationSuccessHandler,
    private val failureHandler: OAuth2AuthenticationFailureHandler
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {

        //CSRF(Cross-Site Request Forgery) 보호 비활성화
        // 스프링 부트 3.x.x 버전 부터는 csrf().disable()이 적용 시 경고 문장을 막기위한 setting
        httpSecurity.csrf { it.disable() }
        httpSecurity.cors(Customizer.withDefaults())

        //HTTP 기본 인증 비활성화
        httpSecurity.formLogin { it.disable() }
        httpSecurity.httpBasic { it.disable() }

        /** X-Frame-Options 비활성화 , iframe은 접근이 가능*/
        httpSecurity.headers { headers ->
            headers.frameOptions { it.sameOrigin() }
        }

        httpSecurity.sessionManagement { sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }

        //todo: social login 사용하지 않는다면 삭제
        httpSecurity.oauth2Login { configure ->
            configure
                .userInfoEndpoint { config -> config.userService(customOauth2UserService) }
                .successHandler(successHandler)
                .failureHandler(failureHandler)
        }

        //권한 규칙 구성 시작
        httpSecurity
            .authorizeHttpRequests { authorize ->
                authorize
                    //
                    .requestMatchers("/**").permitAll()

                    //health check
                    .requestMatchers("/health").permitAll()

                    //swagger
                    .requestMatchers("/swagger/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/v3/api-docs/**").permitAll()
                    .requestMatchers("/swagger-resources/**").permitAll()

                    .requestMatchers("/v1/user/register").permitAll()
                    .requestMatchers("/v1/user/login").permitAll()
                    .requestMatchers("/v1/user/swagger-login").permitAll()
                    .requestMatchers("/v1/user/password-reset").permitAll()
                    .requestMatchers("/v1/verifier/**").permitAll()

                    //Admin 페이지
                    .requestMatchers("/v1/admin/**").hasAnyAuthority("ADMIN")

                    .requestMatchers("/test/**").permitAll()
                    .anyRequest().authenticated()
            }
            .exceptionHandling { exceptionHandling ->
                exceptionHandling
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)      // 인증오류 (401 오류)
                    .accessDeniedHandler(jwtAccessDeniedHandler)
            }

        httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)

        return httpSecurity.build()
    }

    fun corsConfigurationSource() = org.springframework.web.cors.CorsConfigurationSource { request ->
        CorsConfiguration().apply {
            allowedOrigins = corsProperties.allowedOrigins // 허용 도메인 리스트 -> yml 파일에서 관리
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            addAllowedHeader("*")
            allowCredentials = true
            addExposedHeader("Authorization")
        }
    }
}
