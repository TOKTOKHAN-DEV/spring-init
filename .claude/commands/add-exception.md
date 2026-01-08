---
description: 새로운 도메인 예외 코드를 추가하거나 기존 예외 코드에 새로운 에러를 추가합니다
---

# Exception 코드 추가

도메인별 예외 처리를 위한 ExceptionCode enum을 생성하거나 기존 ExceptionCode에 새로운 에러 코드를 추가합니다.

## 프로젝트의 예외 처리 구조

이 프로젝트는 일관된 예외 처리를 위해 다음과 같은 구조를 사용합니다:

1. **BaseErrorCode**: 모든 에러 코드가 구현해야 하는 인터페이스
2. **CommonException**: 비즈니스 예외를 표현하는 런타임 예외
3. **{Domain}ExceptionCode**: 도메인별 에러 코드를 정의하는 enum (BaseErrorCode 구현)
4. **CommonExceptionHandler**: 전역 예외 처리기

## 작업 순서

### 1. 새로운 도메인의 ExceptionCode 생성

**ExceptionCode Enum 생성**
- 위치: `src/main/java/com/spring/{project_name}/{domain}/exception/{Domain}ExceptionCode.java`
- BaseErrorCode 인터페이스 구현
- 각 에러 코드는 HTTP 상태 코드와 에러 메시지를 포함

**참고**: `{project_name}` 부분은 실제 프로젝트명으로 변경됩니다.

**기존 예시 확인**:
- `common/exception/CommonExceptionCode.java`
- `common/security/exception/AuthExceptionCode.java`
- `user/exception/UserExceptionCode.java`
- `verify/exception/EmailVerifyExceptionCode.java`
- `common/aws/exception/FileExceptionCode.java`

### 2. 기존 ExceptionCode에 새로운 에러 추가

기존 {Domain}ExceptionCode enum 파일을 열어 새로운 에러 코드를 추가합니다.

## ExceptionCode Enum 구조

```java
@Getter
@RequiredArgsConstructor
public enum UserExceptionCode implements BaseErrorCode {

    // HTTP 상태 코드, 에러 코드, 에러 메시지
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER_002", "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER_003", "비밀번호가 일치하지 않습니다."),
    // ... 추가 에러 코드
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorResponseDTO getErrorResponse() {
        return ErrorResponseDTO.of(false, this.code, this.message);
    }
}
```

## 예외 사용 방법

Service나 다른 비즈니스 로직에서 예외를 던질 때:

```java
// 사용자를 찾을 수 없을 때
throw new CommonException(UserExceptionCode.USER_NOT_FOUND);

// 추가 메시지와 함께 예외를 던질 때
throw new CommonException(
    UserExceptionCode.USER_NOT_FOUND,
    "사용자 ID: " + userId
);
```

## 에러 코드 네이밍 규칙

**에러 코드 형식**: `{DOMAIN}_{NUMBER}`
- USER_001, USER_002, ...
- AUTH_001, AUTH_002, ...
- FILE_001, FILE_002, ...

**에러 상수명**:
- 대문자 스네이크 케이스
- 의미를 명확히 전달
- 예: `USER_NOT_FOUND`, `DUPLICATE_EMAIL`, `INVALID_PASSWORD`

## HTTP 상태 코드 선택 가이드

- **400 Bad Request**: 잘못된 요청, 유효성 검증 실패
- **401 Unauthorized**: 인증 실패 (로그인 필요)
- **403 Forbidden**: 권한 없음 (로그인은 되어있으나 접근 권한 없음)
- **404 Not Found**: 리소스를 찾을 수 없음
- **409 Conflict**: 리소스 충돌 (중복 등록 등)
- **500 Internal Server Error**: 서버 내부 오류

## Swagger 문서화

API 인터페이스에서 발생 가능한 예외를 문서화:

```java
@ApiExceptionExplanation(value = UserExceptionCode.class)
```

이 어노테이션은 해당 API에서 발생할 수 있는 모든 예외를 Swagger 문서에 자동으로 추가합니다.

## 전역 예외 처리

모든 CommonException은 `CommonExceptionHandler`에서 자동으로 처리되어 클라이언트에게 일관된 형식의 에러 응답을 반환합니다:

```json
{
  "success": false,
  "code": "USER_001",
  "message": "사용자를 찾을 수 없습니다."
}
```

## 주의사항

- **BaseErrorCode 구현**: 모든 ExceptionCode enum은 BaseErrorCode를 구현해야 합니다
- **HTTP 상태 코드**: 적절한 HTTP 상태 코드를 선택합니다
- **에러 코드 고유성**: 에러 코드(예: USER_001)는 프로젝트 전체에서 고유해야 합니다
- **명확한 메시지**: 에러 메시지는 명확하고 사용자 친화적이어야 합니다
- **Lombok**: @Getter, @RequiredArgsConstructor 어노테이션을 사용합니다
- **enum 순서**: 관련된 에러 코드끼리 그룹화하여 가독성을 높입니다

## 생성 후 확인 사항

- [ ] ExceptionCode enum이 BaseErrorCode를 구현하는가?
- [ ] 각 에러 코드가 고유한 코드 값을 가지는가? (예: USER_001, USER_002)
- [ ] 적절한 HTTP 상태 코드가 설정되었는가?
- [ ] 에러 메시지가 명확한가?
- [ ] Swagger API 문서에 @ApiExceptionExplanation이 추가되었는가?
- [ ] Service 로직에서 적절히 예외를 던지는가?
