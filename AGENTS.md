# AGENTS.md

## Project

Developer Growth Simulator

## Goal

Developer Growth Simulator는 개발자의 공부, 업무, 회고, 이직 준비를 RPG처럼 관리하는 가벼운 웹 앱이다.

사용자는 매일 퀘스트를 완료하고 EXP를 얻으며, 레벨업하고, GrowthCategory/GrowthSubCategory 기반 성장 항목을 키운다.

단순 TODO 앱이 아니라 "개발자 성장 시뮬레이터" 느낌이어야 한다.

## Product Direction

UI는 무거운 관리자 페이지가 아니라, 캐릭터가 말풍선으로 안내하는 가벼운 게임형 UI를 지향한다.

예시 캐릭터 이름은 "백둥이"로 한다.

예시 메시지:

- 오늘은 작은 퀘스트 하나만 깨도 충분해요.
- 코테를 풀었군요. 알고리즘 근육이 조금 붙었어요.
- 휴식도 성장입니다.
- 오늘도 출근한 것만으로 기본 EXP +5입니다.

## Tech Stack

- Java 21
- Spring Boot 3.x
- Spring MVC
- Spring Data JPA
- PostgreSQL
- Thymeleaf
- Bootstrap CDN or Tailwind CDN
- Gradle
- Lombok optional

React는 첫 버전에서 사용하지 않는다.

## Local Environment

로컬에서 실행한다.

개발 환경:

- Windows
- WSL
- Docker Desktop
- PostgreSQL은 Docker 컨테이너로 이미 실행 중이다.
- PostgreSQL 컨테이너 이름은 `postgrest`라고 가정한다.

새 `docker-compose.yml`은 우선 만들지 않는다.

기존 PostgreSQL에 연결할 수 있도록 `application.yml` 설정을 작성한다.

DB 접속 정보는 우선 아래 기본값으로 둔다. 실제 값이 다르면 사용자가 수정할 수 있도록 README에 안내한다.

- host: `localhost`
- port: `5432`
- database: `<db_name>`
- username: `<db_username>`
- password: `<db_password>`

## MVP Scope

첫 번째 MVP에서는 아래 기능만 구현한다.

1. DeveloperProfile 생성/조회
2. DeveloperStat 관리
3. Quest 등록
4. Quest 목록 조회
5. Quest 완료
6. Quest 완료 시 EXP 증가
7. Quest 완료 시 관련 GrowthSubCategory 증가
8. EXP 100 이상이면 Level up
9. Dashboard 화면
10. 캐릭터 말풍선 메시지
11. Schedule Management
12. Daily Review
13. Weekly Report
14. Monster System

아직 아래 기능은 구현하지 않는다.

- AI API 연동
- Job Class 전직 시스템
- 로그인/회원가입
- 멀티 유저
- React 프론트엔드
- 복잡한 차트

## Domain

### DeveloperProfile

Fields:

- id
- nickname
- characterName
- level
- exp
- title
- createdAt
- updatedAt

기본값:

- level: `1`
- exp: `0`
- title: `주니어 백엔드`
- characterName: `백둥이`

### DeveloperStat

Fields:

- id
- profile
- algorithm
- spring
- database
- infra
- cs
- communication
- mental
- portfolio
- createdAt
- updatedAt

기본 스탯은 0으로 시작한다.

### Quest

Fields:

- id
- profile
- subCategory
- title
- description
- legacyQuestType
- difficulty
- status
- expReward
- createdAt
- completedAt
- updatedAt

### GrowthCategory

Fields:

- id
- name
- displayName
- description
- sortOrder
- createdAt
- updatedAt

### GrowthSubCategory

Fields:

- id
- category
- name
- displayName
- description
- totalExp
- level
- sortOrder
- createdAt
- updatedAt

### GrowthLog

Fields:

- id
- profile
- quest
- subCategory
- expGained
- message
- createdAt

### Schedule

Fields:

- id
- profile
- relatedGrowthSubCategory
- relatedQuest
- title
- description
- startDateTime
- endDateTime
- scheduleType
- status
- completedAt
- createdAt
- updatedAt

Schedule 완료는 EXP를 지급하지 않는다. EXP와 GrowthLog는 기존처럼 Quest 완료에서만 반영한다.

### ScheduleType

Fields:

- id
- name
- code
- description
- color
- sortOrder
- active
- createdAt
- updatedAt

ScheduleType은 enum이 아니라 DB Entity로 관리한다.
사용자는 `/schedule-types` 화면에서 추가, 수정, 비활성화할 수 있다.
타입 삭제는 물리 삭제하지 않고 `active=false`로 soft delete 처리한다.
Schedule 등록/수정 화면에서는 `active=true`인 ScheduleType만 선택 목록으로 보여준다.

### DailyReview

Fields:

- id
- profile
- reviewDate
- learnedText
- difficultyText
- tomorrowPlanText
- moodScore
- relatedGrowthSubCategory
- createdAt
- updatedAt

DailyReview는 하루에 하나만 작성한다.
DailyReview 작성은 EXP를 지급하지 않고 GrowthLog를 생성하지 않는다.
기분 점수는 1~5 정수로 관리한다.
MVP에서는 하나의 GrowthSubCategory만 선택적으로 연결한다.

### WeeklyReport

MVP에서는 저장형 Entity를 만들지 않는다.

`WeeklyReportService`가 Quest, GrowthLog, DailyReview를 월요일~일요일 기준으로 조회해 View DTO로 조립한다.

조회 데이터:

- 주간 완료 Quest
- 주간 GrowthLog
- Category/SubCategory별 주간 EXP
- Daily Review 목록
- 평균 moodScore

추후 AI 요약이나 과거 리포트 고정 보관이 필요하면 `WeeklyReportSnapshot` 계열 Entity를 추가한다.

### Monster

Fields:

- id
- profile
- name
- code
- description
- monsterType
- status
- difficulty
- maxHp
- currentHp
- rewardExp
- relatedGrowthCategory
- relatedGrowthSubCategory
- createdAt
- updatedAt
- defeatedAt

Monster System은 재미 요소이며 Quest 완료/GrowthLog 핵심 성장 로직을 복잡하게 만들지 않는다.
이번 MVP에서는 Monster CRUD, 수동 공격, HP 0 이하 시 `DEFEATED`, 보관 시 `ARCHIVED` 처리만 한다.
Monster 처치 보상 EXP는 표시용이며 실제 Profile EXP나 GrowthLog에 반영하지 않는다.
Quest 완료나 DailyReview 작성과 자동 연동하지 않는다.

### ScheduleStatus

Enum:

- PLANNED
- IN_PROGRESS
- DONE
- CANCELED

### MonsterType

Enum:

- CAREER_ANXIETY
- SPEC_CHANGE
- BUG
- LEGACY_CODE
- COMMUNICATION
- ENVIRONMENT
- STUDY_BLOCKER
- ETC

### MonsterStatus

Enum:

- ACTIVE
- WEAKENED
- DEFEATED
- ARCHIVED

### MonsterDifficulty

Enum:

- EASY
- NORMAL
- HARD
- BOSS

### QuestStatus

Enum:

- READY
- COMPLETED
- CANCELED

### Difficulty

Enum:

- EASY
- NORMAL
- HARD
- BOSS

EXP reward:

- EASY: `10`
- NORMAL: `30`
- HARD: `60`
- BOSS: `100`

## Level Rule

100 EXP = 1 Level

If `exp >= 100`:

- `level += exp / 100`
- `exp = exp % 100`

## Quest Completion Rule

Quest 완료 시 하나의 트랜잭션에서 처리한다.

1. Quest 조회
2. 이미 완료된 Quest인지 검증
3. Quest status를 `COMPLETED`로 변경
4. `completedAt` 저장
5. Profile exp 증가
6. Quest에 연결된 GrowthSubCategory exp 증가
7. GrowthLog 저장
8. Level up 여부 계산
9. 결과 메시지 반환

## UI Requirements

Dashboard URL:

- `/dashboard`

Dashboard에는 아래 내용을 보여준다.

- 캐릭터 말풍선
- 닉네임
- 캐릭터 이름
- 현재 타이틀
- Level
- EXP progress bar
- GrowthCategory/GrowthSubCategory 성장 카드
- 전체 성장 요약
- SubCategory 진행률
- 최근 GrowthLog
- 최근 완료 Quest
- Quest list
- Quest complete button
- Add quest button

Dashboard 데이터는 Controller에서 직접 조립하지 않고 `DashboardService` 중심으로 구성한다. Controller는 model attribute 연결과 기본 메시지 처리 정도만 담당한다.

UI는 카드형, 둥근 모서리, 가벼운 색감, 모바일에서도 보기 좋은 레이아웃을 지향한다.

## Pages

필수 페이지:

- `/dashboard`
- `/quests`
- `/quests/new`
- `/growth-categories`
- `/schedules`
- `/schedules/new`
- `/schedules/{id}/edit`
- `/daily-reviews`
- `/daily-reviews/new`
- `/daily-reviews/{id}`
- `/daily-reviews/{id}/edit`
- `/weekly-reports`
- `/weekly-reports/current`
- `/monsters`
- `/monsters/new`
- `/monsters/{id}`
- `/monsters/{id}/edit`

Action:

- `POST /quests`
- `POST /quests/{id}/complete`
- `POST /growth-categories`
- `POST /growth-categories/{id}/edit`
- `POST /growth-categories/{id}/delete`
- `POST /growth-categories/{categoryId}/sub-categories`
- `POST /growth-sub-categories/{id}/edit`
- `POST /growth-sub-categories/{id}/delete`
- `POST /schedules`
- `POST /schedules/{id}/edit`
- `POST /schedules/{id}/done`
- `POST /schedules/{id}/delete`
- `POST /schedule-types`
- `POST /schedule-types/{id}/edit`
- `POST /schedule-types/{id}/delete`
- `POST /daily-reviews`
- `POST /daily-reviews/{id}/edit`
- `POST /daily-reviews/{id}/delete`
- `POST /monsters`
- `POST /monsters/{id}/edit`
- `POST /monsters/{id}/attack`
- `POST /monsters/{id}/archive`

## Package Structure

feature-based package structure를 사용한다.

예시:

```text
com.devgwon.growthsimulator
├── character
│   ├── domain
│   ├── repository
│   ├── service
│   └── web
├── quest
│   ├── domain
│   ├── repository
│   ├── service
│   └── web
├── dashboard
│   ├── service
│   └── web
├── growth
│   ├── domain
│   ├── repository
│   ├── service
│   └── web
├── schedule
│   ├── domain
│   ├── repository
│   ├── service
│   └── web
├── review
│   ├── domain
│   ├── repository
│   ├── service
│   └── web
├── weeklyreport
│   ├── service
│   └── web
├── monster
│   ├── domain
│   ├── repository
│   ├── service
│   └── web
└── global
    ├── config
    ├── exception
    └── common
```

## Coding Rules

- Controller에 비즈니스 로직을 넣지 않는다.
- Dashboard 화면 데이터 조립은 `DashboardService`에서 처리한다.
- Quest 완료 로직은 Service에서 처리한다.
- Quest 완료는 `@Transactional`로 처리한다.
- 사용자에게 보이는 메시지는 한국어로 작성한다.
- 코드 식별자는 영어를 사용한다.
- 첫 버전은 과하게 추상화하지 않는다.
- 동작하는 작은 MVP를 우선 만든다.

## Git Rules

- git add, commit, push 같은 git 변경 작업은 개발자가 명시적으로 요청했을 때만 진행한다.
- git에 올리기 전 DB 계정, 비밀번호, API Key, 토큰 같은 민감 정보가 포함되지 않았는지 확인한다.
- 로컬 실행용 민감 설정은 커밋하지 않는다.

## README Requirements

README에는 아래 내용을 포함한다.

1. 프로젝트 소개
2. 기술 스택
3. 로컬 실행 방법
4. PostgreSQL 연결 설정
5. WSL + Docker 환경에서 확인할 점
6. 주요 기능
7. MVP 범위
8. 추후 확장 아이디어

## First Development Goal

첫 번째 목표는 아래 흐름이 동작하는 것이다.

1. `/dashboard` 접속
2. 캐릭터 말풍선, Level, EXP, 성장판, Quest 목록 확인
3. Quest 등록
4. Quest 완료
5. Profile EXP와 GrowthSubCategory EXP 증가
6. EXP가 100 이상이면 Level up

## Current Status

2026-05-14 기준으로 Spring Boot + Thymeleaf MVP와 GrowthCategory 기반 성장판이 구현되어 있다.

생성된 주요 파일:

- `settings.gradle`
- `build.gradle`
- `README.md`
- `src/main/resources/application.yml`
- `src/main/java/com/devgwon/growthsimulator/GrowthSimulatorApplication.java`
- `src/main/java/com/devgwon/growthsimulator/character/domain/DeveloperProfile.java`
- `src/main/java/com/devgwon/growthsimulator/character/domain/DeveloperStat.java`
- `src/main/java/com/devgwon/growthsimulator/quest/domain/Quest.java`
- `src/main/java/com/devgwon/growthsimulator/quest/domain/QuestType.java`
- `src/main/java/com/devgwon/growthsimulator/quest/domain/QuestStatus.java`
- `src/main/java/com/devgwon/growthsimulator/quest/domain/Difficulty.java`
- `gradlew`
- `gradlew.bat`
- `gradle/wrapper/gradle-wrapper.jar`
- `gradle/wrapper/gradle-wrapper.properties`
- Repository, Service, Controller 기본 구조
- Thymeleaf 기본 화면: `/dashboard`, `/quests`, `/quests/new`
- Bootstrap CDN 기반 기본 CSS
- `src/main/java/com/devgwon/growthsimulator/dashboard/service/DashboardService.java`
- `src/main/java/com/devgwon/growthsimulator/schedule/domain/Schedule.java`
- `src/main/java/com/devgwon/growthsimulator/schedule/domain/ScheduleType.java`
- `src/main/java/com/devgwon/growthsimulator/schedule/domain/ScheduleStatus.java`
- `src/main/java/com/devgwon/growthsimulator/schedule/repository/ScheduleRepository.java`
- `src/main/java/com/devgwon/growthsimulator/schedule/service/ScheduleService.java`
- `src/main/java/com/devgwon/growthsimulator/schedule/web/ScheduleController.java`
- `src/main/resources/templates/schedules/list.html`
- `src/main/resources/templates/schedules/form.html`
- `src/main/java/com/devgwon/growthsimulator/review/domain/DailyReview.java`
- `src/main/java/com/devgwon/growthsimulator/review/repository/DailyReviewRepository.java`
- `src/main/java/com/devgwon/growthsimulator/review/service/DailyReviewService.java`
- `src/main/java/com/devgwon/growthsimulator/review/web/DailyReviewController.java`
- `src/main/resources/templates/daily-reviews/list.html`
- `src/main/resources/templates/daily-reviews/form.html`
- `src/main/resources/templates/daily-reviews/detail.html`
- `src/main/java/com/devgwon/growthsimulator/weeklyreport/service/WeeklyReportService.java`
- `src/main/java/com/devgwon/growthsimulator/weeklyreport/web/WeeklyReportController.java`
- `src/main/resources/templates/weekly-reports/detail.html`
- `src/main/java/com/devgwon/growthsimulator/monster/domain/Monster.java`
- `src/main/java/com/devgwon/growthsimulator/monster/domain/MonsterType.java`
- `src/main/java/com/devgwon/growthsimulator/monster/domain/MonsterStatus.java`
- `src/main/java/com/devgwon/growthsimulator/monster/domain/MonsterDifficulty.java`
- `src/main/java/com/devgwon/growthsimulator/monster/repository/MonsterRepository.java`
- `src/main/java/com/devgwon/growthsimulator/monster/service/MonsterService.java`
- `src/main/java/com/devgwon/growthsimulator/monster/web/MonsterController.java`
- `src/main/resources/templates/monsters/list.html`
- `src/main/resources/templates/monsters/form.html`
- `src/main/resources/templates/monsters/detail.html`
- Dashboard 전용 View DTO:
  - `DashboardView`
  - `ProfileProgressView`
  - `GrowthSummaryView`
  - `GrowthCategorySectionView`
  - `GrowthSubCategoryCardView`
  - `GrowthLogSummaryView`
  - `QuestSummaryView`
  - `DashboardScheduleSummary`
  - `DailyReviewSummaryView`
  - `DashboardWeeklyReportSummary`
  - `DashboardMonsterSummary`

현재 구현된 흐름:

1. `/dashboard` 접속 시 기본 DeveloperProfile과 DeveloperStat을 생성하거나 조회한다.
2. Dashboard에서 캐릭터 말풍선, Level, EXP, 전체 성장 요약, 성장 지도, 최근 GrowthLog, 최근 완료 Quest, Quest 목록을 보여준다.
3. `/quests/new`에서 GrowthSubCategory를 선택해 Quest를 등록할 수 있다.
4. `/quests`와 `/dashboard`에서 Quest 목록을 볼 수 있다.
5. Quest 완료 버튼은 Quest 상태를 `COMPLETED`로 변경하고 Profile EXP, GrowthSubCategory EXP, GrowthLog를 반영한다.
6. GrowthCategory/GrowthSubCategory 기반 성장 지도와 관리 화면이 추가되어 있다.
7. Quest 등록은 GrowthSubCategory를 선택하고, Quest 완료 시 GrowthSubCategory EXP와 GrowthLog가 반영된다.
8. `/growth-categories`에서 성장 대분류/중분류를 추가, 수정, 삭제할 수 있다.
9. Dashboard 데이터는 `DashboardService`에서 조립하고 Controller는 가볍게 유지한다.
10. SubCategory 진행률은 `totalExp % 100` 기준으로 표시한다.
11. `/schedules`에서 일정 목록, 오늘 일정, 이번 주 일정을 볼 수 있다.
12. `/schedules/new`에서 일정을 등록할 수 있다.
13. `/schedules/{id}/edit`에서 일정을 수정할 수 있다.
14. 일정 완료와 삭제를 처리할 수 있다.
15. Dashboard에서 오늘 예정 일정 개수, 이번 주 예정 일정 개수, 오늘 일정 3개를 보여준다.
16. Schedule 완료는 EXP/GrowthLog를 발생시키지 않고, 성장 보상은 Quest 완료 흐름에만 남겨두었다.
17. `/daily-reviews`에서 Daily Review 목록을 볼 수 있다.
18. `/daily-reviews/new`에서 오늘 회고를 작성할 수 있다.
19. Daily Review는 날짜별 하루 1개만 작성할 수 있다.
20. Daily Review는 기분 점수 1~5와 관련 GrowthSubCategory를 저장할 수 있다.
21. Daily Review 작성은 EXP/GrowthLog를 발생시키지 않는다.
22. Dashboard에서 오늘 회고 작성 여부와 최근 Daily Review를 보여준다.
23. `/weekly-reports`에서 조회형 주간 리포트를 볼 수 있다.
24. Weekly Report는 완료 Quest, GrowthLog, DailyReview를 월요일~일요일 기준으로 요약한다.
25. Dashboard에서 이번 주 완료 Quest 수와 획득 EXP 요약을 보여준다.
26. `/monsters`에서 Monster 목록을 볼 수 있다.
27. `/monsters/new`에서 Monster를 등록할 수 있다.
28. Monster 상세 화면에서 수동 공격으로 HP를 줄일 수 있다.
29. Monster HP가 0 이하가 되면 `DEFEATED`가 되고, 보관하면 `ARCHIVED`가 된다.
30. Monster 처치 보상 EXP는 표시용이며 실제 GrowthLog/Profile EXP에는 반영하지 않는다.
31. Dashboard에서 Monster 전투 수, 처치 직전 Monster, 최근 처치 Monster를 보여준다.

아직 구현하지 않은 핵심 로직:

- Quest 완료 트랜잭션 테스트
- Daily Review Controller/Form 통합 테스트
- Daily Review 기반 AI Coach
- Weekly Report snapshot 저장
- Quest 완료와 Monster 자동 HP 감소 연동
- Monster 처치 보상 EXP 실제 반영

검증 상태:

- Java 21은 작업 환경에 설치되어 있다.
- Gradle Wrapper를 추가했다.
- `./gradlew test` 실행 결과 빌드가 성공했다.
- `/dashboard`, `/schedules`, `/schedules/new`, `/daily-reviews`, `/daily-reviews/new`, `/weekly-reports`, `/monsters`, `/monsters/new` 렌더링을 로컬 실행 후 HTTP 200으로 확인했다.
- Schedule 등록, 완료, 삭제 POST 흐름을 수동 확인했다.
- Daily Review 생성/삭제 POST 흐름을 수동 확인했다.
- GrowthCategoryService, GrowthSubCategoryService, DashboardService, ScheduleService, ScheduleTypeService, DailyReviewService, WeeklyReportService, MonsterService 단위 테스트를 추가했다.

다음 개발 목표:

1. `QuestService.completeQuest` 트랜잭션 테스트 추가
2. Daily Review Controller/Form 통합 테스트 추가
3. 완료 메시지와 Dashboard flash message 흐름 보강
4. Monster Controller/Form 통합 테스트 추가
5. Error Museum 구현 계획 수립
