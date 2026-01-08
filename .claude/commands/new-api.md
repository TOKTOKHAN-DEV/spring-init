---
description: 새로운 REST API 엔드포인트를 생성합니다 (Controller, Service, DTO 포함)
---

# 새로운 API 엔드포인트 생성

새로운 REST API 엔드포인트와 필요한 Controller, Service, DTO를 생성합니다.

## 작업 순서

1. **API 요구사항 확인**
   - HTTP 메서드 (GET, POST, PUT, PATCH, DELETE)
   - 엔드포인트 경로 (예: /api/v1/users/{userId})
   - Request DTO (요청 파라미터, body)
   - Response DTO (응답 데이터)
   - 인증/인가 필요 여부
   - 비즈니스 로직

2. **DTO 생성**

   **Request DTO** (필요시)
   - 위치: `src/main/java/com/spring/{project_name}/{domain}/dto/request/{Feature}RequestDto.java`
   - Spring Validation 어노테이션 추가 (@NotNull, @NotBlank, @Email, @Size 등)
   - Swagger 문서화 어노테이션 추가 (@Schema)

   **Response DTO** (필요시)
   - 위치: `src/main/java/com/spring/{project_name}/{domain}/dto/response/{Feature}ResponseDto.java`
   - Entity → DTO 변환 메서드 작성 (of, from 등)
   - Swagger 문서화 어노테이션 추가 (@Schema)

3. **Service 메서드 추가**
   - 위치: `src/main/java/com/spring/{project_name}/{domain}/service/{Domain}Service.java`
   - 비즈니스 로직 구현
   - 필요시 @Transactional 추가
   - 예외 처리 (CommonException 사용)
   - Repository를 통한 데이터 접근

4. **API 인터페이스 정의**
   - 위치: `src/main/java/com/spring/{project_name}/{domain}/controller/{Domain}Api.java`
   - Swagger 문서화 어노테이션 작성
     - @Tag: API 그룹 정의
     - @Operation: API 설명
     - @ApiResponse: 응답 코드 및 설명
     - @ApiExceptionExplanation: 발생 가능한 예외 문서화
   - 메서드 시그니처만 정의 (구현 없음)

5. **Controller 구현**
   - 위치: `src/main/java/com/spring/{project_name}/{domain}/controller/{Domain}Controller.java`
   - API 인터페이스 구현
   - @RestController, @RequestMapping 추가
   - Service 주입 및 호출
   - ResponseDTO로 응답 래핑
   - 적절한 HTTP 상태 코드 반환

6. **보안 설정** (필요시)
   - `common/security/config/SecurityConfig.java`에서 엔드포인트별 인증/인가 설정
   - 또는 Controller 메서드에 @PreAuthorize 추가

## 예시

기존 User API를 참고하세요:
- API 인터페이스: `src/main/java/com/spring/{project_name}/user/controller/UserApi.java`
- Controller 구현: `src/main/java/com/spring/{project_name}/user/controller/UserController.java`
- Service: `src/main/java/com/spring/{project_name}/user/service/UserService.java`
- Request DTO: `src/main/java/com/spring/{project_name}/user/dto/request/LoginRequestDto.java`
- Response DTO: `src/main/java/com/spring/{project_name}/user/dto/response/LoginResponseDto.java`

**참고**: `{project_name}` 부분은 실제 프로젝트명으로 변경됩니다.

## RESTful API 설계 원칙

- **GET**: 리소스 조회 (목록, 단건)
- **POST**: 리소스 생성
- **PUT**: 리소스 전체 수정
- **PATCH**: 리소스 부분 수정
- **DELETE**: 리소스 삭제

**엔드포인트 네이밍**:
- 복수형 명사 사용: `/api/v1/users`, `/api/v1/posts`
- 계층 구조: `/api/v1/users/{userId}/posts`
- 동사 사용 지양, 필요시 명확한 의미 전달: `/api/v1/users/{userId}/activate`

**HTTP 상태 코드**:
- 200 OK: 성공 (GET, PUT, PATCH)
- 201 Created: 리소스 생성 성공 (POST)
- 204 No Content: 성공, 응답 본문 없음 (DELETE)
- 400 Bad Request: 잘못된 요청
- 401 Unauthorized: 인증 실패
- 403 Forbidden: 권한 없음
- 404 Not Found: 리소스 없음
- 500 Internal Server Error: 서버 오류

## 주의사항

- **API 인터페이스 분리**: Controller는 API 인터페이스를 구현하는 형태로 작성합니다
- **Swagger 문서화**: API 인터페이스에 충분한 문서화 어노테이션을 추가합니다
- **Validation**: Request DTO에 적절한 검증 로직을 추가합니다
- **ResponseDTO 래핑**: 모든 응답은 `ResponseDTO.of()` 또는 `ResponseDTO.ok()`로 래핑합니다
- **예외 처리**: 비즈니스 예외는 CommonException을 사용하고, 도메인별 ExceptionCode를 정의합니다
- **트랜잭션**: 데이터 변경 작업은 @Transactional을 추가합니다
- **보안**: 인증이 필요한 API는 SecurityConfig 또는 @PreAuthorize로 보호합니다

## Swagger 어노테이션 예시

```java
@Tag(name = "User API", description = "사용자 관련 API")
public interface UserApi {

    @Operation(
        summary = "사용자 정보 조회",
        description = "사용자 ID로 사용자 정보를 조회합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "조회 성공"
    )
    @ApiExceptionExplanation(value = UserExceptionCode.class)
    ResponseEntity<ResponseDTO<UserInfoResponseDto>> getUserInfo(
        @Parameter(description = "사용자 ID", required = true) Long userId
    );
}
```

## 생성 후 확인 사항

- [ ] Request/Response DTO가 생성되었는가?
- [ ] DTO에 Validation 어노테이션이 추가되었는가?
- [ ] Service 메서드가 구현되었는가?
- [ ] API 인터페이스에 Swagger 어노테이션이 추가되었는가?
- [ ] Controller가 API 인터페이스를 구현하는가?
- [ ] 응답이 ResponseDTO로 래핑되었는가?
- [ ] 필요한 경우 보안 설정이 추가되었는가?
- [ ] Swagger UI에서 API 문서가 올바르게 표시되는가? (http://localhost:8080/swagger-ui/index.html)
