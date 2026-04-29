# Onboarding

이 문서는 spring-init 템플릿으로 시작한 프로젝트의 부트스트랩, 개발자 합류, 컨벤션, 배포, 트러블슈팅을 다룬다.

> ⚠️ **상태 안내**: 본 문서는 [PR #2 (`feat/secrets-manager-cicd`)](https://github.com/TOKTOKHAN-DEV/spring-init/pull/2) 가 머지된 **이후 최종 상태**를 기준으로 작성되었다. PR #2 머지 전엔 다음 항목이 현재 main과 다르다:
> - **시크릿 구조**: 현재 main은 단일 `<project>/<env>/spring` (api-key 한 키만) + RDS의 `<project>/<env>/db`. PR #2 머지 후 `<project>/<env>/secrets` / `<project>/<env>/jwt` / `<project>/<env>/firebase` 분리.
> - **CI/CD**: 현재 main은 `APPLICATION_DEV_YML` GitHub Secret으로 yml을 CI에서 주입. PR #2 머지 후 yml이 git에 직접 커밋되고 SM에서 시크릿 로딩.
> - **AWS 인증**: 현재 main의 `S3Config`/`SesConfig`는 정적 키. PR #2 머지 후 `DefaultCredentialsProvider`(Task Role).
> - **Spring 프로파일**: 현재 main에 `application-{dev,prod,local}.yml`이 git에 없음. PR #2 머지 후 추가.
>
> 본 PR은 PR #2 머지 후에 rebase·머지하는 것을 전제로 한다. 그 전까지는 Part A.5 / D.3 / E.3-E.4 등이 현재 main과 불일치할 수 있다.

## 0. 이 문서 사용법

- **새 프로젝트 시작** (테크 리드, 1회성) → [Part A. 새 프로젝트 부트스트랩](#part-a-새-프로젝트-부트스트랩-1회성)
- **진행 중인 프로젝트에 합류** (신규 개발자) → [Part B. 개발자 합류 셋업](#part-b-개발자-합류-셋업)
- **코드 컨벤션·구조 궁금** → [Part C. 코드 컨벤션 & 아키텍처](#part-c-코드-컨벤션--아키텍처)
- **배포·운영 궁금** → [Part D. 배포 & 운영](#part-d-배포--운영)
- **에러 만남** → [Part E. 트러블슈팅](#part-e-트러블슈팅)

문서가 길어질 수 있으니 자기 영역만 읽어도 된다.

## 1. 프로젝트 개요

### 1.1 기술 스택

| 영역 | 기술 |
|---|---|
| Framework | Spring Boot 3.3.5 |
| Language | Java 17 |
| Build | Gradle 8.10 (wrapper) |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA + QueryDSL 5.0 |
| Migration | Flyway (PR #2 yml에 활성, 의존성은 별도 추가 필요) |
| API Docs | springdoc-openapi 2.5 (Swagger UI) |
| Auth | Spring Security + JWT (jjwt 0.11) |
| Cloud | AWS (ECS Fargate, ALB, RDS, S3, SES, Secrets Manager, EventBridge) |
| IaC | CloudFormation |
| CI/CD | GitHub Actions |

### 1.2 폴더 구조 (요약)

```
.
├── .github/
│   ├── cloudformation/         # 인프라 IaC (vpc, rds, s3, ecs, websocket)
│   └── workflows/              # GitHub Actions
├── docs/                       # 문서 (이 문서 포함)
├── src/main/
│   ├── java/com/spring/<projectName>/
│   │   ├── common/             # 횡단 관심사 (security, exception, dto, aws, base)
│   │   └── <domain>/           # 도메인 (controller / service / repository / entity / dto / exception)
│   └── resources/
│       ├── application.yml             # 공통 (profiles.active: local)
│       ├── application-local.yml       # 로컬 (dev SM 사용)
│       ├── application-dev.yml         # dev (SM dev import)
│       └── application-prod.yml        # prod (SM prod import)
├── build.gradle
└── README.md
```

### 1.3 주요 의존성

- **AWS SDK v2**: `software.amazon.awssdk:s3`, `secretsmanager`, `ses`
- **Spring Cloud AWS**: `io.awspring.cloud:spring-cloud-aws-starter-secrets-manager` (Spring `spring.config.import`로 SM 직접 로딩)
- **JPA + QueryDSL**: 동적 쿼리는 QueryDSL, 정적 쿼리는 Spring Data JPA. (build.gradle에 MyBatis가 함께 포함되어 있으나 활성 ORM은 JPA + QueryDSL.)
- **Lombok**: 반드시 IDE 플러그인 설치 (없으면 컴파일 에러)

---

## Part A. 새 프로젝트 부트스트랩 (1회성)

신규 프로젝트를 spring-init 템플릿으로 시작할 때 1회 수행하는 셋업이다. 처음부터 끝까지 순서대로 진행한다.

### A.1 GitHub Template으로 레포 생성

1. https://github.com/TOKTOKHAN-DEV/spring-init 페이지 → **Use this template** → **Create a new repository**
2. 레포 이름 컨벤션: `<프로젝트명>-spring` (예: `awesome-spring`). CI 스크립트가 `-spring` suffix를 떼서 ProjectName으로 사용한다.
3. Owner는 보통 `TOKTOKHAN-DEV`. Visibility는 Private 권장.

### A.2 initialize_project 워크플로 자동 트리거 확인

레포 생성 직후 main 브랜치에서 `Initialize Project` 워크플로가 자동 실행된다 (`on: create: branches: main`).

이 워크플로는:
- `settings.gradle` / `build.gradle`의 `spring_init` → `<레포이름>` 치환
- `README.md`의 `#{PROJECT_NAME}` → `<레포이름>` 치환
- `src/main/resources/application.yml` 및 `application-{local,dev,prod}.yml`의 `{projectName}` → `<레포이름>` 치환
- `initial commit` 메시지로 푸시

GitHub **Actions** 탭에서 `Initialize Project` 워크플로가 ✅ 성공한 것을 확인.

> 이 시점부터 메인 CI/CD(`ci_cd.yml`)는 commit message에 `initial commit` 문자열이 있으면 skip된다 (`if: !contains(...'initial commit')`). 다음 push부터 정상 실행.

### A.3 GitHub Repository Secrets 등록

레포 **Settings → Secrets and variables → Actions → New repository secret**

| 키 | 값 |
|---|---|
| `AWS_ACCESS_KEY_ID` | 인프라 부트스트랩 + 배포 권한 IAM User의 access key |
| `AWS_SECRET_ACCESS_KEY` | 동일 IAM User의 secret key |

이 IAM User에게 부여할 정책 (인라인 또는 관리형 결합):
- `AmazonEC2FullAccess` (VPC, SG)
- `AmazonRDSFullAccess`
- `AmazonS3FullAccess`
- `AmazonECS_FullAccess`, `AmazonEC2ContainerRegistryFullAccess`
- `IAMFullAccess` (CloudFormation이 IAM Role 생성)
- `CloudFormationFullAccess`
- `SecretsManagerReadWrite`
- `AmazonEventBridgeFullAccess`

> CI/CD 파이프라인 전용 키. 운영 후엔 다른 시크릿은 절대 GitHub Secrets에 두지 않는다.

### A.4 AWS 인프라 부트스트랩

**일괄 트리거** (권장):
1. **Actions → Build All Infra → Run workflow**
2. Environment 선택: `dev` 먼저 → 검증 후 `prod`
3. 워크플로가 다음 순서로 실행: `set_environment` → `create_repository` → `deploy_backend_image` → `build_vpc` → (`build_ecs` & `build_rds` & `build_s3` 병렬) → (선택) `build_websocket`

각 CloudFormation stack 결과 확인:
- AWS Console → CloudFormation → 다음 stack 모두 `CREATE_COMPLETE`
  - `<project>-<env>-vpc`
  - `<project>-<env>-rds`
  - `<project>-<env>-s3`
  - `<project>-<env>-ecs`

**단계별 트리거** (문제 발생 시 디버깅용):
- `Build VPC`, `Build RDS`, `Build S3`, `Build ECS`, `Build Websocket` 워크플로를 개별 실행

### A.5 AWS Secrets Manager 값 입력

`build_ecs` 완료 시 다음 시크릿이 자동 생성된다:

| 시크릿 | 키 | 자동/수동 |
|---|---|---|
| `<project>/<env>/db` | host/port/dbname/username/password | RDS attached, 자동 |
| `<project>/<env>/secrets` | api-key | 자동 (50자) |
| `<project>/<env>/secrets` | google-client-id, apple-client-id | **수동 입력 필요** |
| `<project>/<env>/jwt` | jwt-secret | 자동 (64자) |
| `<project>/<env>/firebase` | (Service Account JSON 전체) | **수동 입력 필요** (Firebase 도입 시) |

**입력 절차:**
1. AWS Console → Secrets Manager → `<project>/<env>/secrets` 열기 → **Retrieve secret value** → **Edit**
2. `google-client-id`: Google Cloud Console → APIs & Services → Credentials에서 OAuth 2.0 Client ID 복사
3. `apple-client-id`: Apple Developer → Certificates, IDs & Profiles → Identifiers에서 Service ID 복사
4. Save

`<project>/<env>/firebase`는 Firebase 코드 도입 전까진 placeholder 그대로 두어도 됨.

### A.6 첫 배포 검증

A.4에서 `build_all_infra`로 이미 ECR push + ECS 배포까지 완료되어 있을 가능성 큼. 그렇지 않다면:

1. 임의 commit을 develop 브랜치에 push (예: README 한 줄 수정)
2. **Actions → CI/CD** 워크플로 ✅ 성공 확인
3. AWS Console → ECS → `<project>-<env>-ecs-cluster` → `web` 서비스 → Tasks → 1개 RUNNING 확인
4. ALB DNS 확인: AWS Console → EC2 → Load Balancers → `<project>-<env>-alb` 의 DNS name 복사
5. `curl http://<alb-dns>/health` → `200 OK` 응답
6. CloudWatch Logs → `/ecs/<project>/<env>` log group → 부팅 로그에 `Started SpringInitApplication` 라인 확인

> 📚 **Wiki 활성화 (1회)**: 레포 Settings → General → Features → Wiki 체크박스 활성화. 그 후 Wiki 탭에서 'Create the first page'를 1회 클릭 (내용은 임시로 둠). 이후 main 브랜치에 docs/ONBOARDING.md가 변경되면 `Sync Wiki` 워크플로가 자동으로 Wiki Home을 갱신한다.

### A.7 (선택) 도메인 + HTTPS 셋업

운영용으로 사용한다면:
1. Route53에 도메인 등록 또는 외부 도메인 NS 위임
2. ACM에서 SSL 인증서 발급
3. ALB Listener에 HTTPS(443) 추가 + ACM 인증서 연결
4. Route53 A 레코드 (Alias) → ALB
5. 백엔드 코드의 `cors.allowed-origins`에 도메인 추가

---

## Part B. 개발자 합류 셋업

기존 프로젝트에 신규 멤버로 합류했을 때, 30분~1시간 내에 로컬 빌드/실행이 되도록 한다.

### B.1 사전 준비물

- **JDK 17** (Temurin 권장)
  - macOS: `brew install --cask temurin@17`
  - 다른 JDK가 default라면 `~/.zshrc`에 `export JAVA_HOME=$(/usr/libexec/java_home -v 17)` 추가
- **Gradle**: 별도 설치 불필요. 레포의 `./gradlew` 사용
- **AWS CLI** v2
  - macOS: `brew install awscli`
- **IntelliJ IDEA** (Community 또는 Ultimate)
  - 필수 플러그인: **Lombok** (IntelliJ Settings → Plugins에서 검색·설치)
  - 권장 플러그인: **Spring Boot**, **AWS Toolkit**
- (선택) **Docker** — 로컬에서 PostgreSQL 따로 띄울 경우

### B.2 AWS 프로필 셋업

이 프로젝트는 로컬에서도 dev 환경의 AWS Secrets Manager에 접근하여 시크릿을 읽는다. 따라서 AWS 자격증명이 필요하다.

#### B.2.1 IAM User 자격증명 받기

테크 리드에게 본인용 IAM User access key를 요청한다. (개인별로 발급, 공용 키 공유 금지). 권한:
- `secretsmanager:GetSecretValue`, `DescribeSecret` — `<project>/dev/*` 시크릿
- `s3:*` — `<project>-dev-bucket` (presigned URL 검증·업로드 테스트)
- `ses:SendEmail`, `SendRawEmail` (이메일 발송 테스트)

#### B.2.2 ~/.aws/credentials에 프로필 추가

```bash
mkdir -p ~/.aws
$EDITOR ~/.aws/credentials
```

다음 형식으로 추가 (이미 있는 다른 프로필은 그대로 둔다):

```ini
[<프로젝트명>-dev]
aws_access_key_id     = AKIA...
aws_secret_access_key = ....
```

`~/.aws/config`에 region 지정:

```ini
[profile <프로젝트명>-dev]
region = ap-northeast-2
```

#### B.2.3 AWS_PROFILE 환경변수로 프로필 선택

PC에 다른 회사·다른 프로젝트의 AWS 자격증명이 같이 있으면, 환경변수 `AWS_PROFILE`로 어느 프로필을 쓸지 통제한다.

```bash
# 셸 직접 실행
export AWS_PROFILE=<프로젝트명>-dev
./gradlew bootRun
```

또는 `direnv` 사용자는 프로젝트 루트에 `.envrc`:
```bash
export AWS_PROFILE=<프로젝트명>-dev
```
(주의: `.envrc`는 git에 커밋하지 않는다. `.gitignore`에 추가.)

> 환경변수가 없으면 `~/.aws/credentials`의 `[default]` 프로필이 쓰여 다른 회사 자격증명으로 SM 호출 → 권한 부족 또는 잘못된 시크릿 호출 발생.

### B.3 IDE Run Configuration (IntelliJ)

1. **Run → Edit Configurations → + → Spring Boot**
2. 설정:
   - Name: `SpringInit (local)`
   - Main class: `com.spring.<projectName>.SpringInitApplication`
   - Active profiles: `local` (기본값이지만 명시 권장)
   - **Environment variables**: `AWS_PROFILE=<프로젝트명>-dev`
3. Apply → OK

### B.4 로컬 실행

#### B.4.1 빌드

```bash
./gradlew clean build -x test
```
Expected: `BUILD SUCCESSFUL`. (테스트는 SM 접근이 필요해 -x test로 일단 제외)

#### B.4.2 실행

IDE Run Config로 실행하거나:
```bash
AWS_PROFILE=<프로젝트명>-dev ./gradlew bootRun
```

부팅 로그에 다음 라인 확인:
- `Loaded profile group 'local'`
- `Loading config data from aws-secretsmanager:<project>/dev/db`
- `Loading config data from aws-secretsmanager:<project>/dev/secrets`
- `Loading config data from aws-secretsmanager:<project>/dev/jwt`
- `Started SpringInitApplication in X.XX seconds`

#### B.4.3 동작 확인

- 헬스체크: `curl http://localhost:8080/health`
- Swagger UI: 브라우저에서 http://localhost:8080/swagger-ui/index.html
- 임의 GET API 호출 → DB 응답 확인

### B.5 첫 PR 가이드

#### 브랜치 네이밍

| 유형 | 패턴 |
|---|---|
| 기능 추가 | `feat/<scope>` (예: `feat/user-profile`) |
| 버그 수정 | `fix/<scope>` (예: `fix/jwt-expiry`) |
| 잡일 (CI, 문서, 의존성) | `chore/<scope>` |
| 리팩토링 | `refactor/<scope>` |

#### 커밋 메시지 (Conventional Commits)

```
<type>(<scope>): <subject>

<body>
```

예:
```
feat(user): add password change endpoint

새 비밀번호 정책 (8자 이상, 영문+숫자) 검증 포함.
PasswordChangeRequestDto 추가.
```

#### PR 템플릿

PR 본문 권장 형식:
```markdown
## Summary
- 무엇을 / 왜 변경했는지 1~3줄

## Test plan
- [ ] 빌드 성공
- [ ] 단위 테스트 추가/통과
- [ ] 로컬에서 시나리오 X 확인
```

머지 타겟: `develop` (자동 dev 배포). prod 반영은 별도 develop → main PR.

---

## Part C. 코드 컨벤션 & 아키텍처

### C.1 패키지 구조

```
com.spring.<projectName>/
├── SpringInitApplication.java        # 메인 클래스
├── common/                           # 횡단 관심사
│   ├── apidocs/                      # SwaggerConfig + ApiException 매핑
│   ├── aws/                          # S3Config, SesConfig, SecretsManagerConfig
│   ├── base/                         # BaseEntity, BaseErrorCode
│   ├── dto/                          # ResponseDTO, ErrorResponseDTO, PageResponseDTO, Cursor
│   ├── exception/                    # CommonException, CommonExceptionHandler
│   ├── healtcheck/                   # /health 엔드포인트
│   ├── persistence/config/           # JpaConfig, QueryConfig
│   ├── security/                     # SecurityConfig, JwtTokenFilter, TokenProvider
│   └── utils/                        # ObjectUtils 등
├── user/                             # 도메인 (예시)
│   ├── controller/                   # UserApi (interface) + UserController
│   ├── service/                      # UserService
│   ├── repository/                   # UserRepository (인터페이스), UserJpaRepository, UserRepositoryImpl
│   ├── entity/                       # User, UserRole
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   └── exception/                    # UserExceptionCode
├── verify/                           # 도메인 (예시)
└── oauth/                            # OAuth2 처리 (Google/Apple/Kakao/Naver)
```

### C.2 도메인별 폴더 패턴

신규 도메인 추가 시 `user/`, `verify/` 패턴을 따른다:

```
<도메인>/
├── controller/
│   ├── <Domain>Api.java             # @RestController interface (Swagger 어노테이션 위치)
│   └── <Domain>Controller.java      # implements <Domain>Api (실제 구현)
├── service/
│   └── <Domain>Service.java
├── repository/
│   ├── <Domain>Repository.java       # 도메인 레벨 추상 인터페이스
│   ├── <Domain>JpaRepository.java   # extends JpaRepository
│   └── <Domain>RepositoryImpl.java  # QueryDSL 동적 쿼리
├── entity/
│   └── <Domain>.java                # extends BaseEntity
├── dto/
│   ├── request/
│   └── response/
└── exception/
    └── <Domain>ExceptionCode.java   # implements BaseErrorCode
```

### C.3 예외 처리 패턴

#### CommonException + BaseErrorCode

`com.spring.<projectName>.common.exception.CommonException`이 모든 비즈니스 예외의 기반.
`com.spring.<projectName>.common.base.BaseErrorCode`는 모든 ErrorCode enum이 구현하는 인터페이스:

```java
public interface BaseErrorCode {
    String getCode();
    String getMessage();
    HttpStatus getHttpStatus();
}
```

#### 도메인별 ExceptionCode

각 도메인은 자체 ExceptionCode enum을 정의 (예: `UserExceptionCode`, `EmailVerifyExceptionCode`):

```java
@Getter
public enum UserExceptionCode implements BaseErrorCode {
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "NOT_FOUND_USER", "회원을 찾을 수 없습니다");

    private final HttpStatus httpStatusCode;
    private final String code;
    private final String message;
    // ...
}
```

#### 예외 던지기

서비스 레이어에서:
```java
throw new CommonException(UserExceptionCode.NOT_FOUND_USER.getCode(),
                          UserExceptionCode.NOT_FOUND_USER.getMessage());
```

#### 전역 핸들러

`CommonExceptionHandler` (`@RestControllerAdvice`)가 모든 `CommonException`을 잡아 `ErrorResponseDTO`로 변환.

### C.4 응답 표준

#### 성공 응답

```java
ResponseDTO<UserInfoResponseDto> response = ResponseDTO.<UserInfoResponseDto>builder()
    .statusCode(200)
    .message("성공")
    .data(userInfo)
    .build();
return ResponseEntity.ok(response);
```

`ResponseDTO<T>` 필드:
- `int statusCode`
- `String message`
- `T data`

#### 실패 응답 (자동 처리됨)

`CommonException` 던지면 `CommonExceptionHandler`가 자동으로:

```json
{
  "errorCode": "NOT_FOUND_USER",
  "message": "사용자를 찾을 수 없습니다",
  "fieldErrors": null
}
```

`@Valid` 검증 실패 시 `fieldErrors`에 필드별 에러 채워짐.

### C.5 보안 / JWT

`com.spring.<projectName>.common.security.config.SecurityConfig`:
- 인증 불요 경로: `/health`, `/swagger-ui/**`, `/v3/api-docs/**`, `/v1/auth/**`
- 그 외 경로는 JWT 필수
- `/v1/internal/**`은 x-api-key 헤더 검증 (EventBridge 같은 내부 호출용)
- `/v1/admin/**`은 ADMIN 역할 필수

`com.spring.<projectName>.common.security.jwt.TokenProvider`가 토큰 발급·검증.
`JwtTokenFilter`가 매 요청마다 Authorization 헤더 검사.

만료 시간:
- Access Token: yml `jwt.token-validity-in-milliseconds` (TokenProvider 참조)
- Refresh Token: 90일

OAuth2 (Google/Apple/Kakao/Naver)는 `oauth/` 패키지에서 처리. `SocialAuthRequestDto`로 클라이언트가 social provider id token을 보내면 백엔드가 검증·로컬 사용자와 매핑.

### C.6 AWS 클라이언트

모든 AWS SDK 클라이언트는 `DefaultCredentialsProvider`를 사용한다 (정적 키 미사용).

자격증명 해석 순서:
1. **Local**: `~/.aws/credentials`의 `AWS_PROFILE` 환경변수 프로필
2. **ECS Fargate**: Task Role의 임시 자격증명 (자동)

해당 빈 위치:
- `common/aws/S3Config.java` — `S3Client`, `S3Presigner` 빈
- `common/aws/SesConfig.java` — `SesClient` 빈
- `common/aws/SecretsManagerConfig.java` — 코드에서 직접 SM fetch 시 사용

### C.7 Secrets Manager 사용 패턴

#### 패턴 1: spring.config.import (권장)

평탄한 key-value 시크릿은 `application-{env}.yml`의 `spring.config.import`로 자동 로딩:

```yaml
spring:
  config:
    import:
      - aws-secretsmanager:<project>/<env>/db
      - aws-secretsmanager:<project>/<env>/secrets
      - aws-secretsmanager:<project>/<env>/jwt
```

코드에서는 `@Value`로 사용:
```java
@Value("${jwt-secret}")
private String jwtSecret;
```

#### 패턴 2: 코드에서 직접 fetch (중첩 JSON, 동적 로딩)

Firebase Service Account 같이 중첩 구조는 `SecretsManagerConfig.getSecret()` + `ObjectMapper` 파싱:

```java
@Service
@RequiredArgsConstructor
public class FirebaseClient {
    private final SecretsManagerConfig secretsManagerConfig;
    private final ObjectMapper objectMapper;

    @Value("${app.env}")
    private String env;

    @Value("${spring.application.name}")
    private String projectName;

    public FirebaseConfig loadConfig() throws Exception {
        String json = secretsManagerConfig.getSecret(projectName + "/" + env + "/firebase");
        return objectMapper.readValue(json, FirebaseConfig.class);
    }
}
```

### C.8 테스트 작성

- 단위 테스트: 서비스 로직, 유틸 함수 (Mock 활용)
- Repository 테스트: `@DataJpaTest` + Testcontainers 권장 (도입 예정)
- Controller 테스트: `MockMvc` + `@WebMvcTest`
- 통합 테스트: `@SpringBootTest`는 SM 접근 필요, 주의해서 사용 (또는 `@MockBean`)

테스트 실행:
```bash
./gradlew test
```

CI는 `./gradlew build -x test`로 테스트를 빌드 단계에서 제외. 로컬에서 별도 실행.

---

## Part D. 배포 & 운영

### D.1 브랜치 전략

| 브랜치 | 환경 | 트리거 |
|---|---|---|
| `main` | prod | push 시 자동 ci/cd |
| `develop` | dev | push 시 자동 ci/cd |
| `feat/*`, `fix/*` 등 | (배포 없음) | PR via develop |

작업 흐름:
1. `develop`에서 `feat/<scope>` 브랜치 생성
2. PR을 develop으로 → 리뷰 → squash 또는 merge → dev 자동 배포
3. dev 검증 후 develop → main PR → prod 자동 배포

### D.2 CI/CD 파이프라인

`.github/workflows/ci_cd.yml` (트리거: push to main/develop)

```
[ continuous_integration ]
  Set Environments (PROJECT_NAME, PROJECT_ENV)
    ↓
  Checkout
    ↓
  Set Up JDK 17
    ↓
  Build with Gradle (-x test)
    ↓
  Configure AWS credentials (GitHub Secret)
    ↓
  Login to Amazon ECR
    ↓
  Docker Buildx
    ↓
  Build & Push Backend Image (ECR push)

[ continuous_deployment ]  ← needs: continuous_integration
  Configure AWS credentials
    ↓
  Login to Amazon ECR
    ↓
  Download Web TaskDefinition
    ↓
  Render Web TaskDefinition (새 이미지로 갈아끼움)
    ↓
  Deploy Web Service (ECS rolling update)
```

상세 다이어그램: `.github/CICD.jpeg`

`continuous_integration` job의 첫 줄:
```yaml
if: ${{ !contains(github.event.head_commit.message, 'initial commit') }}
```
이 조건 때문에 `Initialize Project` 워크플로의 `initial commit` 푸시는 CI/CD가 건너뜀.

### D.3 시크릿 관리 모델

| 위치 | 보유 시크릿 | 접근 주체 |
|---|---|---|
| **GitHub Repository Secrets** | `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` (CI/CD 파이프라인 전용) | GitHub Actions 러너 |
| **AWS Secrets Manager** | DB, JWT, OAuth, Firebase, api-key | ECS Task Role (런타임), 개발자 IAM User (로컬) |

원칙:
- **GitHub Secrets는 AWS 진입권만 보유**. 다른 시크릿 절대 두지 않음.
- **런타임 컨테이너에 정적 키 없음**. ECS Task Role의 임시 자격증명 사용.
- **yml 파일에 시크릿 0개**. 모두 `${...}` placeholder.

### D.4 시크릿 회전 절차

#### JWT secret 회전

영향: 발급된 모든 access/refresh 토큰 무효화 → 모든 사용자 재로그인.

1. AWS Console → Secrets Manager → `<project>/<env>/jwt` → **Retrieve secret value** → **Edit**
2. `jwt-secret` 값을 새 64자 랜덤 문자열로 교체 (`openssl rand -base64 64 | tr -d '\n='` 권장)
3. ECS Service → **Force new deployment**
4. 새 task가 부팅하며 새 jwt-secret으로 토큰 검증
5. 사용자 안내: 재로그인 필요

#### api-key 회전

영향: EventBridge x-api-key 인증 끊김.

1. SM에서 `<project>/<env>/secrets`의 `api-key` 갱신
2. CloudFormation `RuleConnection`이 dynamic reference이므로 `aws cloudformation update-stack`을 한 번 돌려 EventBridge Connection을 새 값으로 갱신
3. ECS Service → Force new deployment

#### RDS password 회전

`{project}/{env}/db` 시크릿이 RDS와 attached 상태이므로 AWS Secrets Manager **Rotation** 기능 활성화 가능 (Lambda 자동 회전). 도입 시 별도 작업.

### D.5 모니터링

#### CloudWatch Logs

Log Group: `/ecs/<project>/<env>`

각 task의 stdout/stderr가 모임. Spring 부팅 로그, 비즈니스 로그(SLF4J) 모두 여기.

검색 예:
- 부팅 성공: `Started SpringInitApplication`
- 에러: `ERROR` 또는 `Exception`
- SM 로딩: `Loading config data from aws-secretsmanager`

#### ECS Service Events

ECS Console → Cluster → Service → Events 탭. 배포 실패, 헬스체크 실패 시 여기에 사유가 남는다.

#### ALB Target Group Health

EC2 Console → Target Groups → `<project>-<env>-tg-8080`. Healthy/Unhealthy 카운트 + 사유.

---

## Part E. 트러블슈팅

### E.1 로컬 부팅 실패: AWS_PROFILE 미설정

**증상:**
```
software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException:
The security token included in the request is invalid
```
또는
```
Could not resolve placeholder 'jwt-secret' in value "${jwt-secret}"
```

**원인:** `AWS_PROFILE` 환경변수가 없거나, `~/.aws/credentials`의 `[default]` 프로필이 다른 회사 자격증명.

**해결:**
- 셸: `export AWS_PROFILE=<프로젝트명>-dev`
- IDE: Run Configuration → Environment variables → `AWS_PROFILE=<프로젝트명>-dev`

### E.2 SM 시크릿 lookup 실패

**증상:**
```
ResourceNotFoundException: Secrets Manager can't find the specified secret.
```

**원인:**
- 시크릿 이름 오타 (`<project>/<env>/secrets`가 정확한지)
- 해당 환경 인프라가 아직 부트스트랩 안 됨 → A.4 참조
- IAM 권한 부족

**해결:**
1. AWS Console → Secrets Manager에서 시크릿 존재 확인
2. `application-<env>.yml`의 `spring.config.import` 경로와 실제 시크릿 이름 비교
3. IAM User/Role에 `secretsmanager:GetSecretValue` 권한 확인

**증상:**
```
AccessDeniedException: User: ... is not authorized to perform: secretsmanager:GetSecretValue
```

**해결:** 로컬이면 IAM User 정책 보강. ECS면 `cloudformation/ecs.yml`의 `TaskRole`이 `secretsmanager:*` 권한 있는지 확인.

### E.3 CI 빌드 실패: application yml 누락

**증상:**
```
Could not resolve placeholder 'host' in value "jdbc:postgresql://${host}:${port}/${dbname}"
```
빌드는 되지만 부팅이 안 됨.

**원인:** `application*.yml` 파일이 git에 트래킹되지 않아 CI 체크아웃에 누락.

**해결:** `.gitignore` 9행 부근의 negate 규칙 확인:
```
*.yml
!**/src/main/resources/application*.yml
```

`git ls-files src/main/resources/`에 `application.yml`, `application-dev.yml`, `application-prod.yml`, `application-local.yml` 모두 보여야 한다.

### E.4 ECS task 부팅 실패: SM placeholder 잔존

**증상 (CloudWatch):**
```
Could not resolve placeholder 'google-client-id' in value "${google-client-id}"
```
또는 `REPLACE_ME` 값이 코드에 그대로 들어가 OAuth 동작 안 함.

**원인:** CloudFormation이 시크릿을 만들 때 placeholder(REPLACE_ME)로 시작했고, 운영자가 실제 값을 입력하지 않음.

**해결:**
1. AWS Console → Secrets Manager → `<project>/<env>/secrets` → 값 확인
2. `REPLACE_ME` 들어 있으면 실제 값으로 갱신
3. ECS Service → Force new deployment

### E.5 DB 연결 실패

**증상:**
```
org.postgresql.util.PSQLException: Connection refused
```
또는
```
FATAL: password authentication failed for user "..."
```

**원인:**
- RDS 보안그룹이 ECS 보안그룹의 5432 inbound 막음
- `<project>/<env>/db` 시크릿이 RDS와 attached 상태가 풀려 password 불일치
- RDS 인스턴스가 정지/삭제된 상태

**해결:**
1. AWS Console → RDS → 인스턴스 상태 `Available` 확인
2. RDS Configuration → Security Group inbound: ECS SG에서 5432 허용
3. Secrets Manager → `<project>/<env>/db` → **Versions** 탭에서 SecretTargetAttachment에 의한 회전 이력 확인. 끊겼으면 RDS 콘솔에서 password 재설정 + SM 동기화.

### E.6 EventBridge x-api-key 인증 실패

**증상:** 스케줄 트리거가 ALB에 도달하나 백엔드가 401 또는 403 반환.

**원인:** `<project>/<env>/secrets`의 `api-key` 값과 EventBridge `RuleConnection`의 `ApiKeyValue`가 불일치.

**해결:**
1. SM 콘솔에서 `api-key` 값 확인
2. EventBridge Console → Connections → `<project>-<env>-connection` → 인증 정보 확인
3. CloudFormation update-stack 1회 돌려 dynamic reference 갱신

### E.7 Lombok 관련 컴파일 에러

**증상:** IDE에서 `Cannot find symbol: method getXxx()` 같은 에러.

**원인:** IntelliJ Lombok 플러그인 미설치 또는 Annotation Processing 비활성화.

**해결:**
1. IntelliJ Settings → Plugins → "Lombok" 설치 + 재시작
2. Settings → Build → Compiler → Annotation Processors → **Enable annotation processing** 체크

### E.8 Initialize Project 워크플로 실패

**증상:** 레포 생성 직후 `Initialize Project` 워크플로가 ❌ 실패.

**원인:** GitHub Actions의 default `GITHUB_TOKEN` 권한이 부족하여 push 안 됨 (드물게).

**해결:** Settings → Actions → General → Workflow permissions → **Read and write permissions** 체크 → 워크플로 재실행.
