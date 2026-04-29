# Onboarding

이 문서는 spring-init 템플릿으로 시작한 프로젝트의 부트스트랩, 개발자 합류, 컨벤션, 배포, 트러블슈팅을 다룬다.

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
| Migration | Flyway |
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
- **JPA + QueryDSL**: 동적 쿼리는 QueryDSL, 정적 쿼리는 Spring Data JPA
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
