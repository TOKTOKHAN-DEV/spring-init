package com.spring.spring_init.user.dto.response;

import com.spring.spring_init.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDto {

    private Long id;
    private String penName;
    private String email;
    private String paymentDate;

    public UserInfoResponseDto(User user) {
        this.id = user.getUserId();
        this.penName = user.getPenName();
        this.email = user.getEmail();
        if (user.getPaymentDate() != null) {
            this.paymentDate = user.getPaymentDate().toString();
        }
    }
}
