---
description: 새로운 JPA Entity와 관련 파일들을 생성합니다
---

# 새로운 Entity 생성

새로운 도메인 Entity를 생성하고 관련 Repository, JpaRepository를 함께 생성합니다.

## 작업 순서

1. **Entity 정보 확인**
   - Entity 이름 확인
   - 필드 정보 확인 (필드명, 타입, 제약조건)
   - 다른 Entity와의 관계 확인 (OneToMany, ManyToOne 등)

2. **BaseEntity 패턴 확인**
   - `common/base/BaseEntity.java`를 확인하여 공통 필드 파악
   - 새로운 Entity는 BaseEntity를 상속받아야 함

3. **Entity 생성**
   - 위치: `src/main/java/com/spring/{project_name}/{domain}/entity/{EntityName}.java`
   - BaseEntity 상속
   - Lombok 어노테이션 사용 (@Getter, @NoArgsConstructor, @AllArgsConstructor, @Builder)
   - JPA 어노테이션 추가 (@Entity, @Table, @Id, @GeneratedValue 등)
   - 연관관계 매핑 시 FetchType 명시

4. **JpaRepository 생성**
   - 위치: `src/main/java/com/spring/{project_name}/{domain}/repository/{EntityName}JpaRepository.java`
   - JpaRepository<Entity, IdType> 상속
   - 기본 CRUD 메서드 제공 (save, findById 등)

5. **QueryRepository 생성** (필요시)
   - 위치: `src/main/java/com/spring/{project_name}/{domain}/repository/{EntityName}QueryRepository.java`
   - QueryDSL을 사용하여 복잡한 쿼리 구현
   - JPAQueryFactory 주입받아 사용
   - @Repository 어노테이션 추가

6. **Repository 생성**
   - 위치: `src/main/java/com/spring/{project_name}/{domain}/repository/{EntityName}Repository.java`
   - JpaRepository와 QueryRepository를 주입받아 통합
   - Service에서 사용하는 실제 Repository
   - @Repository 어노테이션 추가

## 예시

기존 User Entity를 참고하세요:
- Entity: `src/main/java/com/spring/{project_name}/user/entity/User.java`
- JpaRepository: `src/main/java/com/spring/{project_name}/user/repository/UserJpaRepository.java`
- QueryRepository: `src/main/java/com/spring/{project_name}/user/repository/UserQueryRepository.java`
- Repository: `src/main/java/com/spring/{project_name}/user/repository/UserRepository.java`

**참고**: `{project_name}` 부분은 실제 프로젝트명으로 변경됩니다.

## 주의사항

- **BaseEntity 상속**: 모든 Entity는 BaseEntity를 상속받아야 합니다
- **Fetch Type**: 연관관계 매핑 시 FetchType.LAZY를 기본으로 사용합니다
- **생성자**: @NoArgsConstructor(access = AccessLevel.PROTECTED)를 사용하여 JPA 요구사항을 충족합니다
- **Builder**: 복잡한 객체 생성을 위해 @Builder를 사용합니다
- **Table 이름**: @Table(name = "테이블명")으로 명시적으로 지정합니다
- **QueryDSL**: Gradle build 후 Q클래스가 자동 생성됩니다 (build/generated/sources/annotationProcessor/java/main/)

## 생성 후 확인 사항

- [ ] Entity 클래스가 BaseEntity를 상속받는가?
- [ ] 적절한 JPA 어노테이션이 추가되었는가?
- [ ] Repository가 올바르게 생성되었는가?
- [ ] QueryDSL 사용 시 Q클래스가 생성되었는가? (Gradle build 필요)
- [ ] 연관관계 매핑이 올바른가?
