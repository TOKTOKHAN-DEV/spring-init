# 온보딩 문서 설계

**작성일**: 2026-04-29
**브랜치**: feat/onboarding-doc
**관련 PR**: TBD

## 1. 배경 및 목적

### 현재 상태
- 이 레포는 **template repository** — 클론하여 새 Spring Boot 프로젝트를 시작하는 용도.
- 기존 `README.md`는 빈약 (placeholder 이름 + 디렉토리 트리 + Swagger URL + CI/CD 이미지).
- 신규 프로젝트 부트스트랩 절차(GitHub Secret, AWS 인프라, SM 값 등)와 신규 개발자 합류 셋업(AWS 프로필, IDE)에 대한 문서가 전무.
- AWS Secrets Manager 기반 CI/CD 전환 PR(#2)이 머지되면 셋업 절차가 더 늘어남 (AWS_PROFILE 환경변수 필수, SM 값 입력 등).

### 목적
- **신규 프로젝트 부트스트래퍼**(테크 리드)가 템플릿 클론부터 첫 배포까지 막힘없이 진행할 수 있는 가이드.
- **신규 합류 개발자**가 30분~1시간 내에 로컬 개발 환경을 띄우고 첫 PR을 만들 수 있게 하는 가이드.
- 코드 컨벤션·아키텍처·배포·트러블슈팅을 한 곳에 모아 매번 같은 질문에 답하지 않게 함.

## 2. 핵심 결정사항

### 2.1 독자
**복수 독자 커버 (C 옵션)** — 한 문서 안에 Part A/B/C/D/E로 분리하여 독자별로 자기 영역만 읽을 수 있도록.

### 2.2 형식
**단일 통합 문서 + GitHub Wiki 미러 (C 옵션)**:
- Source of truth: `docs/ONBOARDING.md` (레포 내, git 추적)
- 미러: GitHub Wiki Home 페이지

이유:
- 단일 파일이라 섹션 간 상호 참조가 자연스럽고 검색이 쉬움.
- Wiki에도 미러링하여 GitHub에서 사이드바 탐색·검색 가능.
- Source가 레포에 있어 클론된 신규 프로젝트도 자동 보유.

### 2.3 언어
한국어 (기존 코드 주석/문서 컨벤션과 일치).

## 3. 파일 구조

```
README.md                              # 슬림화 — 라우팅 허브
docs/
  ONBOARDING.md                        # 신규 — 단일 통합 온보딩 문서
  superpowers/                         # 기존 spec/plan
GitHub Wiki Home                       # docs/ONBOARDING.md 미러
```

### 3.1 Wiki 동기화 전략

`docs/ONBOARDING.md` 변경 시 GitHub Wiki에 자동 push하는 워크플로 추가:

```yaml
# .github/workflows/sync_wiki.yml
name: Sync Wiki
on:
  push:
    branches: [ main ]
    paths: [ 'docs/ONBOARDING.md' ]
  workflow_dispatch:

jobs:
  sync:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout repo
        uses: actions/checkout@v4
      - name: Checkout wiki
        uses: actions/checkout@v4
        with:
          repository: ${{ github.repository }}.wiki
          path: wiki
          token: ${{ secrets.GITHUB_TOKEN }}
      - name: Copy ONBOARDING to wiki Home
        run: cp docs/ONBOARDING.md wiki/Home.md
      - name: Commit & push if changed
        working-directory: wiki
        run: |
          git config user.email "github-actions[bot]@users.noreply.github.com"
          git config user.name "github-actions[bot]"
          git add Home.md
          git diff --cached --quiet || (git commit -m "sync: ONBOARDING.md → Home" && git push)
```

**전제조건**: 레포 Settings > Wiki 활성화. wiki repo가 처음 한 번 생성되어야 함 (콘솔에서 "Create the first page" 1회 클릭).

**대안**: 자동 동기화 워크플로 없이 수동으로 wiki 편집 (구현 단계에서 결정).

## 4. ONBOARDING.md 목차

```markdown
# Onboarding

## 0. 이 문서 사용법
- 새 프로젝트 시작 → Part A
- 진행 중인 프로젝트에 합류 → Part B
- 컨벤션·구조 궁금 → Part C
- 배포·운영 궁금 → Part D
- 에러 만남 → Part E

## 1. 프로젝트 개요
1.1 기술 스택 (Spring Boot 3.3.5, Java 17, Gradle, PostgreSQL, AWS)
1.2 폴더 구조 (요약)
1.3 주요 의존성

---

## Part A. 새 프로젝트 부트스트랩 (1회성)

A.1 GitHub Template으로 레포 생성
    - "Use this template" 버튼
    - 레포 이름 컨벤션 (`<project>-spring`)
A.2 initialize_project 워크플로 자동 트리거 확인
    - 워크플로가 settings.gradle / build.gradle / README.md / application*.yml의
      `{projectName}` placeholder를 레포 이름으로 치환
A.3 GitHub Repository Secrets 등록
    - AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY (CI/CD 파이프라인 전용)
    - 필요한 IAM User 권한 명세
A.4 AWS 인프라 부트스트랩 — 트리거 순서
    A.4.1 build_all_infra 워크플로 (vpc + rds + s3 + ecs 일괄)
          또는 단계별: build_vpc → build_rds → build_s3 → build_ecs
    A.4.2 (선택) build_websocket
    A.4.3 각 단계별 CloudFormation stack 결과 확인 포인트
A.5 AWS Secrets Manager 값 입력
    A.5.1 {project}/{env}/secrets — google-client-id, apple-client-id (콘솔 입력)
    A.5.2 {project}/{env}/jwt — 자동 생성 64자 확인
    A.5.3 {project}/{env}/firebase — 도입 시점에 (Service Account JSON)
    A.5.4 {project}/{env}/db — RDS가 자동 관리, 손대지 않음
A.6 첫 배포 검증
    - GitHub Actions에서 ci_cd 워크플로 성공 확인
    - ECS 콘솔에서 task running 확인
    - ALB DNS로 /health 호출 → 200 OK
A.7 (선택) 도메인 연결, ALB HTTPS Listener 추가, Route53 레코드

## Part B. 개발자 합류 셋업

B.1 사전 준비물
    - JDK 17 (Temurin 권장)
    - Gradle wrapper (./gradlew, 별도 설치 불필요)
    - AWS CLI (자격증명 설정용)
    - IntelliJ IDEA (Lombok 플러그인 필수)
B.2 AWS 프로필 셋업
    B.2.1 ~/.aws/credentials에 프로젝트용 프로필 추가
    B.2.2 IAM User에 필요한 권한 (secretsmanager:GetSecretValue, s3:*, ses:*)
    B.2.3 AWS_PROFILE 환경변수로 프로필 통제 (다중 프로필 충돌 방지)
B.3 IDE Run Configuration (IntelliJ)
    - Spring Boot Run Config 생성
    - Environment Variables: AWS_PROFILE=<프로필명>
    - Active profile: local (기본)
B.4 로컬 실행
    - ./gradlew bootRun
    - http://localhost:8080/health 확인
    - http://localhost:8080/swagger-ui/index.html 확인
B.5 첫 PR 가이드
    - 브랜치 네이밍: feat/<scope>, fix/<scope>, chore/<scope>
    - 커밋 메시지 규칙 (Conventional Commits)
    - PR 템플릿 (Summary / Test plan)

## Part C. 코드 컨벤션 & 아키텍처

C.1 패키지 구조
    - common/: 횡단 관심사 (security, exception, dto, aws, base)
    - domain/: 비즈니스 도메인 (controller / service / repository / entity / dto / exception)
C.2 도메인별 폴더 패턴
    - 신규 도메인 추가 시 폴더 구조 예시 (user, verify 참고)
C.3 예외 처리
    - CommonException + BaseErrorCode enum 패턴
    - @RestControllerAdvice (CommonExceptionHandler)
    - 도메인별 ExceptionCode (UserExceptionCode 등)
C.4 응답 표준 (최근 PR로 통일됨)
    - ResponseDTO<T>, ErrorResponseDTO, FieldErrorResponse
    - 성공/실패 응답 포맷 일관성
C.5 보안 / JWT
    - SecurityConfig 핵심 (인증/인가 경로 분리)
    - TokenProvider, JwtTokenFilter 흐름
    - OAuth2 (Google, Apple, Kakao, Naver)
C.6 AWS 클라이언트
    - DefaultCredentialsProvider 사용 (Task Role on ECS, AWS_PROFILE on local)
    - S3Config, SesConfig, SecretsManagerConfig 역할
C.7 Secrets Manager 사용 패턴
    - spring.config.import: aws-secretsmanager:... (평탄 key-value 자동 매핑)
    - 코드에서 직접 fetch + JSON 파싱 (Firebase 같은 중첩 구조)
C.8 테스트 작성
    - 빠른 단위 테스트 우선
    - 통합 테스트는 별도 프로파일 (옵션)

## Part D. 배포 & 운영

D.1 브랜치 전략
    - main → prod 자동 배포 (push 시)
    - develop → dev 자동 배포 (push 시)
    - feature 브랜치 → PR via develop
D.2 CI/CD 파이프라인 흐름
    - 다이어그램 (./.github/CICD.jpeg) 설명
    - ci_cd.yml 단계별 설명 (CI 빌드 → ECR push → ECS deploy)
D.3 시크릿 관리
    - GitHub Secrets vs AWS Secrets Manager 역할 분리
    - GitHub: AWS 진입권 (CI/CD 전용)
    - SM: 모든 런타임 시크릿
D.4 시크릿 회전 절차
    - JWT 회전 (AWS 콘솔에서 jwt-secret 갱신 → ECS force new deployment, 발급된 토큰 무효화)
    - api-key 회전 (EventBridge x-api-key 동시 갱신 필요)
    - RDS 자동 회전 (선택, AWS Secrets Manager 자동 회전 기능)
D.5 모니터링
    - CloudWatch Logs (/ecs/<project>/<env>)
    - ECS Service events
    - ALB Target Group 헬스체크

## Part E. 트러블슈팅

E.1 로컬 부팅 실패: AWS_PROFILE 미설정
    증상: SecretsManagerException, NoSuchElementException
    해결: export AWS_PROFILE=<프로필명> 또는 IDE Run Config 환경변수
E.2 SM 시크릿 lookup 실패
    증상: ResourceNotFoundException, AccessDeniedException
    해결: 시크릿 이름 오타 확인, IAM 권한 확인
E.3 CI 빌드 실패: application yml 누락
    증상: Failed to load profile / Could not resolve placeholder
    해결: application*.yml이 git에 트래킹되어 있는지 확인 (.gitignore의 negate 규칙)
E.4 ECS task 부팅 실패: SM placeholder 잔존
    증상: Could not resolve placeholder 'jwt-secret'
    해결: SM 콘솔에서 REPLACE_ME 값 갱신 후 force new deployment
E.5 DB 연결 실패
    증상: Connection refused, authentication failed
    해결: RDS 보안그룹 (ECS SG → RDS 5432 허용), SecretTargetAttachment 상태 확인
E.6 EventBridge x-api-key 인증 실패
    증상: 스케줄 트리거가 ALB에서 403
    해결: SM api-key 값과 RuleConnection ApiKeyValue 일치 여부 확인
```

## 5. README.md 재구성

```markdown
# #{PROJECT_NAME}

[Spring Boot / Gradle / Java 뱃지들 — 기존 유지]

Spring Boot 기반 백엔드 템플릿 프로젝트.

## 빠른 링크
- 🚀 [새 프로젝트 시작하기](docs/ONBOARDING.md#part-a-새-프로젝트-부트스트랩-1회성)
- 👋 [개발자 합류 셋업](docs/ONBOARDING.md#part-b-개발자-합류-셋업)
- 📐 [코드 컨벤션 & 아키텍처](docs/ONBOARDING.md#part-c-코드-컨벤션--아키텍처)
- 🚢 [배포 & 운영](docs/ONBOARDING.md#part-d-배포--운영)
- 🩹 [트러블슈팅](docs/ONBOARDING.md#part-e-트러블슈팅)
- 📋 API 문서 (로컬): http://localhost:8080/swagger-ui/index.html

## 🗂️ 디렉토리 구조 (요약)
[기존 트리 그대로]

## ⚙️ CI/CD Pipeline
![CI/CD](./.github/CICD.jpeg)
```

## 6. 의존성 (다른 PR과의 관계)

- **PR #2 (`feat/secrets-manager-cicd`)** 가 머지된 후 본 문서가 의미 있음.
  - Part A.5 (SM 시크릿 입력)은 PR #2의 새 시크릿 구조 기준.
  - Part B.2 (AWS_PROFILE)은 PR #2가 도입한 셋업.
  - Part C.6 (DefaultCredentialsProvider)은 PR #2의 코드 변경 후 정상.
  - Part D.3 (시크릿 관리 모델)은 PR #2 기준.
- 본 문서는 PR #2가 머지된 **이후의 최종 상태**를 기술한다.
- 본 PR의 머지 순서: PR #2 머지 → 본 PR rebase → 본 PR 머지.

## 7. 변경 범위 요약

| 파일 | 변경 |
|---|---|
| `README.md` | 슬림화 (라우팅 허브) |
| `docs/ONBOARDING.md` | **신규** — 5 Part 통합 문서 (Part A~E) |
| `.github/workflows/sync_wiki.yml` | **신규** — Wiki 자동 동기화 워크플로 |

## 8. 본 작업 범위에서 제외된 항목

- PR 템플릿 (`.github/pull_request_template.md`)·이슈 템플릿 신설 — Part B.5에서 언급만, 별도 작업.
- CONTRIBUTING.md, CODE_OF_CONDUCT.md 등 기타 메타 문서.
- 영어 번역.
- 코드 컨벤션 자동화 (linter, formatter 설정) — 별도 작업.
- 시크릿 회전 자동화 스크립트 — Part D.4는 운영 절차만 기술.

## 9. 결정 기록 (Q&A 요약)

| # | 질문 | 결정 | 근거 |
|---|---|---|---|
| 1 | 독자 범위 | 부트스트래퍼 + 신규 개발자 둘 다 | 단일 SoT, 독자별 Part 분리로 navigability 유지 |
| 2 | 문서 형식 | 단일 `docs/ONBOARDING.md` + GitHub Wiki 미러 | 검색·상호 참조 용이, 클론 시 자동 동반 |
