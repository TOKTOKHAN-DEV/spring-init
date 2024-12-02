package com.spring.spring_init.verify.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPasswordResetRequest {

    @NotNull(message = "uid 필수 값 입니다.")
    private String uid;

    @NotNull(message = "token 필수 값 입니다.")
    private String token;
}
