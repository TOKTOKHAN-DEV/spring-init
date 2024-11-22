package com.spring.spring_init.user.controller;

import com.spring.spring_init.common.dto.ResponseDTO;
import com.spring.spring_init.common.security.jwt.TokenResponseDto;
import com.spring.spring_init.user.dto.request.LoginRequestDto;
import com.spring.spring_init.user.dto.request.RegisterUserRequestDto;
import com.spring.spring_init.user.dto.response.LoginResponseDto;
import com.spring.spring_init.user.dto.response.RegisterUserResponseDto;
import com.spring.spring_init.user.oauth.OAuthLoginService;
import com.spring.spring_init.user.oauth.kakao.KakaoLoginRequest;
import com.spring.spring_init.user.oauth.naver.NaverLoginRequest;
import com.spring.spring_init.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/user")

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;
    private final OAuthLoginService oauthLoginService;

    @PostMapping("/na/register")
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

    @PostMapping("/na/login")
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

    @PostMapping("/na/login/kakao")
    public ResponseEntity<ResponseDTO<TokenResponseDto>> kakaoLogin(
        @Validated @RequestBody KakaoLoginRequest requestDto,
        HttpServletResponse response
    ) {
        TokenResponseDto loginResponse = oauthLoginService.login(requestDto);
        response.setHeader("Authorization", loginResponse.getAccessToken());
        return ResponseEntity.ok(
            ResponseDTO.<TokenResponseDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(loginResponse)
                .build()
        );
    }

    @PostMapping("/na/login/naver")
    public ResponseEntity<ResponseDTO<TokenResponseDto>> naverLogin(
        @Validated @RequestBody NaverLoginRequest requestDto,
        HttpServletResponse response
    ) {
        TokenResponseDto loginResponse = oauthLoginService.login(requestDto);
        response.setHeader("Authorization", loginResponse.getAccessToken());
        return ResponseEntity.ok(
            ResponseDTO.<TokenResponseDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(loginResponse)
                .build()
        );
    }
}
