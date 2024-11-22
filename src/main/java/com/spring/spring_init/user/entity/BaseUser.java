package com.spring.spring_init.user.entity;

import com.spring.spring_init.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Column(unique = true, nullable = false)
    private String username;

    @Column
    private String password;

    @Column
    private String email;

    @Column(length = 11)
    private String phone;

    @CreationTimestamp
    @Column(name = "date_joined", nullable = false, updatable = false, columnDefinition = "timestamptz")
    private LocalDateTime dateJoined;

    @Column(name = "oauth_provider")
    @Enumerated(EnumType.STRING)
    private OAuthProvider oAuthProvider;

    public BaseUser(
        final String username,
        final String password
    ) {
        this.username = username;
        this.password = password;
    }

    public BaseUser(String username, String email, OAuthProvider oAuthProvider) {
        this.username = username;
        this.email = email;
        this.oAuthProvider = oAuthProvider;
    }
}
