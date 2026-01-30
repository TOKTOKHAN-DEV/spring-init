package com.spring.spring_init.user.dto.response

import com.spring.spring_init.user.entity.User

data class RegisterUserResponseDto(
    val userId: Long?,
    val username: String?
) {
    constructor(user: User) : this(
        userId = user.userId,
        username = null
    )
}
