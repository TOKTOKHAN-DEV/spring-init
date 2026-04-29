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
