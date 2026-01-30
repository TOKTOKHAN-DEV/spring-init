package com.spring.spring_init.user.dto.response

import com.spring.spring_init.common.security.jwt.TokenResponseDto

data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String
) {
    constructor(tokenResponseDto: TokenResponseDto) : this(
        accessToken = tokenResponseDto.accessToken,
        refreshToken = tokenResponseDto.refreshToken
    )
}
