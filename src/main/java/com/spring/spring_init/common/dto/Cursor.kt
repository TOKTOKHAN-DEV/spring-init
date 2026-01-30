package com.spring.spring_init.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cursor<T> {

    @Schema(
        requiredMode = RequiredMode.REQUIRED,
        description = "cursor / 다음 요청의 cursor",
        nullable = true
    )
    private String cursor;

    @Schema(
        requiredMode = RequiredMode.REQUIRED,
        description = "다음 데이터 존재 여부"
    )
    private Boolean hasNext;

    private List<T> data;
}
