package com.spring.spring_init.user.controller;

import com.spring.spring_init.common.dto.ResponseDTO;
import com.spring.spring_init.common.security.user.UserDetailsImpl;
import com.spring.spring_init.user.dto.request.LoginRequestDto;
import com.spring.spring_init.user.dto.request.PasswordChangeRequestDto;
import com.spring.spring_init.user.dto.request.PasswordResetConfirmRequest;
import com.spring.spring_init.user.dto.request.PasswordResetRequest;
import com.spring.spring_init.user.dto.request.RegisterUserRequestDto;
import com.spring.spring_init.user.dto.request.UserRefreshRequestDto;
import com.spring.spring_init.user.dto.response.LoginResponseDto;
import com.spring.spring_init.user.dto.response.PasswordResetResponse;
import com.spring.spring_init.user.dto.response.RegisterUserResponseDto;
import com.spring.spring_init.user.dto.response.SwaggerLoginResponseDto;
import com.spring.spring_init.user.dto.response.UserInfoResponseDto;
import com.spring.spring_init.user.dto.response.UserRefreshResponseDto;
import com.spring.spring_init.user.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/user")

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<RegisterUserResponseDto>> registerUser(
        @Validated @RequestBody RegisterUserRequestDto requestDto
    ) {
        return ResponseEntity.ok(
            ResponseDTO.<RegisterUserResponseDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(userService.registerUser(requestDto))
                .build()
        );
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<LoginResponseDto>> login(
        @Validated @RequestBody LoginRequestDto requestDto,
        HttpServletResponse response
    ) {
        LoginResponseDto loginResponse = userService.login(requestDto);
        response.setHeader("Authorization", loginResponse.getAccessToken());

        return ResponseEntity.ok(
            ResponseDTO.<LoginResponseDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(loginResponse)
                .build()
        );
    }

    // 유저 리프레시
    @PostMapping("/refresh")
    public ResponseEntity<ResponseDTO<UserRefreshResponseDto>> userRefresh(
        @Validated @RequestBody UserRefreshRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(
            ResponseDTO.<UserRefreshResponseDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(userService.userRefresh(requestDto, userDetails))
                .build()
        );
    }

    // 유저 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserInfoResponseDto>> getUserInfo(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(
            ResponseDTO.<UserInfoResponseDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(userService.getUserInfo(id))
                .build()
        );
    }

    // 유저 삭제(탈퇴)
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> deleteUser(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(
            ResponseDTO.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(userService.deleteUser(id, userDetails))
                .build()
        );
    }

    // 비밀번호 변경
    @PostMapping("/password-change")
    public ResponseEntity<ResponseDTO<String>> changePassword(
        @Validated @RequestBody PasswordChangeRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetail
    ) {
        return ResponseEntity.ok(
            ResponseDTO.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(userService.changePassword(requestDto, userDetail))
                .build()
        );
    }


    //비밀번호 초기화 메일 발송
    @PostMapping("/password-reset")
    public ResponseEntity<ResponseDTO<PasswordResetResponse>> passwordReset(
        @Validated @RequestBody PasswordResetRequest request
    ) {
        return ResponseEntity.ok(
            ResponseDTO.<PasswordResetResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(userService.passwordReset(request))
                .build()
        );
    }

    /**
     * 이메일을 통한 비밀번호 초기화
     */
    @PostMapping("/password-reset-confirm")
    public ResponseEntity<ResponseDTO<Void>> passwordResetConfirm(
        @Validated @RequestBody PasswordResetConfirmRequest request
    ) {
        userService.passwordResetConfirm(request);

        return ResponseEntity.ok(
            ResponseDTO.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("SUCCESS")
                .build()
        );
    }

    @Hidden
    @PostMapping("/swagger-login")
    public SwaggerLoginResponseDto swaggerLogin(
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String password,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        LoginRequestDto convertRequestDto = new LoginRequestDto(username, password);
        LoginResponseDto responseDto = userService.login(convertRequestDto);

        response.setHeader("Authorization", responseDto.getAccessToken());
        return new SwaggerLoginResponseDto(responseDto.getAccessToken());
    }

//    @PostMapping("/na/login/kakao")
//    public ResponseEntity<ResponseDTO<TokenResponseDto>> kakaoLogin(
//        @Validated @RequestBody KakaoLoginRequest requestDto,
//        HttpServletResponse response
//    ) {
//        TokenResponseDto loginResponse = oauthLoginService.login(requestDto);
//        response.setHeader("Authorization", loginResponse.getAccessToken());
//        return ResponseEntity.ok(
//            ResponseDTO.<TokenResponseDto>builder()
//                .statusCode(HttpStatus.OK.value())
//                .message("SUCCESS")
//                .data(loginResponse)
//                .build()
//        );
//    }
//
//    @PostMapping("/na/login/naver")
//    public ResponseEntity<ResponseDTO<TokenResponseDto>> naverLogin(
//        @Validated @RequestBody NaverLoginRequest requestDto,
//        HttpServletResponse response
//    ) {
//        TokenResponseDto loginResponse = oauthLoginService.login(requestDto);
//        response.setHeader("Authorization", loginResponse.getAccessToken());
//        return ResponseEntity.ok(
//            ResponseDTO.<TokenResponseDto>builder()
//                .statusCode(HttpStatus.OK.value())
//                .message("SUCCESS")
//                .data(loginResponse)
//                .build()
//        );
//    }
}
