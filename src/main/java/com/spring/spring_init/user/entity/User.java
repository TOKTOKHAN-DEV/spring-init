package com.spring.spring_init.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
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

    public User(String username, String password) {
        super(username, password);
    }

    public User(
        final String username,
        final String email,
        final OAuthProvider oAuthProvider
    ) {
        super(username, email, oAuthProvider);
    }
}
