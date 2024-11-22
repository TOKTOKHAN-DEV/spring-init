package com.spring.spring_init.user.dto.response;

import com.spring.spring_init.common.security.jwt.TokenResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;

    public LoginResponseDto(TokenResponseDto tokenResponseDto) {
        this.accessToken = tokenResponseDto.getAccessToken();
        this.refreshToken = tokenResponseDto.getRefreshToken();
    }
}
