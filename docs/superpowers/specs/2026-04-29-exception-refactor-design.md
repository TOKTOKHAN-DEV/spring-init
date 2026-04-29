# Exception Handling Refactor — Design

**Date:** 2026-04-29
**Status:** Draft
**Scope:** `CommonException` 생성자 단순화 + 핸들러 httpStatus 버그 수정 + `AuthExceptionCode` 일관성 정리

---

## 1. 배경 및 목표

### 현재 패턴

```java
throw new CommonException(
    UserExceptionCode.NOT_FOUND_USER.getCode(),
    UserExceptionCode.NOT_FOUND_USER.getMessage()
);
```

코드베이스 전체 22 콜사이트가 동일한 보일러플레이트(`enum.getCode()` + `enum.getMessage()`)를 반복하고 있음.

### 목표 패턴

```java
throw new CommonException(UserExceptionCode.NOT_FOUND_USER);
```

ErrorCode enum 단일 인자만으로 예외 발생 가능하게 한다.

### 부수적으로 해결할 인접 이슈

리팩토링 흐름에서 자연스럽게 발견·정리할 항목:

1. **`CommonExceptionHandler.commonExceptionHandler()` httpStatus 버그**
   - 현재 항상 `ResponseEntity.badRequest()` (400) 반환.
   - `BaseErrorCode.getHttpStatus()`가 정의되어 있음에도 핸들러가 사용하지 않음.
   - 결과: `NOT_FOUND_USER`(`HttpStatus.NOT_FOUND`)도 400으로 응답되는 등 의미 불일치.

2. **`AuthExceptionCode`가 `BaseErrorCode`를 구현하지 않음**
   - 다른 모든 ExceptionCode enum(User, File, EmailVerify, Common)은 `BaseErrorCode`를 구현.
   - `AuthExceptionCode`만 누락 → 일관성 깨짐.

---

## 2. 비목표 (Out of Scope)

- JWT 필터 계열 응답 패턴 변경 (`JwtTokenFilter`, `JwtAuthenticationEntryPoint`, `JwtAccessDeniedHandler`)
  - 이들은 `CommonException`을 throw하지 않고 `ErrorResponseDTO`를 직접 response에 write. 현재 구조 유지.
- 동적 메시지 오버로드 (`CommonException(BaseErrorCode, String detail)` 등)
  - 현재 사용처 없음. YAGNI.
- 단위/통합 테스트 추가
  - 본 PR 범위에서 제외.
- `MethodArgumentNotValidException`, `Exception.class` 폴백 핸들러 변경
  - 그대로 유지.
- `ApiExceptionExplainParser`, `@ApiExceptionExplanation` 등 Swagger 문서화 도구 변경
  - `BaseErrorCode.getHttpStatus()` 기반으로 이미 동작 중. 변경 불필요.

---

## 3. 아키텍처

### 3.1 `CommonException` 변경

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

**핵심 변경:**
- 기존 `(String code, String message)` 생성자 **제거**.
- 신규 `(BaseErrorCode)` 생성자 단일 진입점.
- `errorCode` 필드 보유 → 핸들러에서 `getHttpStatus()` 등 풀세트 접근 가능.
- `getCode()` shim 메서드 유지 → 기존 핸들러 코드(`e.getCode()`) 무중단.

**대안 검토:**
- *코드/메시지/httpStatus를 분리해서 필드로 저장 (옵션 B)* → `errorCode` 객체 노출 안 됨. 그러나 핸들러에서 진단·로깅 시 정보가 약함. **불채택**.
- *정적 팩토리 `CommonException.of(...)` (옵션 C)* → 향후 확장은 유연하나, 현재 `throw new ...` 관용을 깨뜨리고 즉각 효용 없음. **불채택**.

### 3.2 `CommonExceptionHandler` 버그 수정

```java
@JsonView(Common.class)
@ExceptionHandler(value = CommonException.class)
public ResponseEntity<ErrorResponseDTO> commonExceptionHandler(CommonException e) {
    return ResponseEntity.status(e.getErrorCode().getHttpStatus())
        .body(new ErrorResponseDTO(e.getCode(), e.getMessage()));
}
```

- `ResponseEntity.badRequest()` → `ResponseEntity.status(...)`로 변경.
- enum이 정의한 `HttpStatus`를 그대로 사용.

### 3.3 `AuthExceptionCode` 일관화

```java
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

- `implements BaseErrorCode` 추가.
- `getHttpStatus()` 오버라이드.
- 다른 enum(User/File/EmailVerify/Common)과 동일 패턴.
- JWT 필터 사용처는 현 시점 영향 없음 (필드 접근 그대로).

---

## 4. 콜사이트 마이그레이션

### 4.1 대상

| 파일 | 콜사이트 수 |
|---|---|
| `user/service/UserService.java` | 11 |
| `verify/service/EmailVerifyService.java` | 7 |
| `verify/service/VerifyService.java` | 2 |
| `common/security/user/UserDetailsServiceImpl.java` | 1 |
| `common/aws/service/FileService.java` | 1 |
| **합계** | **22** |

### 4.2 변환 규칙

```java
// Before
new CommonException(X.getCode(), X.getMessage())

// After
new CommonException(X)
```

- 기계적 치환 (IDE refactor 또는 정규식).
- 구 생성자 제거로 누락 시 **컴파일 에러로 강제 검출**.

---

## 5. 영향 범위

### 5.1 동작 변화 (Breaking)

`commonExceptionHandler` 응답 코드가 enum 정의에 따라 변경됨:

| ErrorCode | 수정 전 | 수정 후 |
|---|---|---|
| `NOT_FOUND_USER` | 400 | **404** |
| `EXIST_EMAIL` | 400 | 400 |
| `UNVERIFIED_EMAIL` | 400 | 400 |
| `PASSWORD_MISMATCH` | 400 | 400 |
| `LOGIN_FAIL` | 400 | 400 |
| `NOT_MATCH_*` 계열 | 400 | 400 |
| `EXIST_*` 계열 | 400 | 400 |
| `INVALID_TOKEN` (EmailVerify) | 400 | 400 |
| `TIME_OVER` | 400 | 400 |
| `FAIL_UPLOAD_FILE` | 400 | 400 |
| `FIELD_ERROR`(별도 핸들러) | 422 | 422 |
| `INTERNAL_SERVER_ERROR`(별도 핸들러) | 500 | 500 |

→ 실질적으로 변하는 매핑은 **`NOT_FOUND_USER` 한 건뿐 (400 → 404)**. 클라이언트가 400 기준으로 분기 중이라면 영향. **PR description에 명시 필요**.

### 5.2 컴파일/런타임 영향 없음

- `JwtAuthenticationEntryPoint`, `JwtAccessDeniedHandler`, `JwtTokenFilter` — `AuthExceptionCode` 필드 접근 패턴 동일하게 동작.
- `ApiExceptionExplainParser` — `BaseErrorCode.getHttpStatus()` 기반 스웨거 응답 생성 로직 그대로.
- `ErrorResponseDTO` — 필드/생성자 변경 없음.

---

## 6. 마이그레이션 순서

작업은 다음 순서로 진행. 단계 2의 시그니처 교체와 콜사이트 치환은 **한 커밋**으로 묶어 빌드 깨짐 상태가 남지 않도록 한다.

1. **`AuthExceptionCode`에 `BaseErrorCode` 구현 추가**
   - 무중단. 단독 커밋.

2. **`CommonException` 시그니처 교체 + 22 콜사이트 일괄 치환 (단일 커밋)**
   - 신규 생성자 추가, 구 생성자 제거.
   - 콜사이트 5개 파일 22곳 치환.
   - 한 커밋 안에서 완료해야 빌드가 항상 통과.

3. **`CommonExceptionHandler.commonExceptionHandler()` httpStatus 버그 수정**
   - `ResponseEntity.status(e.getErrorCode().getHttpStatus())`.
   - 단독 커밋.

---

## 7. 위험 및 완화

| 위험 | 완화 |
|---|---|
| `NOT_FOUND_USER` 응답코드 변경(400→404)이 클라이언트 영향 | PR description에 breaking change 명시. 변경 매핑 표 첨부. |
| 22 콜사이트 누락 | 구 생성자 제거 → 컴파일러가 강제 검출. |
| 빌드 깨진 중간 상태 커밋 | 시그니처 변경 + 콜사이트 치환을 한 커밋에 묶음. |

---

## 8. 검증

테스트 추가는 본 PR 범위에서 제외. 검증은 다음으로 한정:

- `./gradlew build` 통과 — 22 콜사이트 마이그레이션 완전성 보장.
- 핵심 시나리오 수동 확인 (선택):
  - `GET /users/{id}` with non-existent id → 404 + `{"errorCode":"NOT_FOUND_USER", ...}`
  - 회원가입 시 중복 이메일 → 400 + `{"errorCode":"EXIST_EMAIL", ...}`
