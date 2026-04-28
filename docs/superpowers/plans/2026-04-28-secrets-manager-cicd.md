# Secrets Manager 기반 CI/CD 시크릿 관리 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** GitHub Secrets에서 AWS 진입권만 보유하고, 그 외 모든 시크릿을 AWS Secrets Manager에서 런타임 로딩하도록 Spring Boot 애플리케이션 + CI/CD 파이프라인 + CloudFormation 인프라를 일괄 전환한다.

**Architecture:** ECS Task IAM Role의 임시 자격증명으로 SM 접근. yml은 git에 placeholder만 있는 파일로 커밋하고, 런타임에 `spring.config.import: aws-secretsmanager:...`로 값 주입. AWS SDK 클라이언트는 `DefaultCredentialsProvider` 사용.

**Tech Stack:** Spring Boot 3.3, Spring Cloud AWS Secrets Manager 3.1, AWS SDK v2, AWS CloudFormation, GitHub Actions.

**관련 문서:** `docs/superpowers/specs/2026-04-28-secrets-manager-cicd-design.md`

**전체 작업은 단일 PR**로 머지된다. 본 계획은 그 PR을 구성하는 코드/설정 변경을 다룬다. 인프라 update, SM 값 입력, ECS 배포는 머지 전후의 운영 절차(스펙 §9)로 처리한다.

---

## File Structure

| 파일 | 변경 유형 | 책임 |
|---|---|---|
| `src/main/resources/application-{env}.yml` | 삭제 | (제거) 기존 템플릿형 자리표시 파일 |
| `src/main/resources/application-dev.yml` | 신규 | dev 환경 Spring 설정 (SM dev import) |
| `src/main/resources/application-prod.yml` | 신규 | prod 환경 Spring 설정 (SM prod import) |
| `src/main/resources/application-local.yml` | 신규 | 로컬 환경 Spring 설정 (SM dev import + AWS_PROFILE 사용) |
| `src/main/java/com/spring/spring_init/common/aws/S3Config.java` | 수정 | S3Client/S3Presigner를 DefaultCredentialsProvider로 전환 |
| `src/main/java/com/spring/spring_init/common/aws/SesConfig.java` | 수정 | SesClient를 DefaultCredentialsProvider로 전환 |
| `.github/cloudformation/ecs.yml` | 수정 | 기존 `Secret` → `AppSecrets`/`JwtSecret`/`FirebaseSecret` 3개로 재구성, `RuleConnection` 참조 변경 |
| `.github/workflows/ci_cd.yml` | 수정 | "Copy Spring .yml" 단계 삭제, Docker build-args 정리 |
| `.github/workflows/initialize_project.yml` | 수정 | 신규 yml 3개의 `{projectName}` 치환 단계 추가 |

**작업 순서 원칙:**
1. yml 파일을 먼저 정리한다 (코드가 참조할 placeholder가 yml에 정의되어야 컴파일·부팅 가능).
2. Java 코드 변경 (yml에서 `aws.credentials` 블록이 사라지므로 함께 적용해야 부팅 실패 방지).
3. CloudFormation, CI/CD, initialize_project는 코드/yml과 결합도 낮아 그 다음 순서.

---

## Task 1: `application-dev.yml` 생성

**Files:**
- Create: `src/main/resources/application-dev.yml`

- [ ] **Step 1: 파일 생성 및 내용 작성**

`src/main/resources/application-dev.yml`:
```yaml
app:
  env: dev

spring:
  cloud:
    aws:
      region:
        static: ap-northeast-2
  config:
    import:
      - aws-secretsmanager:{projectName}/dev/db
      - aws-secretsmanager:{projectName}/dev/secrets
      - aws-secretsmanager:{projectName}/dev/jwt
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://${host}:${port}/${dbname}
    username: ${username}
    password: ${password}
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
        default_batch_fetch_size: 500
    open-in-view: false
  flyway:
    enabled: true
    baseline-on-migrate: true
  jackson:
    serialization:
      write-enums-using-to-string: true
    deserialization:
      read-enums-using-to-string: true

springdoc:
  swagger-ui:
    path: /swagger
    tags-sorter: alpha
    operations-sorter: method
    disable-swagger-default-url: false
  default-produces-media-type: application/json
  group-configs:
    - group: internal
      paths-to-match: /v1/internal/**
      display-name: Internal API
    - group: admin
      paths-to-match: /v1/admin/**
      display-name: Admin API
    - group: user
      paths-to-match: /v1/**
      paths-to-exclude:
        - /v1/admin/**
        - /v1/internal/**
      display-name: User API
  api-docs:
    enabled: true
    servers: []

jwt:
  header: Authorization
  secret: ${jwt-secret}
  access-token-validity-in-milliseconds: 604800000
  refresh-token-validity-in-milliseconds: 7776000000
  algorithm: HS256

google:
  oauth:
    client-id: ${google-client-id}

apple:
  oauth:
    client-id: ${apple-client-id}

aws:
  region:
    static: ap-northeast-2
  s3:
    bucket: {projectName}-dev-bucket
    bucket-full-path: "https://${aws.s3.bucket}.s3.amazonaws.com/_media"
    bucket-path: "https://${aws.s3.bucket}.s3.ap-northeast-2.amazonaws.com/"

api-key:
  secret-key: ${api-key}

logging:
  filter:
    exclude-paths:
      - /health
      - /swagger-ui
      - /v3/api-docs
      - /webjars
      - /favicon.ico

cors:
  allowed-origins:
    - http://localhost:3000

api:
  validation:
    internal-path: /v1/internal/
```

기존 `application-{env}.yml` 대비 핵심 차이점:
- `aws.credentials.access-key/secret-key` 블록 **삭제** (Task Role 사용)
- `spring.cloud.aws.credentials` 블록 **삭제** (Task Role 사용)
- `spring.config.import`가 단일 → 3개 SM (db, secrets, jwt)
- `jwt.secret: {jwt-access-key}` placeholder → `${jwt-secret}` (SM 키 매핑)
- `firebase` 블록 **삭제** (코드에서 직접 SM fetch + 파싱 예정)
- `api-key.secret-name`/`api-key.secret-key: key` → `api-key.secret-key: ${api-key}` 한 줄

- [ ] **Step 2: YAML 문법 검증**

Run:
```bash
python3 -c "import yaml; yaml.safe_load(open('src/main/resources/application-dev.yml'))"
```
Expected: 출력 없음 (오류 시 line number 출력).

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/application-dev.yml
git commit -m "feat: add application-dev.yml with SM-based config import"
```

---

## Task 2: `application-prod.yml` 생성

**Files:**
- Create: `src/main/resources/application-prod.yml`

- [ ] **Step 1: 파일 생성 및 내용 작성**

`src/main/resources/application-prod.yml`:
```yaml
app:
  env: prod

spring:
  cloud:
    aws:
      region:
        static: ap-northeast-2
  config:
    import:
      - aws-secretsmanager:{projectName}/prod/db
      - aws-secretsmanager:{projectName}/prod/secrets
      - aws-secretsmanager:{projectName}/prod/jwt
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://${host}:${port}/${dbname}
    username: ${username}
    password: ${password}
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
        default_batch_fetch_size: 500
    open-in-view: false
  flyway:
    enabled: true
    baseline-on-migrate: true
  jackson:
    serialization:
      write-enums-using-to-string: true
    deserialization:
      read-enums-using-to-string: true

springdoc:
  swagger-ui:
    path: /swagger
    tags-sorter: alpha
    operations-sorter: method
    disable-swagger-default-url: false
  default-produces-media-type: application/json
  group-configs:
    - group: internal
      paths-to-match: /v1/internal/**
      display-name: Internal API
    - group: admin
      paths-to-match: /v1/admin/**
      display-name: Admin API
    - group: user
      paths-to-match: /v1/**
      paths-to-exclude:
        - /v1/admin/**
        - /v1/internal/**
      display-name: User API
  api-docs:
    enabled: true
    servers: []

jwt:
  header: Authorization
  secret: ${jwt-secret}
  access-token-validity-in-milliseconds: 604800000
  refresh-token-validity-in-milliseconds: 7776000000
  algorithm: HS256

google:
  oauth:
    client-id: ${google-client-id}

apple:
  oauth:
    client-id: ${apple-client-id}

aws:
  region:
    static: ap-northeast-2
  s3:
    bucket: {projectName}-prod-bucket
    bucket-full-path: "https://${aws.s3.bucket}.s3.amazonaws.com/_media"
    bucket-path: "https://${aws.s3.bucket}.s3.ap-northeast-2.amazonaws.com/"

api-key:
  secret-key: ${api-key}

logging:
  filter:
    exclude-paths:
      - /health
      - /swagger-ui
      - /v3/api-docs
      - /webjars
      - /favicon.ico

cors:
  allowed-origins: []

api:
  validation:
    internal-path: /v1/internal/
```

dev와의 차이점:
- `app.env: prod`
- `spring.config.import`의 환경 경로가 `prod`
- `aws.s3.bucket` suffix가 `-prod-bucket`
- `cors.allowed-origins`가 비어 있음 (운영 도메인은 별도 작업에서 추가)

- [ ] **Step 2: YAML 문법 검증**

Run:
```bash
python3 -c "import yaml; yaml.safe_load(open('src/main/resources/application-prod.yml'))"
```
Expected: 출력 없음.

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/application-prod.yml
git commit -m "feat: add application-prod.yml with SM-based config import"
```

---

## Task 3: `application-local.yml` 생성

**Files:**
- Create: `src/main/resources/application-local.yml`

- [ ] **Step 1: 파일 생성 및 내용 작성**

`src/main/resources/application-local.yml`:
```yaml
# 로컬 실행 전 AWS_PROFILE 환경변수를 본 프로젝트용 프로필로 지정.
# 예) export AWS_PROFILE=myproject-dev   또는 IDE Run Configuration에 설정
# DefaultCredentialsProvider 체인이 ~/.aws/credentials의 해당 프로필을 자동 사용한다.

app:
  env: local

spring:
  cloud:
    aws:
      region:
        static: ap-northeast-2
  config:
    import:
      - aws-secretsmanager:{projectName}/dev/db
      - aws-secretsmanager:{projectName}/dev/secrets
      - aws-secretsmanager:{projectName}/dev/jwt
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://${host}:${port}/${dbname}
    username: ${username}
    password: ${password}
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
        default_batch_fetch_size: 500
    open-in-view: false
  flyway:
    enabled: true
    baseline-on-migrate: true
  jackson:
    serialization:
      write-enums-using-to-string: true
    deserialization:
      read-enums-using-to-string: true

springdoc:
  swagger-ui:
    path: /swagger
    tags-sorter: alpha
    operations-sorter: method
    disable-swagger-default-url: false
  default-produces-media-type: application/json
  api-docs:
    enabled: true
    servers: []

jwt:
  header: Authorization
  secret: ${jwt-secret}
  access-token-validity-in-milliseconds: 604800000
  refresh-token-validity-in-milliseconds: 7776000000
  algorithm: HS256

google:
  oauth:
    client-id: ${google-client-id}

apple:
  oauth:
    client-id: ${apple-client-id}

aws:
  region:
    static: ap-northeast-2
  s3:
    bucket: {projectName}-dev-bucket
    bucket-full-path: "https://${aws.s3.bucket}.s3.amazonaws.com/_media"
    bucket-path: "https://${aws.s3.bucket}.s3.ap-northeast-2.amazonaws.com/"

api-key:
  secret-key: ${api-key}

logging:
  filter:
    exclude-paths:
      - /health
      - /swagger-ui
      - /v3/api-docs
      - /webjars
      - /favicon.ico

cors:
  allowed-origins:
    - http://localhost:3000

api:
  validation:
    internal-path: /v1/internal/
```

dev와의 차이점:
- `app.env: local`
- `spring.jpa.show-sql: true` (로컬 디버깅용)
- 상단 주석으로 `AWS_PROFILE` 환경변수 가이드

- [ ] **Step 2: YAML 문법 검증**

Run:
```bash
python3 -c "import yaml; yaml.safe_load(open('src/main/resources/application-local.yml'))"
```
Expected: 출력 없음.

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/application-local.yml
git commit -m "feat: add application-local.yml using dev SM"
```

---

## Task 4: 기존 `application-{env}.yml` 삭제

**Files:**
- Delete: `src/main/resources/application-{env}.yml`

- [ ] **Step 1: 파일 삭제**

Run:
```bash
git rm src/main/resources/application-\{env\}.yml
```
Expected: `rm 'src/main/resources/application-{env}.yml'`

- [ ] **Step 2: 잔존 참조 확인**

Run:
```bash
grep -rn "application-{env}" /Users/poku/projects/toktokhan/spring/spring-init --include="*.yml" --include="*.java" --include="*.gradle" 2>/dev/null
```
Expected: 출력 없음 (참조 없음). 만약 출력이 있으면 그 파일도 함께 정리 후 같은 commit 포함.

- [ ] **Step 3: Commit**

```bash
git commit -m "chore: remove application-{env}.yml template placeholder"
```

---

## Task 5: `S3Config.java` Task Role 모드로 전환

**Files:**
- Modify: `src/main/java/com/spring/spring_init/common/aws/S3Config.java`

- [ ] **Step 1: 파일 전체 교체**

`src/main/java/com/spring/spring_init/common/aws/S3Config.java` 새 전체 내용:
```java
package com.spring.spring_init.common.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

	@Value("${aws.region.static}")
	private String region;

	@Bean
	public S3Client s3Client() {
		return S3Client.builder()
			.region(Region.of(region))
			.credentialsProvider(DefaultCredentialsProvider.create())
			.build();
	}

	@Bean
	public S3Presigner s3Presigner() {
		return S3Presigner.builder()
			.region(Region.of(region))
			.credentialsProvider(DefaultCredentialsProvider.create())
			.build();
	}
}
```

기존 코드 대비 변경점:
- `@Value("${aws.credentials.access-key|secret-key}")` 두 필드 제거
- `awsBasicCredentials` 필드 제거
- `@PostConstruct getAwsBasicCredentials()` 메서드 제거
- import에서 `AwsBasicCredentials`, `StaticCredentialsProvider`, `jakarta.annotation.PostConstruct` 제거
- import에 `DefaultCredentialsProvider` 추가
- 두 Bean 모두 `.credentialsProvider(DefaultCredentialsProvider.create())` 사용

- [ ] **Step 2: 컴파일 검증**

Run:
```bash
./gradlew compileJava
```
Expected: `BUILD SUCCESSFUL` (오류 시 import 누락/오타 확인).

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/spring/spring_init/common/aws/S3Config.java
git commit -m "refactor(aws): switch S3Config to DefaultCredentialsProvider for Task Role auth"
```

---

## Task 6: `SesConfig.java` Task Role 모드로 전환

**Files:**
- Modify: `src/main/java/com/spring/spring_init/common/aws/SesConfig.java`

- [ ] **Step 1: 파일 전체 교체**

`src/main/java/com/spring/spring_init/common/aws/SesConfig.java` 새 전체 내용:
```java
package com.spring.spring_init.common.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
public class SesConfig {

    @Value("${aws.region.static}")
    private String region;

    @Bean
    public SesClient sesClient() {
        return SesClient.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }
}
```

기존 코드 대비 변경점:
- `@Value("${aws.credentials.access-key|secret-key}")` 두 필드 제거
- import에서 `AwsBasicCredentials`, `StaticCredentialsProvider` 제거
- import에 `DefaultCredentialsProvider` 추가
- `SesClient` Bean이 `DefaultCredentialsProvider.create()` 사용

- [ ] **Step 2: 컴파일 검증**

Run:
```bash
./gradlew compileJava
```
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/spring/spring_init/common/aws/SesConfig.java
git commit -m "refactor(aws): switch SesConfig to DefaultCredentialsProvider for Task Role auth"
```

---

## Task 7: `cloudformation/ecs.yml` — `Secret` 리소스 재구성

**Files:**
- Modify: `.github/cloudformation/ecs.yml:19-28` (기존 `Secret` 리소스 영역)

- [ ] **Step 1: 기존 `Secret` 리소스 블록을 새 3개 리소스로 교체**

기존 (19-28행):
```yaml
  # Secret Manager
  Secret:
    Type: AWS::SecretsManager::Secret
    Properties:
      Name: !Sub '${ProjectName}/${ENV}/spring'
      GenerateSecretString:
        SecretStringTemplate: !Sub '{}'
        GenerateStringKey: 'key'
        PasswordLength: 50
        ExcludeCharacters: '"@/\&;='
```

대체:
```yaml
  # Secrets Manager — 도메인별 분리
  AppSecrets:
    Type: AWS::SecretsManager::Secret
    Properties:
      Name: !Sub '${ProjectName}/${ENV}/secrets'
      GenerateSecretString:
        SecretStringTemplate: '{"google-client-id":"REPLACE_ME","apple-client-id":"REPLACE_ME"}'
        GenerateStringKey: 'api-key'
        PasswordLength: 50
        ExcludeCharacters: '"@/\&;='

  JwtSecret:
    Type: AWS::SecretsManager::Secret
    Properties:
      Name: !Sub '${ProjectName}/${ENV}/jwt'
      GenerateSecretString:
        SecretStringTemplate: '{}'
        GenerateStringKey: 'jwt-secret'
        PasswordLength: 64
        ExcludeCharacters: '"@/\&;='

  FirebaseSecret:
    Type: AWS::SecretsManager::Secret
    Properties:
      Name: !Sub '${ProjectName}/${ENV}/firebase'
      SecretString: '{"type":"service_account","project_id":"REPLACE_ME","private_key_id":"REPLACE_ME","private_key":"REPLACE_ME","client_email":"REPLACE_ME","client_id":"REPLACE_ME"}'
```

- [ ] **Step 2: `RuleConnection.ApiKeyValue` 참조 갱신**

`.github/cloudformation/ecs.yml`에서 `RuleConnection` 리소스의 `ApiKeyValue` 라인을 찾아 변경:

기존:
```yaml
          ApiKeyValue: !Join [ '', [ '{{resolve:secretsmanager:', !Ref Secret, ':SecretString:key}}' ] ]
```

변경 후:
```yaml
          ApiKeyValue: !Join [ '', [ '{{resolve:secretsmanager:', !Ref AppSecrets, ':SecretString:api-key}}' ] ]
```

차이점: `!Ref Secret` → `!Ref AppSecrets`, `:SecretString:key` → `:SecretString:api-key`.

- [ ] **Step 3: CloudFormation 템플릿 검증**

Run:
```bash
aws cloudformation validate-template --template-body file://.github/cloudformation/ecs.yml
```
Expected: `Parameters`, `Description`이 정상 출력되고 오류 없음.

만약 AWS CLI/자격증명 미설정이면 최소한 YAML 문법만 검증:
```bash
python3 -c "import yaml; yaml.safe_load(open('.github/cloudformation/ecs.yml'))"
```
Expected: 출력 없음.

- [ ] **Step 4: 잔존 `!Ref Secret` 참조 확인**

Run:
```bash
grep -n "!Ref Secret" .github/cloudformation/ecs.yml
```
Expected: 출력 없음 (모든 참조가 `AppSecrets`로 변경되었어야 함).

- [ ] **Step 5: Commit**

```bash
git add .github/cloudformation/ecs.yml
git commit -m "feat(infra): split SM secrets into AppSecrets/JwtSecret/FirebaseSecret"
```

---

## Task 8: `.github/workflows/ci_cd.yml` — Copy yml 단계 삭제 + build-args 정리

**Files:**
- Modify: `.github/workflows/ci_cd.yml:29-34` (Copy Spring .yml 단계)
- Modify: `.github/workflows/ci_cd.yml:60-72` (Build & Push Backend Image 단계의 build-args)

- [ ] **Step 1: "Copy Spring .yml" 단계 삭제**

기존 (29-34행, 빈 줄 포함):
```yaml
      # Spring .yml file copy
      - name: Copy Spring .yml
        env:
          CREATE_SECRET: ${{secrets.APPLICATION_DEV_YML}}
          CREATE_SECRET_DIR: src/main/resources
          CREATE_SECRET_DIR_FILE_NAME: application-dev.yml
        run: echo "$CREATE_SECRET" > $CREATE_SECRET_DIR/$CREATE_SECRET_DIR_FILE_NAME

```

이 블록을 통째로 삭제. 직전 "Set Up JDK 17" 단계와 "Build with Gradle" 단계 사이가 직접 연결되도록 한다.

- [ ] **Step 2: "Build & Push Backend Image" 단계의 build-args 제거**

기존 (60-72행 영역):
```yaml
      - name: Build & Push Backend Image
        uses: docker/build-push-action@v4
        # uses: docker/build-push-action@v4 2와 4의 차이는?
        with:
          context: .
          build-args: |
            CI=true
            APP_ENV=${{ env.PROJECT_ENV }}
            AWS_ACCESS_KEY_ID=${{ secrets.AWS_ACCESS_KEY_ID }}
            AWS_SECRET_ACCESS_KEY=${{ secrets.AWS_SECRET_ACCESS_KEY }}
          push: true
          tags: >-
            ${{ steps.login-ecr.outputs.registry }}/${{ env.PROJECT_NAME }}-backend:${{ env.PROJECT_ENV }},
            ${{ steps.login-ecr.outputs.registry }}/${{ env.PROJECT_NAME }}-backend:${{ github.sha }}
          cache-from: type=gha
          cache-to: type=gha,mode=max
```

변경 후:
```yaml
      - name: Build & Push Backend Image
        uses: docker/build-push-action@v4
        with:
          context: .
          push: true
          tags: >-
            ${{ steps.login-ecr.outputs.registry }}/${{ env.PROJECT_NAME }}-backend:${{ env.PROJECT_ENV }},
            ${{ steps.login-ecr.outputs.registry }}/${{ env.PROJECT_NAME }}-backend:${{ github.sha }}
          cache-from: type=gha
          cache-to: type=gha,mode=max
```

차이점: 옛 주석 한 줄 + `build-args` 블록 4줄 제거.

- [ ] **Step 3: YAML 문법 검증**

Run:
```bash
python3 -c "import yaml; yaml.safe_load(open('.github/workflows/ci_cd.yml'))"
```
Expected: 출력 없음.

- [ ] **Step 4: 잔존 `APPLICATION_DEV_YML` 참조 확인**

Run:
```bash
grep -rn "APPLICATION_DEV_YML\|APPLICATION_PROD_YML" .github 2>/dev/null
```
Expected: 출력 없음.

- [ ] **Step 5: Commit**

```bash
git add .github/workflows/ci_cd.yml
git commit -m "ci: remove APPLICATION_DEV_YML copy step and unused docker build-args"
```

---

## Task 9: `.github/workflows/initialize_project.yml` — yml 치환 단계 추가

**Files:**
- Modify: `.github/workflows/initialize_project.yml:19-23` (Change 단계의 sed 명령들)

- [ ] **Step 1: "Change" 단계에 yml 3개 치환 추가**

기존 (19-23행):
```yaml
      - name: Change
        run: |
          sed -i "s|spring_init|${PROJECT_NAME}|g" settings.gradle
          sed -i "s|spring_init|${PROJECT_NAME}|g" build.gradle
          sed -i "s|#{PROJECT_NAME}|${PROJECT_NAME}|g" README.md
```

변경 후:
```yaml
      - name: Change
        run: |
          sed -i "s|spring_init|${PROJECT_NAME}|g" settings.gradle
          sed -i "s|spring_init|${PROJECT_NAME}|g" build.gradle
          sed -i "s|#{PROJECT_NAME}|${PROJECT_NAME}|g" README.md
          sed -i "s|{projectName}|${PROJECT_NAME}|g" src/main/resources/application-local.yml
          sed -i "s|{projectName}|${PROJECT_NAME}|g" src/main/resources/application-dev.yml
          sed -i "s|{projectName}|${PROJECT_NAME}|g" src/main/resources/application-prod.yml
```

- [ ] **Step 2: YAML 문법 검증**

Run:
```bash
python3 -c "import yaml; yaml.safe_load(open('.github/workflows/initialize_project.yml'))"
```
Expected: 출력 없음.

- [ ] **Step 3: sed 패턴 dry-run 검증**

Run:
```bash
grep -n "{projectName}" src/main/resources/application-dev.yml | head -5
grep -n "{projectName}" src/main/resources/application-prod.yml | head -5
grep -n "{projectName}" src/main/resources/application-local.yml | head -5
```
Expected: 각 파일마다 4개 이상 매치 (3개 SM import 경로 + s3 bucket 1개).

- [ ] **Step 4: Commit**

```bash
git add .github/workflows/initialize_project.yml
git commit -m "ci: substitute {projectName} in application yml files during init"
```

---

## Task 10: 통합 빌드 검증

**Files:**
- (변경 없음, 검증만 수행)

- [ ] **Step 1: 전체 빌드 (테스트 제외)**

Run:
```bash
./gradlew build -x test
```
Expected: `BUILD SUCCESSFUL`.

> 주의: 테스트는 SM 접근이 필요해 자격증명 없이 실패 가능성이 있어 제외. CI도 현재 `./gradlew build -x test`로 동작 중(ci_cd.yml 38행).

- [ ] **Step 2: 전체 변경 사항 git diff 리뷰**

Run:
```bash
git log --oneline origin/main..HEAD
```
Expected: 9개의 commit (Task 1~9) 출력.

Run:
```bash
git diff --stat origin/main..HEAD
```
Expected: 9개 파일이 변경된 통계 (4 추가, 5 수정, 1 삭제 — 단 같은 파일 여러 번 수정은 1번으로 집계됨).

- [ ] **Step 3: 잔존 hardcoded 시크릿 검사**

Run:
```bash
grep -rn "AWS_ACCESS_KEY_ID\|AWS_SECRET_ACCESS_KEY" src/ .github/ 2>/dev/null
```
Expected:
- `.github/workflows/ci_cd.yml`의 "Configure AWS credentials" 단계 (CI/CD 파이프라인 전용, 의도된 사용)
- `.github/workflows/build_*.yml` 파일들 (인프라 빌드 워크플로, 의도된 사용)
- 그 외 src/, application*.yml에는 출력 없음.

Run:
```bash
grep -rn "aws.credentials" src/ 2>/dev/null
```
Expected: 출력 없음 (yml/Java에서 모두 제거됨).

---

## 운영 절차 (PR 머지 전후 — 본 계획 외)

스펙 §9에 정의된 마이그레이션 절차가 별도 진행되어야 한다:

1. PR을 develop 브랜치에 푸시 (CI 자동 실행 안 함, develop 머지 시 dev 환경 배포)
2. **PR 머지 전**: dev 환경 CloudFormation stack을 `build_ecs.yml` 워크플로 또는 AWS CLI로 update → 새 SM 시크릿 3개 자동 생성, 기존 `${project}/dev/spring` 삭제
3. AWS 콘솔에서 `{project}/dev/secrets`의 `google-client-id`/`apple-client-id` placeholder 채우기
4. PR을 develop에 머지 → CI/CD 자동 트리거 → ECS 신규 task 배포
5. dev 환경 §5 검증 체크리스트 수행
6. main으로 PR → prod 동일 절차 반복
7. GitHub Repository Secret `APPLICATION_DEV_YML` (있다면 prod용도) 삭제

---

## Self-Review 결과

**Spec 커버리지 점검:**
- 스펙 §4 시크릿 4개 → Task 7에서 3개 신규 생성 (db는 rds.yml이 관리, 변경 없음) ✓
- 스펙 §5 yml 재구성 → Task 1-4 ✓
- 스펙 §6 Java 코드 변경 → Task 5-6 (S3Config, SesConfig). SecretsManagerConfig는 변경 없음 명시 ✓
- 스펙 §7 CloudFormation → Task 7 ✓
- 스펙 §8.1 ci_cd.yml → Task 8 ✓
- 스펙 §8.2 initialize_project.yml → Task 9 ✓
- 스펙 §9 마이그레이션 절차 → "운영 절차" 섹션에 명시 (코드 외 작업) ✓

**Placeholder 점검:** 모든 Task에 실제 코드/명령이 들어 있음. "TODO/TBD" 없음 (REPLACE_ME는 SM 템플릿 의도된 placeholder).

**타입/명명 일관성:** `AppSecrets`/`JwtSecret`/`FirebaseSecret` (CFN logical name), `api-key`/`jwt-secret`/`google-client-id` 등 (SM 키 이름) — 모든 Task에서 동일 표기 사용.
