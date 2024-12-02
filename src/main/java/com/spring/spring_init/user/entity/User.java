package com.spring.spring_init.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
public class User extends BaseUser {

    @ManyToMany
    @JoinTable(
        name = "user_authority",
        joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
        inverseJoinColumns = {
            @JoinColumn(name = "authority_id", referencedColumnName = "authority_id")})
    private Set<Authority> authorities;

    @Column(name = "is_staff")
    private Boolean isStaff;

    @Column(name = "is_superuser")
    private Boolean isSuperuser;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    public User(String email, String password, Set<UserRole> userRoles, String penName) {
        super(email, password, penName);
    }

    // 회원 탈퇴
    public void delete() {
        this.isActive = false;
    }

    // 펜슬리 코드 등록
    public void approvalCode() {
        this.paymentDate = LocalDate.now();
    }
}
