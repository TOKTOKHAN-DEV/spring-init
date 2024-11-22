package com.spring.spring_init.common.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.spring.spring_init.common.exception.CustomJsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorResponse {

    @Schema(name = "필드 이름")
    @JsonView(CustomJsonView.Hidden.class)
    private String filedName;

    @Schema(name = "필드 에러 이유")
    @JsonView(CustomJsonView.Hidden.class)
    private String reason;
}
