package com.spring.spring_init.common.exception

import com.fasterxml.jackson.annotation.JsonView
import com.spring.spring_init.common.dto.ErrorResponseDTO
import com.spring.spring_init.common.dto.FieldErrorResponse
import com.spring.spring_init.common.exception.CustomJsonView.Common
import com.spring.spring_init.common.exception.CustomJsonView.Hidden
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CommonExceptionHandler : CommonExceptionHandlerApi {

    @JsonView(Common::class)
    @ExceptionHandler(value = [CommonException::class])
    override fun commonExceptionHandler(e: CommonException): ResponseEntity<ErrorResponseDTO> {
        return ResponseEntity.badRequest()
            .body(
                ErrorResponseDTO(
                    errorCode = e.code,
                    message = e.message ?: ""
                )
            )
    }

    @JsonView(Hidden::class)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    override fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException
    ): ResponseEntity<ErrorResponseDTO> {
        return ResponseEntity.unprocessableEntity().body(
            ErrorResponseDTO(
                errorCode = CommonExceptionCode.FIELD_ERROR.code,
                message = CommonExceptionCode.FIELD_ERROR.message,
                fieldErrors = e.bindingResult.fieldErrors.map { fieldError ->
                    FieldErrorResponse(
                        filedName = fieldError.field,
                        reason = fieldError.defaultMessage ?: ""
                    )
                }
            )
        )
    }
}
