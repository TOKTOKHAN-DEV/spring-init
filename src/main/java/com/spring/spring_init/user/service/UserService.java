package com.spring.spring_init.user.service;

import com.spring.spring_init.common.exception.CommonException;
import com.spring.spring_init.common.security.jwt.TokenProvider;
import com.spring.spring_init.common.security.jwt.TokenResponseDto;
import com.spring.spring_init.common.security.user.UserDetailsImpl;
import com.spring.spring_init.user.dto.request.LoginRequestDto;
import com.spring.spring_init.user.dto.request.PasswordChangeRequestDto;
import com.spring.spring_init.user.dto.request.PasswordResetConfirmRequest;
import com.spring.spring_init.user.dto.request.PasswordResetRequest;
import com.spring.spring_init.user.dto.request.RegisterUserRequestDto;
import com.spring.spring_init.user.dto.request.UserRefreshRequestDto;
import com.spring.spring_init.user.dto.response.LoginResponseDto;
import com.spring.spring_init.user.dto.response.PasswordResetResponse;
import com.spring.spring_init.user.dto.response.RegisterUserResponseDto;
import com.spring.spring_init.user.dto.response.UserInfoResponseDto;
import com.spring.spring_init.user.dto.response.UserRefreshResponseDto;
import com.spring.spring_init.user.entity.User;
import com.spring.spring_init.user.entity.UserRole;
import com.spring.spring_init.user.exception.UserExceptionCode;
import com.spring.spring_init.user.repository.UserRepository;
import com.spring.spring_init.verify.entity.EmailVerifier;
import com.spring.spring_init.verify.entity.EmailVerifyPurpose;
import com.spring.spring_init.verify.exception.EmailVerifyExceptionCode;
import com.spring.spring_init.verify.repository.EmailVerifyRepository;
import com.spring.spring_init.verify.service.EmailTokenGenerator;
import com.spring.spring_init.verify.service.MailSender;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final EmailVerifyRepository emailVerifyRepository;
    private final MailSender mailSender;
    private final EmailTokenGenerator emailTokenGenerator;

    // 회원가입
    @Transactional
    public RegisterUserResponseDto registerUser(
        final RegisterUserRequestDto requestDto
    ) {
        //이메일 토큰 검증
        validateEmailToken(
            requestDto.getEmail(),
            requestDto.getEmailToken(),
            EmailVerifyPurpose.EMAIL_VALIDATION
        );

        //이메일 중복 여부 검증
        checkEmailExists(requestDto);

        //비밀번호 확인란 일치 여부 검증
        checkPasswordConfirm(requestDto);

        //유저 권한
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(UserRole.ROLE_USER);

        User savedUser = userRepository.save(
            new User(
                requestDto.getEmail(),
                passwordEncoder.encode(requestDto.getPassword()),
                userRoles,
                requestDto.getPenName()
            ));

        //인증 관련 이메일 기록 전부 삭제 처리
        emailVerifyRepository.deleteByEmailAndPurpose(
            savedUser.getEmail(),
            EmailVerifyPurpose.EMAIL_VALIDATION
        );

        return new RegisterUserResponseDto(savedUser);
    }

    // 로그인
    public LoginResponseDto login(final LoginRequestDto requestDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(
                requestDto.getEmail(),
                requestDto.getPassword()
            );
        Authentication authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        TokenResponseDto tokenByUser = tokenProvider.getTokenByUser(userDetails);

        return new LoginResponseDto(tokenByUser);
    }

    // 유저 정보 조회
    public UserInfoResponseDto getUserInfo(Long id) {
        User user = getUser(id);
        return new UserInfoResponseDto(user);
    }

    // 유저 탈퇴
    @Transactional
    public String deleteUser(Long id, UserDetailsImpl userDetails) {
        validateUserId(id, userDetails);
        User user = getUser(id);

        user.delete();
        userRepository.save(user);

        return user.getEmail();
    }

    // 유저 리프레시
    public UserRefreshResponseDto userRefresh(UserRefreshRequestDto requestDto,
        UserDetailsImpl userDetails) {
        tokenProvider.validateToken(requestDto.getRefreshToken());
        String accessToken = tokenProvider.getAccessTokenByUser(userDetails);

        return new UserRefreshResponseDto(accessToken, requestDto.getRefreshToken());
    }

    // 비밀번호 변경
    @Transactional
    public String changePassword(PasswordChangeRequestDto requestDto, UserDetailsImpl userDetails) {
        User user = getUser(userDetails.getUserId());

        validatePassword(
            requestDto.getCurrentPassword(),
            requestDto.getPassword(),
            requestDto.getPasswordConfirm(),
            user,
            false
        );

        user.changePassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(user);

        return user.getEmail();
    }

    @Transactional
    //비밀번호 초기화 이메일 발송
    public PasswordResetResponse passwordReset(final PasswordResetRequest request) {

        //이메일로 회원 존재 여부 검증
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
            () -> new CommonException(
                UserExceptionCode.NOT_FOUND_USER.getCode(),
                UserExceptionCode.NOT_FOUND_USER.getMessage()
            ));

        String token = emailTokenGenerator.generateVerificationToken(request.getEmail(), "");

        //비밀번호 초기화 메일 발송
        mailSender.sendEmail(
            request.getEmail(),
            null,
            token,
            user,
            EmailVerifyPurpose.RESET_PASSWORD
        );

        //발송한 이메일 정보 저장
        emailVerifyRepository.save(
            new EmailVerifier(
                request.getEmail(),
                null,
                token,
                EmailVerifyPurpose.RESET_PASSWORD
            )
        );
        return new PasswordResetResponse(request.getEmail());
    }

    /**
     * 이메일을 통한 비밀번호 초기화
     */
    @Transactional
    public void passwordResetConfirm(final PasswordResetConfirmRequest request) {
        Long userId = emailTokenGenerator.decodeUidByUserId(request.getUid());
        User user = getUser(userId);

        validateEmailToken(
            user.getEmail(),
            request.getToken(),
            EmailVerifyPurpose.RESET_PASSWORD
        );

        validatePassword(
            null,
            request.getPassword(),
            request.getPasswordConfirm(),
            user,
            true
        );

        user.changePassword(request.getPassword());
    }

    // -------------------------------- //

    // 유저 정보 조회
    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CommonException(
                UserExceptionCode.NOT_FOUND_USER.getCode(),
                UserExceptionCode.NOT_FOUND_USER.getMessage()
            ));
    }

    // 로그인한 유저 Id와 요청한 유저 Id가 일치하는지 확인
    private void validateUserId(Long id, UserDetailsImpl userDetails) {
        if (!userDetails.getUserId().equals(id)) {
            throw new CommonException(
                UserExceptionCode.NOT_MATCH_USER.getCode(),
                UserExceptionCode.NOT_MATCH_USER.getMessage()
            );
        }
    }

    // 비밀번호 확인란 일치 여부 검증
    private static void checkPasswordConfirm(RegisterUserRequestDto requestDto) {
        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            throw new CommonException(
                UserExceptionCode.PASSWORD_MISMATCH.getCode(),
                UserExceptionCode.PASSWORD_MISMATCH.getMessage()
            );
        }
    }

    // 이메일 중복 여부 검증
    private void checkEmailExists(RegisterUserRequestDto requestDto) {
        userRepository.findByEmail(requestDto.getEmail())
            .ifPresent(user -> {
                throw new CommonException(
                    UserExceptionCode.EXIST_EMAIL.getCode(),
                    UserExceptionCode.EXIST_EMAIL.getMessage()
                );
            });
    }

    // 이메일 토큰 검증
    private void validateEmailToken(
        final String email,
        final String token,
        final EmailVerifyPurpose purpose
    ) {
        EmailVerifier emailVerifier = emailVerifyRepository.findByEmailAndToken(
            email,
            token
        ).orElseThrow(() -> new CommonException(
            UserExceptionCode.UNVERIFIED_EMAIL.getCode(),
            UserExceptionCode.UNVERIFIED_EMAIL.getMessage()
        ));

        EmailVerifier lastEmailVerifier =
            emailVerifyRepository.findFirstByEmailAndPurposeOrderByCreatedAtDesc(
                email,
                purpose
            ).orElseThrow(() -> new CommonException(
                UserExceptionCode.UNVERIFIED_EMAIL.getCode(),
                UserExceptionCode.UNVERIFIED_EMAIL.getMessage()
            ));

        if (!emailVerifier.equals(lastEmailVerifier)) {
            throw new CommonException(
                EmailVerifyExceptionCode.INVALID_TOKEN.getCode(),
                EmailVerifyExceptionCode.INVALID_TOKEN.getMessage()
            );
        }
    }

    //비밀번호 변경에서 비밀번호 검증 절차
    private void validatePassword(
        final String currentPassword,
        final String newPassword,
        final String newPasswordConfirm,
        final User user,
        final boolean isResetPassword
    ) {
        // 현재 비밀번호 확인
        if (!isResetPassword && !passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CommonException(
                UserExceptionCode.NOT_MATCH_CURRENT_PASSWORD.getCode(),
                UserExceptionCode.NOT_MATCH_CURRENT_PASSWORD.getMessage()
            );
        }

        // 새로운 비밀번호와 기존 비밀번호가 같은지 확인
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new CommonException(
                UserExceptionCode.SAME_PASSWORD.getCode(),
                UserExceptionCode.SAME_PASSWORD.getMessage()
            );
        }

        // 새로운 비밀번호 확인
        if (!newPassword.equals(newPasswordConfirm)) {
            throw new CommonException(
                UserExceptionCode.NOT_MATCH_CHANGE_PASSWORD.getCode(),
                UserExceptionCode.NOT_MATCH_CHANGE_PASSWORD.getMessage()
            );
        }
    }

//    public SwaggerLoginResponseDto swaggerLogin(
//        final LoginRequestDto convertRequestDto,
//        final HttpServletRequest request,
//        final HttpServletResponse response
//    ) {
//    }
}
