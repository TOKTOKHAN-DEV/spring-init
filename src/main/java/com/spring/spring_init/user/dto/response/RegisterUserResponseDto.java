package com.spring.spring_init.user.dto.response;

import com.spring.spring_init.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserResponseDto {

    private Long userId;

    private String username;

    public RegisterUserResponseDto(User user) {
        this.userId = user.getUserId();
    }
}
