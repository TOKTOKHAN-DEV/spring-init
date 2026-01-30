package com.spring.spring_init.user.entity

import com.spring.spring_init.common.base.BaseEntity
import com.spring.spring_init.oauth.OAuthProvider
import com.spring.spring_init.oauth.userInfo.OAuth2UserInfo
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "\"user\"") // user는 DB 예약어이기 때문에 "를 명시해줘야 테이블 생성 가능
@SQLRestriction("deleted_at is NULL")
@SQLDelete(sql = "UPDATE user SET deleted_at = NOW() WHERE user_id = ?")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    var userId: Long? = null,

    @Column(name = "email", nullable = false)
    var email: String,

    @Column(name = "password", nullable = false)
    var password: String,

    @Column(name = "user_role", nullable = false)
    var userRole: UserRole,

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    var provider: OAuthProvider? = null,

    @Column(name = "provider_id")
    var providerId: String? = null,

    @CreationTimestamp
    @Column(name = "date_joined", nullable = false, updatable = false, columnDefinition = "timestamptz")
    var dateJoined: LocalDateTime? = null
) : BaseEntity() {

    constructor(
        email: String,
        password: String,
        userRole: UserRole
    ) : this(
        userId = null,
        email = email,
        password = password,
        userRole = userRole,
        provider = null,
        providerId = null,
        dateJoined = null
    )

    constructor(
        userInfo: OAuth2UserInfo,
        authProvider: OAuthProvider
    ) : this(
        userId = null,
        email = userInfo.getEmail(),
        password = "password",
        userRole = UserRole.ROLE_USER,
        provider = authProvider,
        providerId = userInfo.getId(),
        dateJoined = null
    )

    // 비밀번호 변경
    fun changePassword(changePassword: String) {
        this.password = changePassword
    }

    // 회원 탈퇴
    fun delete() {
        super.setDeletedAt()
    }
}
