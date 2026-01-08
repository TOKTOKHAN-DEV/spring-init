# Claude Code 설정 가이드

이 디렉토리는 Spring Boot 보일러플레이트 프로젝트를 위한 Claude Code 설정을 포함합니다.

## 📁 구조

```
.claude/
├── README.md              # 이 파일 - 사용 가이드
├── CLAUDE.md              # Claude의 프로젝트 메모리 (전체 컨텍스트, 가이드라인)
├── settings.json          # Claude Code 동작 설정 (권한)
├── commands/              # 커스텀 슬래시 커맨드
│   ├── new-entity.md
│   ├── new-api.md
│   └── add-exception.md
└── domains/               # 도메인별 상세 문서
    ├── _template.md       # 새 도메인 문서 템플릿
    ├── user.md           # User 도메인 (인증, 회원관리)
    └── common.md         # Common 도메인 (AWS, 파일, 이메일)
```

## 🔍 파일 역할 구분

### **CLAUDE.md** - Claude의 프로젝트 메모리
> "Claude가 이 프로젝트에 대해 알아야 할 모든 것"

**역할**: Claude가 모든 대화에서 참고하는 프로젝트 컨텍스트

**포함 내용**:
- 프로젝트 개요 및 기술 스택
- 패키지 구조
- 아키텍처 패턴
- 코딩 컨벤션 (네이밍, 파일 구성 원칙)
- 보안/테스트/데이터베이스/API 가이드라인
- 자주 사용하는 커맨드

**편집 방법**: 직접 편집 또는 `/memory` 커맨드 사용

### **settings.json** - Claude Code 권한 설정
> "Claude Code가 무엇을 할 수 있고 할 수 없는가"

**역할**: Claude Code의 파일 접근 및 명령어 실행 권한 제어

**포함 내용**:
- **permissions.deny**: 접근 금지할 파일 및 명령어
- **permissions.allow**: 명시적으로 허용할 파일 및 명령어

**편집 방법**: 직접 편집

### **commands/*.md** - 커스텀 슬래시 커맨드
> "자주 사용하는 워크플로우 자동화"

**역할**: 반복적인 작업을 `/command-name`으로 간단히 실행

**사용 시점**: 명시적으로 `/command-name` 입력할 때만

### **domains/*.md** - 도메인별 상세 문서
> "각 도메인의 API와 엔티티 정보"

**역할**: 도메인별 API 엔드포인트와 엔티티 문서화

**포함 내용**:
- 도메인 개요
- API 엔드포인트 목록 (엔드포인트, 설명, 인증, 예외)
- 주요 엔티티 및 필드 설명

**편집 방법**:
- 새 도메인 추가: `_template.md`를 복사하여 `{domain}.md` 생성
- CLAUDE.md에 링크 추가

---

## 🚀 빠른 시작

### 1단계: CLAUDE.md 확인 및 수정

`.claude/CLAUDE.md`를 열어 프로젝트 정보를 확인하세요.
프로젝트별로 수정이 필요한 부분:

```markdown
## 🔧 프로젝트별 설정

### 기본 정보
- **Base Package**: `com.spring.{project_name}`  👈 실제 프로젝트명으로 변경
- **Default Port**: `8080`
```

**참고**: 이 보일러플레이트는 템플릿이므로 `{project_name}` 부분을 실제 프로젝트명으로 교체해야 합니다.
예: `com.spring.ecommerce`, `com.spring.blog` 등

### 2단계: settings.json 권한 설정

`.claude/settings.json`을 열어 프로젝트에 맞게 권한을 조정하세요:

```json
{
  "permissions": {
    "deny": [
      // 위험한 명령어 차단
      "Bash(rm:-rf *)",
      "Bash(sudo:*)",

      // Git 강제 명령 차단
      "Bash(git:push --force)",
      "Bash(git:reset --hard)"
    ],
    "allow": [
      // 소스 코드 접근 허용
      "Read(./src/**)",
      "Write(./src/**)",

      // Gradle 명령어 허용
      "Bash(./gradlew:build)",
      "Bash(./gradlew:test)"
    ]
  }
}
```

### 3단계: 도메인별 문서 작성

각 도메인의 API와 엔티티를 문서화하세요:

**기존 도메인 수정**:
- `.claude/domains/user.md` - User 도메인 API 엔드포인트 확인 및 수정
- `.claude/domains/common.md` - Common 도메인 설정 확인

**새 도메인 추가**:
```bash
# 1. 템플릿 복사
cp .claude/domains/_template.md .claude/domains/product.md

# 2. product.md 편집하여 도메인 정보 작성
# 3. CLAUDE.md에 링크 추가
```

### 4단계: 커스텀 커맨드 사용

프로젝트에서 사용할 수 있는 슬래시 커맨드:

```bash
/new-entity       # JPA Entity + Repository 생성
/new-api          # REST API (Controller + Service + DTO) 생성
/add-exception    # Exception 코드 추가
```

**사용 예시**:
```
> /new-entity
Claude: "어떤 Entity를 생성하시겠습니까?"

You: "Product라는 이름의 Entity를 만들어줘.
      name(String), price(Long), description(String) 필드가 필요해"

→ Product Entity + ProductJpaRepository + ProductQueryRepository + ProductRepository 자동 생성
```

---

## 📖 상세 가이드

### CLAUDE.md 활용법

**언제 수정하나요?**
- 새로운 아키텍처 패턴을 도입할 때
- 코딩 컨벤션이 변경될 때
- 새로운 기술 스택을 추가할 때
- 팀 가이드라인이 업데이트될 때

**수정 예시**:
```markdown
## 🏗️ 기술 스택

**Framework & Language**
- Spring Boot 3.3.5
- Redis 7.0 (NEW!)  👈 새로운 기술 추가
```

### settings.json 활용법

**언제 수정하나요?**
- 민감한 파일/폴더를 보호해야 할 때
- 위험한 명령어를 차단해야 할 때
- 특정 파일이나 명령어만 허용해야 할 때

**권한 설정 패턴**:

**파일 접근 제어**:
```json
{
  "permissions": {
    "deny": [
      "Read(.env)",                    // 환경 변수 파일
      "Read(**/application-prod.yml)", // 프로덕션 설정
      "Read(./secrets/**)"             // 민감 정보 디렉토리
    ],
    "allow": [
      "Read(./src/**)",                // 소스 코드
      "Write(./src/**)",               // 소스 코드 쓰기
      "Edit(./src/**)"                 // 소스 코드 수정
    ]
  }
}
```

**명령어 실행 제어**:
```json
{
  "permissions": {
    "deny": [
      "Bash(rm:-rf *)",               // 위험한 삭제 명령
      "Bash(sudo:*)",                 // sudo 권한 명령
      "Bash(git:push --force)"        // Git 강제 푸시
    ],
    "allow": [
      "Bash(./gradlew:build)",        // Gradle 빌드
      "Bash(./gradlew:test)",         // 테스트 실행
      "Bash(git:status)",             // Git 상태 확인
      "Bash(git:diff)"                // Git diff
    ]
  }
}
```

### 커스텀 커맨드 추가하기

새로운 커맨드를 추가하려면 `.claude/commands/` 디렉토리에 마크다운 파일을 만드세요:

**예시**: `.claude/commands/add-test.md`

```markdown
---
description: 단위 테스트를 추가합니다
---

# 단위 테스트 추가

새로운 서비스나 컨트롤러에 대한 단위 테스트를 작성합니다.

## 작업 순서
1. 테스트 대상 클래스 확인
2. @SpringBootTest 또는 @WebMvcTest 선택
3. Mock 설정 (@MockBean)
4. 테스트 케이스 작성 (given-when-then 패턴)
5. Assertion 추가
```

### 도메인 문서 관리

**도메인 문서는 간결하게**:
- API 엔드포인트 목록만 포함
- 엔티티 필드 정보만 포함
- 비즈니스 로직은 코드와 Swagger에서 확인

**언제 업데이트하나요?**
- 새로운 API 엔드포인트를 추가할 때
- 엔티티 필드를 변경할 때
- 도메인 개요가 변경될 때

---

## 🔐 보안 주의사항

### ✅ Git에 커밋해도 안전한 파일

- `.claude/CLAUDE.md` - 프로젝트 가이드라인
- `.claude/settings.json` - 권한 설정 (민감 정보 없음)
- `.claude/commands/*.md` - 커맨드 정의
- `.claude/domains/*.md` - 도메인 문서
- `.claude/README.md` - 사용 가이드

### ❌ settings.json에 포함하면 안 되는 정보

**절대 포함하지 마세요**:
- 데이터베이스 비밀번호
- API 키, Secret Key
- AWS 자격증명
- JWT Secret
- 개인정보

**대신 사용하세요**:
- AWS Secrets Manager
- 환경 변수 (.env 파일 - gitignored)
- `.claude/settings.local.json` (개인 설정, gitignored)

---

## 🔄 로컬 설정 오버라이드

개인별로 다른 설정이 필요한 경우 `.claude/settings.local.json`을 사용하세요.

**예시**: `.claude/settings.local.json`

```json
{
  "permissions": {
    "allow": [
      "Bash(docker:*)"  // 개인 환경에서만 Docker 명령 허용
    ]
  }
}
```

이 파일은 `.gitignore`에 추가되어 있어 Git에 커밋되지 않습니다.

---

## 💡 유용한 팁

### 1. Claude에게 프로젝트 이해시키기

CLAUDE.md 덕분에 Claude는 프로젝트를 자동으로 이해합니다:

```
You: "새로운 Product API를 추가해줘"

Claude:
✅ BaseEntity를 상속받는 Product Entity 생성
✅ ProductApi 인터페이스 + ProductController 구현 분리
✅ ProductJpaRepository, ProductQueryRepository, ProductRepository 생성
✅ ResponseDTO로 응답 래핑
✅ Swagger 문서화 자동 추가
✅ 기존 코딩 컨벤션 준수
```

### 2. 일관된 코드 스타일

CLAUDE.md의 코딩 컨벤션이 자동 적용됩니다:

```
You: "UserService와 같은 패턴으로 ProductService를 만들어줘"

Claude:
✅ 동일한 패키지 구조
✅ 동일한 네이밍 규칙
✅ 동일한 아키텍처 패턴
```

### 3. 워크플로우 자동화

```
You: /new-api
     "주문(Order) 생성 API를 만들어줘"

Claude:
✅ OrderApi.java 생성
✅ OrderController.java 생성
✅ CreateOrderRequestDto.java 생성
✅ OrderResponseDto.java 생성
✅ OrderService에 메서드 추가
✅ Swagger 문서화 완료
```

### 4. 도메인 문서 활용

```
You: "User 도메인 문서를 참고해서 로그아웃 API를 추가해줘"

Claude:
→ .claude/domains/user.md를 읽고 기존 API 패턴 파악
→ 일관된 형식으로 로그아웃 API 추가
→ 도메인 문서도 함께 업데이트
```

---

## 🤝 팀원과 공유

### Git에 커밋하여 공유

```bash
git add .claude/
git commit -m "feat: Add Claude Code configuration for team productivity"
git push
```

### 팀원 온보딩

새로운 팀원이 합류하면:

1. `.claude/README.md` (이 파일) 읽기
2. `.claude/CLAUDE.md`로 프로젝트 구조 이해하기
3. `.claude/domains/` 문서로 각 도메인 API 파악하기
4. `/new-entity`, `/new-api` 등 커맨드 활용

---

## 🆘 문제 해결

### Q: 커맨드가 작동하지 않아요

**A**: 다음을 확인하세요:
- `.claude/commands/` 디렉토리가 존재하는지
- 마크다운 파일의 frontmatter (---) 형식이 올바른지
- Claude Code를 최신 버전으로 업데이트

### Q: CLAUDE.md가 적용되지 않아요

**A**: 다음을 확인하세요:
- 파일이 `.claude/CLAUDE.md`에 있는지
- Claude Code를 재시작
- `/memory` 커맨드로 메모리 확인

### Q: 권한 설정이 작동하지 않아요

**A**: 다음을 확인하세요:
- `settings.json` 파일의 JSON 형식이 올바른지
- 권한 패턴 문법이 올바른지 (예: `Read(./src/**)`)
- Claude Code를 재시작

---

## 📚 참고 자료

- [Claude Code 공식 문서](https://github.com/anthropics/claude-code)
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [프로젝트 README](../README.md)

---

## 📝 변경 이력

### Version 1.2.0 (2026-01-07)

- settings.json 단순화: 권한 설정만 포함
- domains/*.md 간소화: API와 엔티티 정보만 포함
- Repository 구조 명확화: JpaRepository, QueryRepository, Repository 3단 구조
- /review-security 커맨드 제거

### Version 1.1.0 (2026-01-07)

- 역할 구분: CLAUDE.md와 settings.json 분리
- 도메인별 문서 추가: domains/ 디렉토리
- settings.local.json 지원 추가

### Version 1.0.0 (2026-01-07)

- 초기 설정 추가
- 커스텀 커맨드 추가
- Spring Boot 3.3.5, Java 17 기반 설정

---

**팀원들에게**: 이 설정은 프로젝트의 생산성을 높이기 위한 것입니다.
자유롭게 수정하고 개선해주세요! 🚀
