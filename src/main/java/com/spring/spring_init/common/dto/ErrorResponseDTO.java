package com.spring.spring_init.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.spring.spring_init.common.exception.CustomJsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(title = "API 응답 - 실패 및 에러")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {

    @JsonView(CustomJsonView.Common.class)
    @Schema(description = "에러 코드", example = "ERROR_CODE")
    private String errorCode;

    @JsonView(CustomJsonView.Common.class)
    @Schema(description = "에러 메시지", example = "에러 이유")
    private String message;

    @JsonView(CustomJsonView.Hidden.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "필드 에러")
    private List<FieldErrorResponse> fieldErrors;

    public ErrorResponseDTO(String code, String message) {
        this.errorCode = code;
        this.message = message;
    }
}
