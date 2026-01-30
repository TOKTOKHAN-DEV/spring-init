package com.spring.spring_init.common.aws.exception

import com.spring.spring_init.common.base.BaseErrorCode
import org.springframework.http.HttpStatus

enum class FileExceptionCode(
    private val httpStatusCode: HttpStatus,
    override val code: String,
    override val message: String
) : BaseErrorCode {
    FAIL_UPLOAD_FILE(HttpStatus.BAD_REQUEST, "FAIL_UPLOAD_FILE", "파일 업로드에 실패했습니다");

    override val httpStatus: HttpStatus
        get() = httpStatusCode
}
