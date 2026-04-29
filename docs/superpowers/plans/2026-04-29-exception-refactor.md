# Exception Refactor Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** `CommonException` 생성자를 `BaseErrorCode` 단일 인자로 단순화하고, 핸들러의 httpStatus 누락 버그를 고치며, `AuthExceptionCode`를 `BaseErrorCode`와 일관화한다.

**Architecture:** 3단계로 분리. Task 1은 `AuthExceptionCode` 일관화(무중단). Task 2는 `CommonException` 시그니처 교체와 5개 파일 23개 콜사이트 일괄 치환을 한 커밋으로(빌드 깨짐 방지). Task 3은 핸들러 응답 코드 버그 수정. 단위 테스트 추가는 본 PR 범위 외 — `./gradlew build`로 컴파일 검증.

**Tech Stack:** Java 17, Spring Boot 3.3.5, Gradle 8.10, Lombok.

**참고 스펙:** `docs/superpowers/specs/2026-04-29-exception-refactor-design.md`

**작업 브랜치:** `feat/exception-refactor` (main 기준)

---

## File Structure

| 파일 | 변경 종류 | 책임 |
|---|---|---|
| `src/main/java/com/spring/spring_init/common/security/exception/AuthExceptionCode.java` | Modify | `BaseErrorCode` 구현 추가 |
| `src/main/java/com/spring/spring_init/common/exception/CommonException.java` | Modify | 생성자 단순화, `errorCode` 필드 보유 |
| `src/main/java/com/spring/spring_init/user/service/UserService.java` | Modify | 11개 콜사이트 치환 |
| `src/main/java/com/spring/spring_init/verify/service/EmailVerifyService.java` | Modify | 8개 콜사이트 치환 |
| `src/main/java/com/spring/spring_init/verify/service/VerifyService.java` | Modify | 2개 콜사이트 치환 |
| `src/main/java/com/spring/spring_init/common/security/user/UserDetailsServiceImpl.java` | Modify | 1개 콜사이트 치환 |
| `src/main/java/com/spring/spring_init/common/aws/service/FileService.java` | Modify | 1개 콜사이트 치환 |
| `src/main/java/com/spring/spring_init/common/exception/CommonExceptionHandler.java` | Modify | httpStatus 버그 수정 |

---

## Task 1: `AuthExceptionCode`에 `BaseErrorCode` 구현 추가

**Files:**
- Modify: `src/main/java/com/spring/spring_init/common/security/exception/AuthExceptionCode.java`

**왜 첫 번째인가:** 단독으로 컴파일·런타임 무중단 변경. JWT 필터들은 enum 필드만 직접 접근하므로 인터페이스 추가는 영향 없음. Task 3에서 핸들러가 `BaseErrorCode.getHttpStatus()`를 사용하므로, 향후 동일 추상화로 일관 처리할 토대.

- [ ] **Step 1.1: `AuthExceptionCode`에 `BaseErrorCode` import 및 implements 추가**

`src/main/java/com/spring/spring_init/common/security/exception/AuthExceptionCode.java` 전체 교체:

```java
package com.spring.spring_init.common.security.exception;

import com.spring.spring_init.common.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthExceptionCode implements BaseErrorCode {
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_ACCESS", "인증이 필요한 접근입니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근이 거부되었습니다"),
    TOKEN_EXPIRED(HttpStatus.valueOf(444), "TOKEN_EXPIRED", "토큰이 만료되었습니다");

    private final HttpStatus httpStatusCode;

    private final String code;

    private final String message;

    AuthExceptionCode(HttpStatus httpStatusCode, String code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatusCode;
    }
}
```

> 핵심 변경: `import com.spring.spring_init.common.base.BaseErrorCode;` 추가, `enum AuthExceptionCode implements BaseErrorCode {` 변경, `@Override public HttpStatus getHttpStatus()` 추가. 나머지는 동일. `getCode()`/`getMessage()`는 Lombok `@Getter`가 자동 생성하므로 명시 오버라이드 불필요(`UserExceptionCode`/`FileExceptionCode`는 명시했고 `EmailVerifyExceptionCode`/`CommonExceptionCode`는 명시 안 함 — 두 패턴 다 동작. 본 task에선 `EmailVerifyExceptionCode` 패턴(`getHttpStatus`만 오버라이드) 따른다).

- [ ] **Step 1.2: 컴파일 검증**

Run:
```bash
./gradlew compileJava
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 1.3: 커밋**

```bash
git add src/main/java/com/spring/spring_init/common/security/exception/AuthExceptionCode.java
git commit -m "refactor(auth): AuthExceptionCode implements BaseErrorCode

Aligns AuthExceptionCode with the other ExceptionCode enums (User,
File, EmailVerify, Common). Enables uniform handling via the
BaseErrorCode abstraction.

No behavior change — JWT filters access enum fields directly."
```

---

## Task 2: `CommonException` 시그니처 교체 + 23 콜사이트 일괄 치환 (단일 커밋)

**Files:**
- Modify: `src/main/java/com/spring/spring_init/common/exception/CommonException.java`
- Modify: `src/main/java/com/spring/spring_init/user/service/UserService.java`
- Modify: `src/main/java/com/spring/spring_init/verify/service/EmailVerifyService.java`
- Modify: `src/main/java/com/spring/spring_init/verify/service/VerifyService.java`
- Modify: `src/main/java/com/spring/spring_init/common/security/user/UserDetailsServiceImpl.java`
- Modify: `src/main/java/com/spring/spring_init/common/aws/service/FileService.java`

**왜 단일 커밋인가:** 구 생성자 `(String, String)`를 제거하면 23 콜사이트가 동시에 컴파일 에러. 단일 커밋으로 묶지 않으면 빌드 깨짐 상태로 히스토리에 남는다.

- [ ] **Step 2.1: `CommonException` 클래스 교체**

`src/main/java/com/spring/spring_init/common/exception/CommonException.java` 전체 교체:

```java
package com.spring.spring_init.common.exception;

import com.spring.spring_init.common.base.BaseErrorCode;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

    private final BaseErrorCode errorCode;

    public CommonException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }
}
```

> 변경 포인트:
> - 필드 `String code` 제거 → `BaseErrorCode errorCode`로 교체.
> - 구 생성자 `(String code, String message)` 제거.
> - 신규 생성자 `(BaseErrorCode)`만 노출.
> - `getCode()` shim 메서드 유지 → 기존 핸들러의 `e.getCode()` 호출 무중단.
> - `@Getter`가 `getErrorCode()` 자동 생성.

- [ ] **Step 2.2: `UserService.java` 11곳 치환**

`src/main/java/com/spring/spring_init/user/service/UserService.java`에서 다음 11개 블록을 차례로 변경.

**(2.2.a) L151-155** (`passwordReset` 내부, `NOT_FOUND_USER`):
```java
// Before
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
            () -> new CommonException(
                UserExceptionCode.NOT_FOUND_USER.getCode(),
                UserExceptionCode.NOT_FOUND_USER.getMessage()
            ));

// After
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
            () -> new CommonException(UserExceptionCode.NOT_FOUND_USER));
```

**(2.2.b) L208-214** (`getUser`, `NOT_FOUND_USER`):
```java
// Before
    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CommonException(
                UserExceptionCode.NOT_FOUND_USER.getCode(),
                UserExceptionCode.NOT_FOUND_USER.getMessage()
            ));
    }

// After
    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CommonException(UserExceptionCode.NOT_FOUND_USER));
    }
```

**(2.2.c) L217-224** (`validateUserId`, `NOT_MATCH_USER`):
```java
// Before
    private void validateUserId(Long id, UserDetailsImpl userDetails) {
        if (!userDetails.getUserId().equals(id)) {
            throw new CommonException(
                UserExceptionCode.NOT_MATCH_USER.getCode(),
                UserExceptionCode.NOT_MATCH_USER.getMessage()
            );
        }
    }

// After
    private void validateUserId(Long id, UserDetailsImpl userDetails) {
        if (!userDetails.getUserId().equals(id)) {
            throw new CommonException(UserExceptionCode.NOT_MATCH_USER);
        }
    }
```

**(2.2.d) L227-234** (`checkPasswordConfirm`, `PASSWORD_MISMATCH`):
```java
// Before
    private static void checkPasswordConfirm(RegisterUserRequestDto requestDto) {
        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            throw new CommonException(
                UserExceptionCode.PASSWORD_MISMATCH.getCode(),
                UserExceptionCode.PASSWORD_MISMATCH.getMessage()
            );
        }
    }

// After
    private static void checkPasswordConfirm(RegisterUserRequestDto requestDto) {
        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            throw new CommonException(UserExceptionCode.PASSWORD_MISMATCH);
        }
    }
```

**(2.2.e) L237-245** (`checkEmailExists`, `EXIST_EMAIL`):
```java
// Before
    private void checkEmailExists(RegisterUserRequestDto requestDto) {
        userRepository.findByEmail(requestDto.getEmail())
            .ifPresent(user -> {
                throw new CommonException(
                    UserExceptionCode.EXIST_EMAIL.getCode(),
                    UserExceptionCode.EXIST_EMAIL.getMessage()
                );
            });
    }

// After
    private void checkEmailExists(RegisterUserRequestDto requestDto) {
        userRepository.findByEmail(requestDto.getEmail())
            .ifPresent(user -> {
                throw new CommonException(UserExceptionCode.EXIST_EMAIL);
            });
    }
```

**(2.2.f) L253-259** (`validateEmailToken` 첫 번째 `orElseThrow`, `UNVERIFIED_EMAIL`):
```java
// Before
        EmailVerifier emailVerifier = emailVerifyRepository.findByEmailAndToken(
            email,
            token
        ).orElseThrow(() -> new CommonException(
            UserExceptionCode.UNVERIFIED_EMAIL.getCode(),
            UserExceptionCode.UNVERIFIED_EMAIL.getMessage()
        ));

// After
        EmailVerifier emailVerifier = emailVerifyRepository.findByEmailAndToken(
            email,
            token
        ).orElseThrow(() -> new CommonException(UserExceptionCode.UNVERIFIED_EMAIL));
```

**(2.2.g) L261-268** (`validateEmailToken` 두 번째 `orElseThrow`, `UNVERIFIED_EMAIL`):
```java
// Before
        EmailVerifier lastEmailVerifier =
            emailVerifyRepository.findFirstByEmailAndPurposeOrderByCreatedAtDesc(
                email,
                purpose
            ).orElseThrow(() -> new CommonException(
                UserExceptionCode.UNVERIFIED_EMAIL.getCode(),
                UserExceptionCode.UNVERIFIED_EMAIL.getMessage()
            ));

// After
        EmailVerifier lastEmailVerifier =
            emailVerifyRepository.findFirstByEmailAndPurposeOrderByCreatedAtDesc(
                email,
                purpose
            ).orElseThrow(() -> new CommonException(UserExceptionCode.UNVERIFIED_EMAIL));
```

**(2.2.h) L270-275** (`validateEmailToken` `INVALID_TOKEN`):
```java
// Before
        if (!emailVerifier.equals(lastEmailVerifier)) {
            throw new CommonException(
                EmailVerifyExceptionCode.INVALID_TOKEN.getCode(),
                EmailVerifyExceptionCode.INVALID_TOKEN.getMessage()
            );
        }

// After
        if (!emailVerifier.equals(lastEmailVerifier)) {
            throw new CommonException(EmailVerifyExceptionCode.INVALID_TOKEN);
        }
```

**(2.2.i) L286-292** (`validatePassword`, `NOT_MATCH_CURRENT_PASSWORD`):
```java
// Before
        if (!isResetPassword && !passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CommonException(
                UserExceptionCode.NOT_MATCH_CURRENT_PASSWORD.getCode(),
                UserExceptionCode.NOT_MATCH_CURRENT_PASSWORD.getMessage()
            );
        }

// After
        if (!isResetPassword && !passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CommonException(UserExceptionCode.NOT_MATCH_CURRENT_PASSWORD);
        }
```

**(2.2.j) L294-300** (`validatePassword`, `SAME_PASSWORD`):
```java
// Before
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new CommonException(
                UserExceptionCode.SAME_PASSWORD.getCode(),
                UserExceptionCode.SAME_PASSWORD.getMessage()
            );
        }

// After
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new CommonException(UserExceptionCode.SAME_PASSWORD);
        }
```

**(2.2.k) L302-308** (`validatePassword`, `NOT_MATCH_CHANGE_PASSWORD`):
```java
// Before
        if (!newPassword.equals(newPasswordConfirm)) {
            throw new CommonException(
                UserExceptionCode.NOT_MATCH_CHANGE_PASSWORD.getCode(),
                UserExceptionCode.NOT_MATCH_CHANGE_PASSWORD.getMessage()
            );
        }

// After
        if (!newPassword.equals(newPasswordConfirm)) {
            throw new CommonException(UserExceptionCode.NOT_MATCH_CHANGE_PASSWORD);
        }
```

- [ ] **Step 2.3: `EmailVerifyService.java` 8곳 치환**

`src/main/java/com/spring/spring_init/verify/service/EmailVerifyService.java`에서 다음 8개 블록 변경.

**(2.3.a) L68-78** (`verifyEmailConfirm`, `NOT_MATCH_CODE`):
```java
// Before
        EmailVerifier findedEmailVerifier =
            emailVerifyRepository.findByEmailAndCodeAndPurposeOrderByCreatedAtDesc(
                request.getEmail(),
                request.getCode(),
                EmailVerifyPurpose.EMAIL_VALIDATION
            ).orElseThrow(() ->
                new CommonException(
                    EmailVerifyExceptionCode.NOT_MATCH_CODE.getCode(),
                    EmailVerifyExceptionCode.NOT_MATCH_CODE.getMessage()
                )
            );

// After
        EmailVerifier findedEmailVerifier =
            emailVerifyRepository.findByEmailAndCodeAndPurposeOrderByCreatedAtDesc(
                request.getEmail(),
                request.getCode(),
                EmailVerifyPurpose.EMAIL_VALIDATION
            ).orElseThrow(() ->
                new CommonException(EmailVerifyExceptionCode.NOT_MATCH_CODE)
            );
```

**(2.3.b) L79-84** (`verifyEmailConfirm`, `TIME_OVER`):
```java
// Before
        if (isOverValidationTimeLimit(findedEmailVerifier.getCreatedAt())) {
            throw new CommonException(
                EmailVerifyExceptionCode.TIME_OVER.getCode(),
                EmailVerifyExceptionCode.TIME_OVER.getMessage()
            );
        }

// After
        if (isOverValidationTimeLimit(findedEmailVerifier.getCreatedAt())) {
            throw new CommonException(EmailVerifyExceptionCode.TIME_OVER);
        }
```

**(2.3.c) L94-99** (`verifyPasswordReset`, `NOT_FOUND_USER`):
```java
// Before
        User user = userRepository.findById(userId).orElseThrow(() ->
            new CommonException(
                UserExceptionCode.NOT_FOUND_USER.getCode(),
                UserExceptionCode.NOT_FOUND_USER.getMessage()
            )
        );

// After
        User user = userRepository.findById(userId).orElseThrow(() ->
            new CommonException(UserExceptionCode.NOT_FOUND_USER)
        );
```

**(2.3.d) L102-112** (`verifyPasswordReset` 첫 token 검증, `INVALID_TOKEN`):
```java
// Before
        EmailVerifier emailVerifier =
            emailVerifyRepository.findByEmailAndTokenAndPurpose(
                user.getEmail(),
                request.getToken(),
                EmailVerifyPurpose.RESET_PASSWORD
            ).orElseThrow(() ->
                new CommonException(
                    EmailVerifyExceptionCode.INVALID_TOKEN.getCode(),
                    EmailVerifyExceptionCode.INVALID_TOKEN.getMessage()
                )
            );

// After
        EmailVerifier emailVerifier =
            emailVerifyRepository.findByEmailAndTokenAndPurpose(
                user.getEmail(),
                request.getToken(),
                EmailVerifyPurpose.RESET_PASSWORD
            ).orElseThrow(() ->
                new CommonException(EmailVerifyExceptionCode.INVALID_TOKEN)
            );
```

**(2.3.e) L114-124** (`verifyPasswordReset` 마지막 토큰 검증, `INVALID_TOKEN`):
```java
// Before
        EmailVerifier firstByEmailAndPurpose =
            emailVerifyRepository.findFirstByEmailAndPurposeOrderByCreatedAtDesc(
                user.getEmail(),
                EmailVerifyPurpose.RESET_PASSWORD
            ).orElseThrow(() ->
                new CommonException(
                    EmailVerifyExceptionCode.INVALID_TOKEN.getCode(),
                    EmailVerifyExceptionCode.INVALID_TOKEN.getMessage()
                )
            );

// After
        EmailVerifier firstByEmailAndPurpose =
            emailVerifyRepository.findFirstByEmailAndPurposeOrderByCreatedAtDesc(
                user.getEmail(),
                EmailVerifyPurpose.RESET_PASSWORD
            ).orElseThrow(() ->
                new CommonException(EmailVerifyExceptionCode.INVALID_TOKEN)
            );
```

**(2.3.f) L126-131** (`verifyPasswordReset` 일치 검증, `INVALID_TOKEN`):
```java
// Before
        if (!emailVerifier.equals(firstByEmailAndPurpose)) {
            throw new CommonException(
                EmailVerifyExceptionCode.INVALID_TOKEN.getCode(),
                EmailVerifyExceptionCode.INVALID_TOKEN.getMessage()
            );
        }

// After
        if (!emailVerifier.equals(firstByEmailAndPurpose)) {
            throw new CommonException(EmailVerifyExceptionCode.INVALID_TOKEN);
        }
```

**(2.3.g) L133-139** (`verifyPasswordReset` 시간 검증, `TIME_OVER`):
```java
// Before
        if (isOverValidationTimeLimit(firstByEmailAndPurpose.getCreatedAt())) {
            throw new CommonException(
                EmailVerifyExceptionCode.TIME_OVER.getCode(),
                EmailVerifyExceptionCode.TIME_OVER.getMessage()
            );
        }

// After
        if (isOverValidationTimeLimit(firstByEmailAndPurpose.getCreatedAt())) {
            throw new CommonException(EmailVerifyExceptionCode.TIME_OVER);
        }
```

**(2.3.h) L148-155** (`checkIfEmailExists`, `EXIST_EMAIL`):
```java
// Before
    private void checkIfEmailExists(final String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new CommonException(
                UserExceptionCode.EXIST_EMAIL.getCode(),
                UserExceptionCode.EXIST_EMAIL.getMessage()
            );
        });
    }

// After
    private void checkIfEmailExists(final String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new CommonException(UserExceptionCode.EXIST_EMAIL);
        });
    }
```

- [ ] **Step 2.4: `VerifyService.java` 2곳 치환**

`src/main/java/com/spring/spring_init/verify/service/VerifyService.java`:

```java
// Before (L20-35)
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

// After
    public void verifyPassword(VerifyPasswordRequestDto requestDto, UserDetailsImpl userDetails) {
        // 사용자 정보 조회
        User user = userRepository.findById(userDetails.getUserId())
            .orElseThrow(() -> new CommonException(UserExceptionCode.NOT_FOUND_USER));

        // 현재 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CommonException(UserExceptionCode.NOT_MATCH_CURRENT_PASSWORD);
        }
    }
```

- [ ] **Step 2.5: `UserDetailsServiceImpl.java` 1곳 치환**

`src/main/java/com/spring/spring_init/common/security/user/UserDetailsServiceImpl.java`:

```java
// Before (L22-29)
    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
            new CommonException(
                UserExceptionCode.LOGIN_FAIL.getCode(),
                UserExceptionCode.LOGIN_FAIL.getMessage()
            )
        );

// After
    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
            new CommonException(UserExceptionCode.LOGIN_FAIL)
        );
```

- [ ] **Step 2.6: `FileService.java` 1곳 치환**

`src/main/java/com/spring/spring_init/common/aws/service/FileService.java`:

```java
// Before (L112-117)
        } catch (Exception e) {
            throw new CommonException(
                FileExceptionCode.FAIL_UPLOAD_FILE.getCode(),
                FileExceptionCode.FAIL_UPLOAD_FILE.getMessage()
            );
        }

// After
        } catch (Exception e) {
            throw new CommonException(FileExceptionCode.FAIL_UPLOAD_FILE);
        }
```

- [ ] **Step 2.7: 컴파일 검증**

Run:
```bash
./gradlew compileJava
```

Expected: BUILD SUCCESSFUL. 만약 실패하면 누락된 콜사이트가 있다는 의미 — 에러 라인을 확인해서 같은 패턴으로 치환.

- [ ] **Step 2.8: 잔여 콜사이트 grep 확인 (안전망)**

Run:
```bash
grep -rn "new CommonException(" src/main/java --include="*.java"
```

Expected 출력 라인:
- 23개 라인 모두 `new CommonException(<EnumCode>)` 형태 (1-인자만)
- `getCode()` 또는 `getMessage()`가 같은 라인에 나오면 안 됨

만약 구 패턴이 남아있으면 해당 위치를 위 단계와 같이 치환.

- [ ] **Step 2.9: 단일 커밋으로 묶기**

```bash
git add \
  src/main/java/com/spring/spring_init/common/exception/CommonException.java \
  src/main/java/com/spring/spring_init/user/service/UserService.java \
  src/main/java/com/spring/spring_init/verify/service/EmailVerifyService.java \
  src/main/java/com/spring/spring_init/verify/service/VerifyService.java \
  src/main/java/com/spring/spring_init/common/security/user/UserDetailsServiceImpl.java \
  src/main/java/com/spring/spring_init/common/aws/service/FileService.java
git commit -m "refactor(exception): simplify CommonException constructor

- CommonException now takes a single BaseErrorCode argument and
  exposes getErrorCode() so handlers can access HttpStatus etc.
- Removed (String code, String message) constructor.
- Migrated all 23 call sites in 5 service files.
- getCode() shim retained for handler/Swagger compatibility."
```

---

## Task 3: `CommonExceptionHandler` httpStatus 버그 수정

**Files:**
- Modify: `src/main/java/com/spring/spring_init/common/exception/CommonExceptionHandler.java`

**왜 마지막인가:** Task 2에서 `CommonException`이 `errorCode`를 보유하므로 핸들러가 `getHttpStatus()`를 호출할 수 있다. 응답 코드 동작 변경(특히 `NOT_FOUND_USER` 400→404)은 PR description에 명시되는 단독 변경이라 별도 커밋이 안전.

- [ ] **Step 3.1: 핸들러 메서드 수정**

`src/main/java/com/spring/spring_init/common/exception/CommonExceptionHandler.java`의 `commonExceptionHandler` 메서드만 변경:

```java
// Before
    @JsonView(Common.class)
    @ExceptionHandler(value = CommonException.class)
    public ResponseEntity<ErrorResponseDTO> commonExceptionHandler(CommonException e) {
        return ResponseEntity.badRequest()
            .body(
                new ErrorResponseDTO(
                    e.getCode(),
                    e.getMessage())
            );
    }

// After
    @JsonView(Common.class)
    @ExceptionHandler(value = CommonException.class)
    public ResponseEntity<ErrorResponseDTO> commonExceptionHandler(CommonException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
            .body(
                new ErrorResponseDTO(
                    e.getCode(),
                    e.getMessage())
            );
    }
```

> 변경: `ResponseEntity.badRequest()` → `ResponseEntity.status(e.getErrorCode().getHttpStatus())`. 다른 두 핸들러(`handleMethodArgumentNotValidException`, `handleException`)는 손대지 않는다.

- [ ] **Step 3.2: 컴파일 + 전체 빌드 검증**

Run:
```bash
./gradlew build -x test
```

Expected: BUILD SUCCESSFUL. (테스트 미작성 합의 — `-x test`로 스킵.)

- [ ] **Step 3.3: 커밋**

```bash
git add src/main/java/com/spring/spring_init/common/exception/CommonExceptionHandler.java
git commit -m "fix(exception): use BaseErrorCode.getHttpStatus() in handler

The handler always returned 400 BAD_REQUEST, ignoring the HttpStatus
defined on each error code. With this fix:

- NOT_FOUND_USER now returns 404 (was 400)
- Other current codes already mapped to BAD_REQUEST → no change

Breaking: clients branching on 400 for NOT_FOUND_USER must update."
```

---

## Final Verification

- [ ] **Step F.1: 잔여 구 패턴 재확인**

Run:
```bash
grep -rn "CommonException(.*\.getCode()" src/main/java --include="*.java"
```

Expected: 출력 없음 (exit code 1).

- [ ] **Step F.2: 풀 빌드**

Run:
```bash
./gradlew build -x test
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step F.3: 커밋 로그 확인**

Run:
```bash
git log --oneline main..HEAD
```

Expected (3 신규 커밋):
```
<sha> fix(exception): use BaseErrorCode.getHttpStatus() in handler
<sha> refactor(exception): simplify CommonException constructor
<sha> refactor(auth): AuthExceptionCode implements BaseErrorCode
```

(스펙 문서 커밋 2개 — `docs: add exception refactor design spec`, `docs: fix call site count` — 도 함께 보임.)
