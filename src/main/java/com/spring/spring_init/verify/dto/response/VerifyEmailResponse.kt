package com.spring.spring_init.verify.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailResponse {

    @NotNull(message = "email 필수 값 입니다")
    private String email;
}
