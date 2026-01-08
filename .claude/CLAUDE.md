# Spring Boot 프로젝트
TODO: 각 프로젝트 대략적인 설명 적어주시면 됩니다.

---

## 🏗️ 기술 스택
TODO: 각 프로젝트에 맞도록 기술 스택 수정해주시면 됩니다.

**Framework & Language**
- Spring Boot 3.3.5
- Spring Security
- Spring Data JPA
- Java 17

**ORM & Database**
- JPA + QueryDSL 5.0.0
- PostgreSQL

**Authentication**
- JWT (jjwt 0.11.5)
- OAuth2 (Google, Kakao, Naver)

**Documentation**
- Swagger/OpenAPI 3.0 (springdoc 2.5.0)

**Cloud Services**
- AWS S3 (파일 저장)
- AWS SES (이메일 발송)
- AWS Secrets Manager (민감 정보 관리)

**Build Tool**
- Gradle 8.10.2

---

## 📁 패키지 구조

```
src/main/java/com/spring/{project_name}/
├── {ProjectName}Application.java
├── common/                    # 공통 설정
│   ├── apidocs/              # Swagger 설정
│   ├── aws/                  # AWS 통합 (S3, SES, Secrets Manager)
│   ├── base/                 # BaseEntity, BaseErrorCode
│   ├── dto/                  # 공통 DTO (ResponseDTO, ErrorResponseDTO 등)
│   ├── exception/            # 전역 예외 처리
│   ├── persistence/          # JPA, QueryDSL 설정
│   └── security/             # Spring Security, JWT 설정
│
└── {domain}/                  # 도메인별 구성
    ├── controller/           # API 인터페이스 + 구현
    ├── service/              # 비즈니스 로직
    ├── repository/           # 데이터 접근 (JPA + QueryDSL)
    ├── entity/               # JPA 엔티티
    ├── dto/                  # 요청/응답 DTO
    └── exception/            # 도메인별 예외 코드
```

**참고**: `{project_name}` 부분은 각 프로젝트명으로 변경됩니다.
예시: `spring_init`, `ecommerce`, `blog` 등

**도메인 예시**: `user`, `verify`, `oauth`

---

## 🏛️ 아키텍처 패턴

### Layered Architecture
```
Controller (API 계층)
    ↓
Service (비즈니스 로직)
    ↓
Repository (데이터 접근)
    ↓
Entity (도메인 모델)
```

### 핵심 패턴

1. **DTO Pattern**: 계층 간 데이터 전달은 DTO를 사용합니다
2. **Repository Pattern**: 데이터 접근 로직을 추상화합니다
   - JpaRepository: 기본 CRUD
   - Custom Repository + Impl: QueryDSL로 복잡한 쿼리 구현
3. **Exception Handling**: 전역 예외 처리기(`CommonExceptionHandler`)를 통해 일관된 에러 응답 제공
4. **Security**: JWT 기반 인증, Spring Security를 통한 인가 처리
5. **Response Wrapper**: 모든 API 응답은 `ResponseDTO`로 래핑합니다

---

## 📝 코딩 컨벤션

### 네이밍 규칙

| 대상 | 규칙 | 예시 |
|------|------|------|
| **클래스** | PascalCase | `UserService`, `JwtTokenProvider` |
| **메서드** | camelCase | `getUserById`, `validateToken` |
| **변수** | camelCase | `userId`, `accessToken` |
| **상수** | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT`, `DEFAULT_PAGE_SIZE` |
| **패키지** | lowercase | `com.spring.{project_name}.user.service` |
| **인터페이스** | PascalCase, 'I' 접두사 사용 안함 | `UserRepository`, `TokenProvider` |
| **DTO** | PascalCase + Dto/Request/Response | `LoginRequestDto`, `UserResponseDto` |

### 파일 구성 원칙

**Controller**
- API 인터페이스와 구현을 분리합니다
- `{Domain}Api.java`: Swagger 문서화 + 메서드 시그니처
- `{Domain}Controller.java`: 실제 구현 + `@RestController`

**Service**
- 비즈니스 로직을 포함하며 트랜잭션 경계를 정의합니다
- `@Transactional` 어노테이션으로 트랜잭션 관리

**Repository**
- JpaRepository 인터페이스와 QueryDSL 구현을 분리합니다
- `{Entity}JpaRepository.java`: Spring Data JPA 인터페이스
- `{Entity}QueryRepository.java`: QueryDSL 구현
- `{Entity}Repository.java`: JPA 인터페이스와 QueryDSL 구현체를 주입 받아 Servivce에서 사용하는 Repository

**Entity**
- JPA 엔티티는 `BaseEntity`를 상속받아 공통 필드 관리 (`createdAt`, `updatedAt`)
- Lombok 어노테이션 활용: `@Getter`, `@NoArgsConstructor`, `@Builder`
- 연관관계 매핑 시 `FetchType.LAZY`를 기본으로 사용

**DTO**
- 요청/응답 DTO는 별도 패키지(`dto/request`, `dto/response`)로 관리
- 스웨거 스키마를 위한 어노테이션 사용 : @Schema(requiredMode = RequiredMode.REQUIRED, description = "", example = "")
  - RequiredMode.REQUIRED : Not null
  - RequiredMode.NOT_REQUIRED : FE optional
- Spring Validation 어노테이션 사용: `@NotNull`, `@NotBlank`, `@Email`, `@Size`

**Exception**
- 도메인별 `{Domain}ExceptionCode` enum 정의
- `BaseErrorCode` 인터페이스 구현
- `CommonException`을 통해 예외 발생

### 주석 스타일
- **코드는 자체 설명적이어야 하며**, 필요한 경우에만 주석을 추가합니다
- **Javadoc**: Public API에는 Javadoc을 작성합니다
- **인라인 주석**: 복잡한 비즈니스 로직이나 알고리즘에는 인라인 주석을 추가합니다

---

## 🛡️ 보안 가이드라인
### 필수 규칙
1. **민감한 정보 보호**
   - 절대 민감한 정보(비밀번호, API 키 등)를 하드코딩하지 않습니다

2. **입력 검증**
   - 사용자 입력은 항상 검증합니다 (Spring Validation 사용)
   - SQL Injection 방지를 위해 QueryDSL이나 JPA를 사용합니다

3. **인증/인가**
   - 기본적으로 인증을 사용하도록 하고 인증이 필요하지 않은 API는 `SecurityConfig`에서 별도로 허용합니다.

---

## 🧪 테스트 가이드라인

1. **새로운 기능 추가 시 단위 테스트를 작성합니다**
2. **API 테스트는 MockMvc를 사용합니다**
3. **테스트는 독립적이어야 하며 실행 순서에 의존하지 않습니다**

---

## 💾 데이터베이스 가이드라인

1. **Entity 관계 설정 시 FetchType을 명시적으로 정의합니다**
   - 기본값: `FetchType.LAZY`
   - N+1 문제 방지

2. **N+1 문제 방지**
   - 필요시 fetch join을 사용합니다
   - QueryDSL로 최적화된 쿼리 작성

3. **JPA로는 save(), findById(PK) 만 처리하고 모든 쿼리는 queryDSL로 작성한다.**
   - Custom Repository 구현
   - JPAQueryFactory 활용

4. **데이터베이스 마이그레이션**
   - Flyway

---

## 🌐 API 설계 원칙

### RESTful 원칙

1. **HTTP 메서드를 적절히 사용합니다**
   - GET: 조회
   - POST: 생성
   - PUT: 전체 수정
   - PATCH: 부분 수정
   - DELETE: 삭제

2. **API 문서화**
   - Swagger 어노테이션으로 작성합니다
   - `@Tag`, `@Operation`, `@ApiResponse` 사용

3. **에러 응답**
   - `ErrorResponseDTO`를 사용하여 일관성을 유지합니다
   - 적절한 HTTP 상태 코드 반환

---

## 🔧 프로젝트별 설정

### 기본 정보

- **Base Package**: `com.spring.{project_name}` (각 프로젝트명으로 변경 필요)
- **Default Port**: `8080`
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html

### 도메인별 상세 문서

각 도메인의 API 엔드포인트, 비즈니스 로직, 주의사항은 도메인별 문서를 참조하세요.

**도메인 문서 위치**: `.claude/domains/`

**기본 제공 도메인**:
- **[User 도메인](.claude/domains/user.md)**: 사용자 인증, 회원가입, 프로필 관리
- **[Common 도메인](.claude/domains/common.md)**: AWS 통합, 파일 업로드, 이메일 인증, 보안 설정

**새로운 도메인 추가 시**:
1. `.claude/domains/_template.md`를 복사
2. 도메인명으로 파일 생성 (예: `product.md`, `order.md`)
3. 도메인 개요, API 엔드포인트, 비즈니스 로직 작성
4. 이 섹션에 링크 추가

### 환경 변수 (AWS Secrets Manager 사용)

**데이터베이스**
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`

**JWT**
- `JWT_SECRET`

**AWS**
- `AWS_ACCESS_KEY`, `AWS_SECRET_KEY`

**OAuth2**
- Google: `OAUTH2_GOOGLE_CLIENT_ID`, `OAUTH2_GOOGLE_CLIENT_SECRET`
- Kakao: `OAUTH2_KAKAO_CLIENT_ID`, `OAUTH2_KAKAO_CLIENT_SECRET`
- Naver: `OAUTH2_NAVER_CLIENT_ID`, `OAUTH2_NAVER_CLIENT_SECRET`

---

## 🚀 자주 사용하는 Claude 커맨드

이 프로젝트에는 생산성을 높이기 위한 커스텀 커맨드가 준비되어 있습니다:

- `/new-entity` - 새로운 JPA Entity 생성 (Entity + Repository)
- `/new-api` - 새로운 REST API 엔드포인트 생성 (Controller + Service + DTO)
- `/add-exception` - 새로운 Exception 코드 추가

자세한 내용은 `.claude/commands/` 디렉토리를 참고하세요.

---

## 💡 개발 시 유의사항

### Entity 생성 시
- BaseEntity 상속 필수
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)` 사용
- 연관관계는 `FetchType.LAZY` 기본

### API 개발 시
- API 인터페이스와 Controller 구현 분리
- Request DTO에 Validation 어노테이션 추가
- 모든 응답은 `ResponseDTO`로 래핑
- Swagger 문서화 작성

### 예외 처리 시
- 도메인별 `ExceptionCode` enum 정의
- `CommonException` 사용
- HTTP 상태 코드와 에러 메시지 명확히

---

**이 문서는 Claude가 프로젝트를 이해하고 일관된 코드를 생성하기 위한 메모리입니다.**
**팀원들과 함께 이 문서를 유지보수하며 프로젝트 가이드라인을 발전시켜 나가세요.**
