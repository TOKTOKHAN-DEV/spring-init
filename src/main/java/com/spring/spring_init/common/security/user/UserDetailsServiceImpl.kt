package com.spring.spring_init.common.security.user;

import com.spring.spring_init.common.exception.CommonException;
import com.spring.spring_init.user.entity.User;
import com.spring.spring_init.user.exception.UserExceptionCode;
import com.spring.spring_init.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
            new CommonException(
                UserExceptionCode.LOGIN_FAIL.getCode(),
                UserExceptionCode.LOGIN_FAIL.getMessage()
            )
        );
        return new UserDetailsImpl(
            user.getUserId(),
            user.getPassword(),
            user.getUserRole()
        );
    }
}
