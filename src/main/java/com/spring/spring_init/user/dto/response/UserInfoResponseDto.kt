package com.spring.spring_init.user.dto.response

import com.spring.spring_init.user.entity.User

data class UserInfoResponseDto(
    val id: Long?,
    val penName: String?,
    val email: String?,
    val paymentDate: String?
) {
    constructor(user: User) : this(
        id = user.userId,
        penName = null,
        email = user.email,
        paymentDate = null
    )
}
