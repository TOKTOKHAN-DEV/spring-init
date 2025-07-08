package com.spring.spring_init.user.entity;

import com.spring.spring_init.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor
@AllArgsConstructor

@Entity
//@Table(name = "users")
@Table(name = "\"user\"") //user는 DB 예약어이기 때문에 "를 명시해줘야 테이블 생성 가능
@SQLRestriction("deleted_at is NULL")
@SQLDelete(sql = "UPDATE user SET deleted_at = NOW() WHERE user_id = ?")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "user_role", nullable = false)
    private UserRole userRole;

    @CreationTimestamp
    @Column(name = "date_joined", nullable = false, updatable = false, columnDefinition = "timestamptz")
    private LocalDateTime dateJoined;

    public User(
        String email,
        String password,
        UserRole userRole
    ) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    // 비밀번호 변경
    public void changePassword(String changePassword) {
        this.password = changePassword;
    }

    // 회원 탈퇴
    public void delete() {
        super.setDeletedAt();
    }
}
