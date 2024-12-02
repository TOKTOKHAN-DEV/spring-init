package com.spring.spring_init.user.entity;

import com.spring.spring_init.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Getter

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public class BaseUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "pen_name", unique = true, nullable = false)
    private String penName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @CreationTimestamp
    @Column(name = "date_joined", nullable = false, updatable = false, columnDefinition = "timestamptz")
    private LocalDateTime dateJoined;

    public BaseUser(
        final String email,
        final String password,
        final String penName
    ) {
        this.email = email;
        this.password = password;
        this.penName = penName;
    }

    // 비밀번호 변경
    public void changePassword(String changePassword) {
        this.password = changePassword;
    }
}
