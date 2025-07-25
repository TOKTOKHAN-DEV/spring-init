package com.spring.spring_init.admin.user.dto.response;

import com.spring.spring_init.user.entity.UserRole;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminInfo {

    private Long userId;

    private String email;

    private LocalDateTime joinedAt;

    private UserRole userRole;
}
