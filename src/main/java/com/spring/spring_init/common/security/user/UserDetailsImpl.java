package com.spring.spring_init.common.security.user;

import com.spring.spring_init.user.entity.UserRole;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long userId;
    private String email;
    private String password;
    private UserRole userRole;

    public UserDetailsImpl(
        final Long userId,
        final String email,
        final UserRole userRole
    ) {
        this.userId = userId;
        this.email = email;
        this.userRole = userRole;
    }

    public UserDetailsImpl(
        final Long userId,
        final String email,
        final String password,
        final UserRole userRole
    ) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(userRole.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
