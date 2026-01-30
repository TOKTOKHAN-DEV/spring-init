package com.spring.spring_init.verify.service;

import com.spring.spring_init.common.exception.CommonException;
import com.spring.spring_init.common.security.user.UserDetailsImpl;
import com.spring.spring_init.user.entity.User;
import com.spring.spring_init.user.exception.UserExceptionCode;
import com.spring.spring_init.user.repository.UserRepository;
import com.spring.spring_init.verify.dto.request.VerifyPasswordRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerifyService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void verifyPassword(VerifyPasswordRequestDto requestDto, UserDetailsImpl userDetails) {
        // 사용자 정보 조회
        User user = userRepository.findById(userDetails.getUserId())
            .orElseThrow(() -> new CommonException(
                UserExceptionCode.NOT_FOUND_USER.getCode(),
                UserExceptionCode.NOT_FOUND_USER.getMessage()
            ));

        // 현재 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CommonException(
                UserExceptionCode.NOT_MATCH_CURRENT_PASSWORD.getCode(),
                UserExceptionCode.NOT_MATCH_CURRENT_PASSWORD.getMessage()
            );
        }
    }
}
