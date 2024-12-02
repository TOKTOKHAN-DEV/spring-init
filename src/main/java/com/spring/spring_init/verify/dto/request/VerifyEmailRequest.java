package com.spring.spring_init.verify.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyEmailRequest {

    @Email
    @NotNull(message = "email은 필수 값 입니다.")
    private String email;
}
