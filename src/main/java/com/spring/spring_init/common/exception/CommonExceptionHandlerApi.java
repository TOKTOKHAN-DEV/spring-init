package com.spring.spring_init.common.exception;

import com.spring.spring_init.common.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

public interface CommonExceptionHandlerApi {

    @ApiResponse(
        responseCode = "400",
        description = "Bad Request",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    public ResponseEntity<ErrorResponseDTO> commonExceptionHandler(
        final CommonException e
    );

    @ApiResponse(
        responseCode = "422",
        description = "Field error",
        content = @Content(
            schema = @Schema(
                implementation = ErrorResponseDTO.class),
            examples = {
                @ExampleObject(
                    name = "Field",
                    value = """
                            {
                              "errorCode": "FIELD_ERROR",
                              "message": "에러 이유",
                              "fieldErrors": {
                                "fieldName": "reason"
                              }
                            }
                        """)
            })
    )
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(
        final MethodArgumentNotValidException e
    );

}