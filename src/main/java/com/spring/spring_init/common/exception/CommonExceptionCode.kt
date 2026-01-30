package com.spring.spring_init.common.exception

import com.spring.spring_init.common.base.BaseErrorCode
import org.springframework.http.HttpStatus

enum class CommonExceptionCode(
    private val httpStatusCode: HttpStatus,
    override val code: String,
    override val message: String
) : BaseErrorCode {
    FIELD_ERROR(HttpStatus.BAD_REQUEST, "FIELD_ERROR", "Field error");

    override val httpStatus: HttpStatus
        get() = httpStatusCode
}
