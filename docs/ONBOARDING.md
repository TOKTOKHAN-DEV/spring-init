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
