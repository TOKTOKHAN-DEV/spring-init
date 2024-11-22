package com.spring.spring_init.user.service;

import com.spring.spring_init.common.exception.CommonException;
import com.spring.spring_init.common.security.jwt.TokenProvider;
import com.spring.spring_init.common.security.jwt.TokenResponseDto;
import com.spring.spring_init.common.security.user.UserDetailsImpl;
import com.spring.spring_init.user.dto.request.LoginRequestDto;
import com.spring.spring_init.user.dto.request.RegisterUserRequestDto;
import com.spring.spring_init.user.dto.response.LoginResponseDto;
import com.spring.spring_init.user.dto.response.RegisterUserResponseDto;
import com.spring.spring_init.user.entity.User;
import com.spring.spring_init.user.exception.UserExceptionCode;
import com.spring.spring_init.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;

    @Transactional
    public RegisterUserResponseDto registerUser(
        final RegisterUserRequestDto requestDto
    ) {
        userRepository.findByUsername(requestDto.getUsername())
            .ifPresent(user -> {
                throw new CommonException(
                    UserExceptionCode.EXIST_USERNAME.getCode(),
                    UserExceptionCode.EXIST_USERNAME.getMessage()
                );
            });

        User user = new User(
            requestDto.getUsername(),
            passwordEncoder.encode(requestDto.getPassword())
        );

        return new RegisterUserResponseDto(userRepository.save(user));
    }

    public LoginResponseDto login(final LoginRequestDto requestDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(
                requestDto.getUsername(),
                requestDto.getPassword()
            );
        Authentication authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        TokenResponseDto tokenByUser = tokenProvider.getTokenByUser(userDetails);

        return new LoginResponseDto(tokenByUser);
    }
}
