package com.spring.spring_init.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRefreshResponseDto {

    private String accessToken;
    private String refreshToken;
}
