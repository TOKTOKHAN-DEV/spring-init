package com.spring.spring_init.common.security.user;

import com.spring.spring_init.user.entity.Authority;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
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
    private Set<Authority> authorities;

    public UserDetailsImpl(
        final Long userId,
        final String email,
        final Set<Authority> authorities
    ) {
        this.userId = userId;
        this.email = email;
        this.authorities = authorities;
    }

    public UserDetailsImpl(
        Long userId,
        String email,
        String password,
        Set<Authority> authorities
    ) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities.stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
            .collect(Collectors.toList());
    }

    public Set<Authority> getSetAuthorities() {
        return authorities;
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
