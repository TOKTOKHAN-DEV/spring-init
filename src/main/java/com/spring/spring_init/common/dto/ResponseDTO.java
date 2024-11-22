package com.spring.spring_init.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {

    @Schema(description = "HttpStatusCode", example = "200")
    private int statusCode;

    @Schema(description = "응답 메시지", example = "성공")
    private String message;

    @Schema(description = "데이터")
    private T data;
}
