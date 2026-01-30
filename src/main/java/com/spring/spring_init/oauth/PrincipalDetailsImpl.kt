package com.spring.spring_init.oauth

import com.spring.spring_init.user.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class PrincipalDetailsImpl : UserDetails, OAuth2User {

    @JvmField
    val user: User
    private val attributes: Map<String, Any>?

    // 일반 로그인
    constructor(user: User) {
        this.user = user
        this.attributes = null
    }

    // oauth 로그인
    constructor(user: User, attributes: Map<String, Any>) {
        this.user = user
        this.attributes = attributes
    }

    fun getUser(): User = user

    override fun getName(): String = user.email

    override fun getAttributes(): Map<String, Any>? = attributes

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(user.userRole.name))
    }

    override fun getPassword(): String = user.password

    override fun getUsername(): String = user.email
}
