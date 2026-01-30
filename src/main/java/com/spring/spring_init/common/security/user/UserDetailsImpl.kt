package com.spring.spring_init.common.security.user

import com.spring.spring_init.user.entity.UserRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(
    @JvmField val userId: Long,
    @JvmField val email: String,
    private var password: String? = null,
    @JvmField val userRole: UserRole
) : UserDetails {

    constructor(
        userId: Long,
        email: String,
        userRole: UserRole
    ) : this(userId, email, null, userRole)

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(userRole.name))
    }

    override fun getPassword(): String? {
        return password
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}
