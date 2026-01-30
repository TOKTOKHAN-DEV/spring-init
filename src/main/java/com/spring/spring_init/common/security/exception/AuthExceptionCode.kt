package com.spring.spring_init.common.security.exception

import com.spring.spring_init.common.base.BaseErrorCode
import org.springframework.http.HttpStatus

enum class AuthExceptionCode(
    private val httpStatusCode: HttpStatus,
    override val code: String,
    override val message: String
) : BaseErrorCode {
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_ACCESS", "인증이 필요한 접근입니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근이 거부되었습니다");

    override val httpStatus: HttpStatus
        get() = httpStatusCode
}
