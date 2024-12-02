package com.spring.spring_init.common.security.config;

import com.spring.spring_init.common.security.exception.JwtAccessDeniedHandler;
import com.spring.spring_init.common.security.exception.JwtAuthenticationEntryPoint;
import com.spring.spring_init.common.security.jwt.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        //CSRF(Cross-Site Request Forgery) 보호 비활성화
        // 스프링 부트 3.x.x 버전 부터는 csrf().disable()이 적용 시 경고 문장을 막기위한 setting
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.cors(Customizer.withDefaults());

        //HTTP 기본 인증 비활성화
        httpSecurity.formLogin(AbstractHttpConfigurer::disable);
        httpSecurity.httpBasic(AbstractHttpConfigurer::disable);

        /** X-Frame-Options 비활성화 , iframe은 접근이 가능*/
        httpSecurity.headers(headers -> headers
            .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        );

        //세션 관리 상태 없음으로 구성, Spring Security가 세션 생성 or 사용 X
        httpSecurity.sessionManagement(sessionManagement -> sessionManagement
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        //권한 규칙 구성 시작
        httpSecurity
            .authorizeHttpRequests(
                authorize -> authorize
                    .requestMatchers("/swagger/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/v3/api-docs/**").permitAll()
                    .requestMatchers("/swagger-resources/**").permitAll()
                    .requestMatchers("/v1/user/register").permitAll()
                    .requestMatchers("/v1/user/login").permitAll()
                    .requestMatchers("/v1/user/swagger-login").permitAll()
                    .requestMatchers("/v1/user/password-reset").permitAll()
                    .requestMatchers("/v1/verifier/**").permitAll()
                    .requestMatchers("/v1/admin/**").hasAnyAuthority("ADMIN")
                    .requestMatchers("/test/**").permitAll()
                    .anyRequest().authenticated()
            )
            .exceptionHandling((exceptionHandling) -> exceptionHandling
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)      // 인증오류 (401 오류)
                .accessDeniedHandler(jwtAccessDeniedHandler));

        httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
