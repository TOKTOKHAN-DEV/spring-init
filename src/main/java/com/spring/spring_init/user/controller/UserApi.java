package com.spring.spring_init.user.controller;

import com.spring.spring_init.common.apidocs.ApiExceptionExplanation;
import com.spring.spring_init.common.apidocs.ApiResponseExplanations;
import com.spring.spring_init.common.dto.ResponseDTO;
import com.spring.spring_init.common.security.user.UserDetailsImpl;
import com.spring.spring_init.user.dto.request.LoginRequestDto;
import com.spring.spring_init.user.dto.request.PasswordChangeRequestDto;
import com.spring.spring_init.user.dto.request.PasswordResetRequest;
import com.spring.spring_init.user.dto.request.RegisterUserRequestDto;
import com.spring.spring_init.user.dto.request.UserRefreshRequestDto;
import com.spring.spring_init.user.dto.response.LoginResponseDto;
import com.spring.spring_init.user.dto.response.PasswordResetResponse;
import com.spring.spring_init.user.dto.response.RegisterUserResponseDto;
import com.spring.spring_init.user.dto.response.UserInfoResponseDto;
import com.spring.spring_init.user.dto.response.UserRefreshResponseDto;
import com.spring.spring_init.user.exception.UserExceptionCode;
import com.spring.spring_init.verify.exception.EmailVerifyExceptionCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[User API]")
public interface UserApi {

    @Operation(summary = "회원가입")
    @ApiResponseExplanations(
        errors = {
            @ApiExceptionExplanation(
                name = "이메일 계정이 이미 존재함",
                value = UserExceptionCode.class,
                constant = "EXIST_EMAIL"),
            @ApiExceptionExplanation(
                name = "인증받지 않은 이메일",
                value = UserExceptionCode.class,
                constant = "UNVERIFIED_EMAIL"),
            @ApiExceptionExplanation(
                name = "토큰이 유효하지 않음",
                value = EmailVerifyExceptionCode.class,
                constant = "INVALID_TOKEN"),
            @ApiExceptionExplanation(
                name = "비밀번호 확인란이 일치하지 않는 경우",
                value = UserExceptionCode.class,
                constant = "PASSWORD_MISMATCH")
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

    @Operation(summary = "유저 리프레시")
    @ApiResponseExplanations(
        errors = {

        }
    )
    ResponseEntity<ResponseDTO<UserRefreshResponseDto>> userRefresh(
        @RequestBody UserRefreshRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    );


    @Operation(summary = "유저 삭제(탈퇴)")
    @ApiResponseExplanations(
        errors = {
            @ApiExceptionExplanation(
                name = "회원 정보가 일치하지 않음",
                value = UserExceptionCode.class,
                constant = "NOT_MATCH_USER"
            )
        }
    )
    ResponseEntity<ResponseDTO<String>> deleteUser(
        @PathVariable Long userId,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "유저 정보 조회")
    @ApiResponseExplanations(
        errors = {
            @ApiExceptionExplanation(
                name = "유저 정보 조회 실패",
                value = UserExceptionCode.class,
                constant = "NOT_FOUND_USER"
            )
        }
    )
    ResponseEntity<ResponseDTO<UserInfoResponseDto>> getUserInfo(
        @PathVariable Long userId,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "유저 비밀번호 변경")
    @ApiResponseExplanations(
        errors = {
            @ApiExceptionExplanation(
                name = "기존 비밀번호와 불일치",
                value = UserExceptionCode.class,
                constant = "NOT_MATCH_CURRENT_PASSWORD"
            ),
            @ApiExceptionExplanation(
                name = "기존 비밀번호와 새로운 비밀번호가 일치",
                value = UserExceptionCode.class,
                constant = "SAME_PASSWORD"
            ),
            @ApiExceptionExplanation(
                name = "새로운 비밀번호와 비밀번호 확인이 일치하지 않음",
                value = UserExceptionCode.class,
                constant = "NOT_MATCH_CHANGE_PASSWORD"
            )
        }
    )
    ResponseEntity<ResponseDTO<String>> changePassword(
        @Validated @RequestBody PasswordChangeRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "유저 비밀번호 초기화 메일 발송", description = "이메일을 통해 비밀번호 재설정 가능한 link를 발급받습니다.")
    @ApiResponseExplanations(
        errors = {
            @ApiExceptionExplanation(
                name = "존재하지 않는 회원",
                value = UserExceptionCode.class,
                constant = "NOT_FOUND_USER"
            )
        }
    )
    ResponseEntity<ResponseDTO<PasswordResetResponse>> passwordReset(
        @Validated @RequestBody PasswordResetRequest request
    );
//    @Operation(summary = "카카오 로그인")
//    @ApiResponseExplanations(
//        errors = {
//        }
//    )
//    ResponseEntity<ResponseDTO<TokenResponseDto>> kakaoLogin(
//        @Validated @RequestBody KakaoLoginRequest requestDto,
//        HttpServletResponse response
//    );
//
//    @Operation(summary = "네이버 로그인")
//    @ApiResponseExplanations(
//        errors = {
//        }
//    )
//    ResponseEntity<ResponseDTO<TokenResponseDto>> naverLogin(
//        @Validated @RequestBody NaverLoginRequest requestDto,
//        HttpServletResponse response
//    );
}
