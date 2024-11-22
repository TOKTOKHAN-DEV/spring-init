package com.spring.spring_init.user.controller;

import com.spring.spring_init.common.apidocs.ApiExceptionExplanation;
import com.spring.spring_init.common.apidocs.ApiResponseExplanations;
import com.spring.spring_init.common.dto.ResponseDTO;
import com.spring.spring_init.common.security.jwt.TokenResponseDto;
import com.spring.spring_init.user.dto.request.LoginRequestDto;
import com.spring.spring_init.user.dto.request.RegisterUserRequestDto;
import com.spring.spring_init.user.dto.response.LoginResponseDto;
import com.spring.spring_init.user.dto.response.RegisterUserResponseDto;
import com.spring.spring_init.user.exception.UserExceptionCode;
import com.spring.spring_init.user.oauth.kakao.KakaoLoginRequest;
import com.spring.spring_init.user.oauth.naver.NaverLoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[User API]")
public interface UserApi {

    @Operation(summary = "회원가입")
    @ApiResponseExplanations(
        errors = {
            @ApiExceptionExplanation(
                name = "일반 회원가입 계정이 이미 존재함",
                value = UserExceptionCode.class,
                constant = "EXIST_USERNAME")
        }
    )
    ResponseEntity<ResponseDTO<RegisterUserResponseDto>> registerUser(
        @Validated @RequestBody RegisterUserRequestDto requestDto
    );

    @Operation(summary = "시스템 로그인")
    @ApiResponseExplanations(
        errors = {
            @ApiExceptionExplanation(
                name = "아이디나 비밀번호가 틀렸습니다.(회원이 존재하지 않는 경우도 전부)",
                value = UserExceptionCode.class,
                constant = "LOGIN_FAIL")
        }
    )
    ResponseEntity<ResponseDTO<LoginResponseDto>> login(
        @Validated @RequestBody LoginRequestDto requestDto,
        HttpServletResponse response
    );

    @Operation(summary = "카카오 로그인")
    @ApiResponseExplanations(
        errors = {
        }
    )
    ResponseEntity<ResponseDTO<TokenResponseDto>> kakaoLogin(
        @Validated @RequestBody KakaoLoginRequest requestDto,
        HttpServletResponse response
    );

    @Operation(summary = "네이버 로그인")
    @ApiResponseExplanations(
        errors = {
        }
    )
    ResponseEntity<ResponseDTO<TokenResponseDto>> naverLogin(
        @Validated @RequestBody NaverLoginRequest requestDto,
        HttpServletResponse response
    );
}
