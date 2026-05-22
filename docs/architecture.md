# Architecture

## 기술 구조

- Java 21
- Spring Boot 3.3.x
- Spring MVC
- Spring Data JPA
- PostgreSQL
- Thymeleaf
- Bootstrap CDN
- Gradle Wrapper

현재 저장소에는 `package.json`, Vite, TypeScript 설정이 없다. 첫 버전에서는 React를 사용하지 않는다.

## 주요 디렉토리

```text
src/main/java/com/devgwon/growthsimulator
├── character
│   ├── domain
│   ├── repository
│   └── service
├── dashboard
│   ├── service
│   └── web
├── errorrecord
│   ├── domain
│   ├── repository
│   ├── service
│   └── web
├── global
│   ├── common
│   ├── config
│   └── exception
├── growth
│   ├── domain
│   ├── repository
│   ├── service
│   └── web
├── monster
│   ├── domain
│   ├── repository
│   ├── service
│   └── web
├── quest
│   ├── domain
│   ├── repository
│   ├── service
│   └── web
├── review
│   ├── domain
│   ├── repository
│   ├── service
│   └── web
├── schedule
│   ├── domain
│   ├── repository
│   ├── service
│   └── web
└── weeklyreport
    ├── service
    └── web
```

```text
src/main/resources
├── application.yml
├── static
│   ├── css
│   └── js
└── templates
    ├── dashboard
    ├── daily-reviews
    ├── errors
    ├── fragments
    ├── growth-categories
    ├── growth-sub-categories
    ├── monsters
    ├── quests
    ├── schedule-types
    ├── schedules
    └── weekly-reports
```

## 계층 역할

- `domain`: JPA Entity와 enum
- `repository`: Spring Data JPA Repository
- `service`: 비즈니스 로직, 화면 DTO 조립, 트랜잭션 처리
- `web`: Spring MVC Controller
- `templates`: Thymeleaf 화면
- `static/css`, `static/js`: 화면 스타일과 최소 JavaScript
- `global`: 공통 설정, 예외 처리, 공통 패키지

## 데이터 흐름

일반 CRUD 흐름:

1. Controller가 요청을 받는다.
2. Controller는 입력 DTO 또는 form 객체를 Service에 전달한다.
3. Service가 Repository를 통해 Entity를 조회/저장한다.
4. Service가 View DTO를 조립한다.
5. Controller는 Model에 View DTO를 넣고 Thymeleaf template을 반환한다.

Quest 완료 흐름:

1. Quest 조회
2. 이미 완료된 Quest인지 검증
3. Quest status를 `COMPLETED`로 변경
4. `completedAt` 저장
5. Profile exp 증가
6. Quest에 연결된 GrowthSubCategory exp 증가
7. GrowthLog 저장
8. Level up 여부 계산
9. 결과 메시지 반환

## 화면 구조

- Dashboard는 `DashboardController`와 `DashboardService`를 중심으로 동작한다.
- Dashboard template은 `templates/dashboard/index.html`이다.
- 공통 네비게이션은 `templates/fragments/navigation.html`을 사용한다.
- Quest, Schedule, Daily Review, Weekly Report, Monster, Error Museum, Growth Settings는 각 전용 template 디렉토리를 가진다.

## 작업 제약

- Controller에 비즈니스 로직을 추가하지 않는다.
- Dashboard 화면 데이터 조립은 Controller가 아니라 `DashboardService`에서 처리한다.
- Quest 완료는 Service 트랜잭션 안에서 처리한다.
- UI 컴포넌트와 도메인 로직을 과도하게 섞지 않는다.
- 공통 UI 조각은 재사용 가능한 fragment 또는 공통 CSS로 분리한다.
- 상태 관리 위치를 임의로 분산하지 않는다. 현재는 서버 렌더링 중심이며 클라이언트 상태 관리는 최소 JavaScript로 제한한다.
- 파일명과 디렉토리 네이밍은 기존 feature-based package structure를 유지한다.
- 대규모 UI 변경은 한 번에 하지 않는다. Dashboard, 공통 navigation, 각 기능 화면을 분리해서 다룬다.
- 새로운 외부 도구, 프론트엔드 프레임워크, compose 파일은 요구가 명확할 때만 도입한다.

## 구조 변경 시 주의사항

- `DeveloperStat`은 현재 남아 있다. GrowthCategory/GrowthSubCategory 구조가 있어도 임의로 제거하지 않는다.
- Weekly Report는 저장형 Entity 없이 조회형 DTO로 조립한다.
- Monster 보상 EXP는 표시용이며 실제 Profile EXP나 GrowthLog에 반영하지 않는다.
- Schedule 완료는 EXP/GrowthLog를 발생시키지 않는다.
- Daily Review 작성은 EXP/GrowthLog를 발생시키지 않는다.
- Error Museum은 GrowthSubCategory 필수, Quest 선택 연결 구조를 유지한다.
