package com.spring.spring_init.common.exception;

import com.fasterxml.jackson.annotation.JsonView;
import com.spring.spring_init.common.dto.ErrorResponseDTO;
import com.spring.spring_init.common.dto.FieldErrorResponse;
import com.spring.spring_init.common.exception.CustomJsonView.Common;
import com.spring.spring_init.common.exception.CustomJsonView.Hidden;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionHandler implements CommonExceptionHandlerApi {

    @JsonView(Common.class)
    @ExceptionHandler(value = CommonException.class)
    public ResponseEntity<ErrorResponseDTO> commonExceptionHandler(CommonException e) {
        return ResponseEntity.badRequest()
            .body(
                new ErrorResponseDTO(
                    e.getCode(),
                    e.getMessage())
            );
    }

    @JsonView(Hidden.class)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(
        final MethodArgumentNotValidException e
    ) {
        return ResponseEntity.unprocessableEntity().body(
            new ErrorResponseDTO(
                CommonExceptionCode.FIELD_ERROR.getCode(),
                CommonExceptionCode.FIELD_ERROR.getMessage(),
                e.getBindingResult().getFieldErrors().stream()
                    .map(fieldError ->
                        new FieldErrorResponse(
                            fieldError.getField(), // filedName: 필드 이름
                            fieldError.getDefaultMessage() // reason: 에러 메시지
                        )
                    )
                    .collect(Collectors.toList())
            )
        );
    }
}
