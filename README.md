# Developer Growth Simulator

Developer Growth Simulator는 개발자의 공부, 업무, 회고, 이직 준비를 RPG처럼 관리하는 가벼운 웹 앱입니다.

매일 퀘스트를 등록하고 완료하면서 EXP를 얻고, 레벨과 성장 항목을 키웁니다. 단순 TODO 앱이 아니라 백둥이의 말풍선, 퀘스트 보드, 성장판이 있는 개발자 성장 시뮬레이터를 목표로 합니다.

## 기술 스택

- Java 21
- Spring Boot 3.x
- Spring MVC
- Spring Data JPA
- PostgreSQL
- Thymeleaf
- Bootstrap CDN
- Gradle Wrapper

첫 버전에서는 React를 사용하지 않습니다.

## 실행 전 확인사항

이 프로젝트는 로컬 Windows + WSL + Docker Desktop 환경을 기준으로 실행합니다.

- Docker Desktop이 실행 중이어야 합니다.
- PostgreSQL은 Docker 컨테이너로 이미 실행 중이라고 가정합니다.
- 새 `docker-compose.yml`은 만들지 않습니다.
- 기존 PostgreSQL 컨테이너의 DB, 계정, 비밀번호, 포트는 사용자 환경에 따라 다를 수 있습니다.
- DB 접속 정보는 개인 환경에 맞게 직접 설정해야 합니다. README에는 실제 ID나 비밀번호를 기록하지 않습니다.

Java 확인:

```bash
java -version
```

Gradle은 별도 설치가 필요 없습니다. 프로젝트에 포함된 Gradle Wrapper를 사용합니다.

## Docker PostgreSQL 컨테이너 확인 명령어

실행 중인 컨테이너를 확인합니다.

```bash
docker ps
```

PostgreSQL 컨테이너가 보이고, DB 포트가 로컬에 노출되어 있는지 확인합니다.

이후 명령어의 `<postgres_container>` 부분은 실제 컨테이너 이름으로 바꿔서 실행하세요.

## PostgreSQL 접속 확인 명령어

접속 확인:

```bash
docker exec -it <postgres_container> psql -U <db_username> -d <db_name>
```

접속에 성공하면 `psql` 프롬프트가 표시됩니다.

## DB 생성 예시

만약 애플리케이션에서 사용할 데이터베이스가 없다면 PostgreSQL에 접속한 뒤 DB를 생성합니다.

먼저 기본 DB나 `postgres` DB로 접속합니다.

```bash
docker exec -it <postgres_container> psql -U <db_username> -d postgres
```

`psql` 프롬프트에서 실행:

```sql
CREATE DATABASE <db_name>;
```

생성 후 접속 확인:

```bash
docker exec -it <postgres_container> psql -U <db_username> -d <db_name>
```

계정 자체가 없거나 권한이 부족한 경우에는 컨테이너의 PostgreSQL 초기 설정에 맞춰 사용자 생성 또는 권한 부여가 필요합니다.

## application.yml 수정 방법

DB 연결 정보는 [src/main/resources/application.yml](/home/jaekw/lab/codex/devgwon/pro/growth-simulator/src/main/resources/application.yml)에 있습니다.

설정 예시:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://<db_host>:<db_port>/<db_name>
    username: <db_username>
    password: <db_password>
    driver-class-name: org.postgresql.Driver
```

환경이 다르면 아래 값을 수정하세요.

- `<db_host>`, `<db_port>`, `<db_name>`, `<db_username>`, `<db_password>`를 로컬 환경에 맞게 변경합니다.
- 실제 비밀번호는 README나 커밋에 남기지 말고, 로컬 설정 파일 또는 환경 변수로 관리하는 것을 권장합니다.

## Spring Boot 실행 방법

빌드 확인:

```bash
./gradlew test
```

애플리케이션 실행:

```bash
./gradlew bootRun
```

Windows PowerShell에서 실행하는 경우:

```powershell
.\gradlew.bat bootRun
```

실행 중 DB 연결 오류가 발생하면 Docker 컨테이너, DB 이름, 계정, 비밀번호, 포트를 먼저 확인하세요.

## 접속 URL

애플리케이션 실행 후 브라우저에서 접속합니다.

```text
http://localhost:8080/dashboard
```

주요 화면:

- `GET /dashboard`: 대시보드
- `GET /quests`: 퀘스트 목록
- `GET /quests/new`: 퀘스트 등록
- `POST /quests`: 퀘스트 등록 처리
- `POST /quests/{id}/complete`: 퀘스트 완료 처리
- `GET /schedules`: 일정 목록
- `GET /schedules/new`: 일정 등록
- `GET /schedules/{id}/edit`: 일정 수정
- `POST /schedules`: 일정 등록 처리
- `POST /schedules/{id}/edit`: 일정 수정 처리
- `POST /schedules/{id}/done`: 일정 완료 처리
- `POST /schedules/{id}/delete`: 일정 삭제 처리
- `GET /schedule-types`: 일정 타입 관리
- `GET /schedule-types/new`: 일정 타입 추가
- `GET /schedule-types/{id}/edit`: 일정 타입 수정
- `GET /daily-reviews`: Daily Review 목록
- `GET /daily-reviews/new`: Daily Review 작성
- `GET /daily-reviews/{id}`: Daily Review 상세
- `GET /daily-reviews/{id}/edit`: Daily Review 수정
- `GET /weekly-reports`: 이번 주 Weekly Report
- `GET /weekly-reports/current`: 이번 주 Weekly Report
- `GET /weekly-reports?weekStart=YYYY-MM-DD`: 특정 주차 Weekly Report
- `GET /monsters`: Monster 목록
- `GET /monsters/new`: Monster 등록
- `GET /monsters/{id}`: Monster 상세
- `GET /monsters/{id}/edit`: Monster 수정
- `POST /monsters/{id}/attack`: Monster 수동 공격
- `POST /monsters/{id}/archive`: Monster 보관
- `GET /errors`: Error Museum 목록, 상태 필터, 검색
- `GET /errors/new`: 에러 기록 등록
- `POST /errors`: 에러 기록 등록 처리
- `GET /errors/{id}`: 에러 기록 상세
- `GET /errors/{id}/edit`: 에러 기록 수정
- `POST /errors/{id}/edit`: 에러 기록 수정 처리
- `POST /errors/{id}/resolve`: 에러 해결 처리
- `POST /errors/{id}/archive`: 에러 보관
- `POST /errors/{id}/delete`: 에러 삭제
- `GET /growth-categories`: 성장 대분류/중분류 관리
- `GET /growth-categories/new`: 성장 대분류 추가
- `GET /growth-categories/{id}/edit`: 성장 대분류 수정
- `GET /growth-categories/{categoryId}/sub-categories/new`: 성장 중분류 추가
- `GET /growth-sub-categories/{id}/edit`: 성장 중분류 수정

## 주요 기능

- 기본 DeveloperProfile 생성/조회
- 기본 DeveloperStat 생성/조회
- Quest 등록
- Quest 목록 조회
- Quest 완료
- Quest 완료 시 EXP 증가
- Quest 완료 시 GrowthSubCategory EXP 증가
- Quest 완료 시 GrowthLog 저장
- EXP 100 이상이면 Level up
- Dashboard 화면
- Dashboard 앱 홈형 게임 로비 UI
- 공통 게임형 네비게이션
- GrowthCategory/GrowthSubCategory 기반 성장 지도
- 전체 성장 요약
- Category별 성장 카드
- SubCategory 진행률
- 최근 GrowthLog
- 최근 완료 Quest
- 내부 일정 관리
- 오늘/이번 주 일정 요약
- 일정 등록, 수정, 완료, 삭제
- 일정 타입 추가, 수정, 비활성화
- Daily Review 작성, 조회, 수정, 삭제
- Daily Review 기분 점수 기록
- Daily Review와 GrowthSubCategory 연결
- Dashboard 오늘 회고 작성 여부 표시
- Dashboard 최근 Daily Review 표시
- Weekly Report 조회형 주간 성장 요약
- Weekly Report 완료 Quest/GrowthLog/DailyReview 요약
- Dashboard Weekly Report 바로가기와 주간 요약 표시
- Monster 등록, 조회, 수정, 수동 공격, 보관
- Monster HP progress, 상태, 난이도 표시
- HP 0 이하 시 Monster 처치 처리
- Dashboard Monster 전투 요약 표시
- Error Museum 에러 기록 등록, 조회, 수정, 삭제
- Error Museum 상태 필터와 키워드 검색
- Error Museum 해결/보관 처리
- Dashboard 최근 ErrorRecord 요약 표시
- Dashboard에서 Error Museum 작성 화면 바로가기
- 성장 대분류/중분류 추가, 수정, 삭제
- 백둥이 캐릭터 말풍선 메시지

## MVP 범위

첫 번째 MVP 목표:

1. `/dashboard` 접속
2. 캐릭터 말풍선, Level, EXP, 성장판, Quest 목록 확인
3. Quest 등록
4. Quest 완료
5. Profile EXP와 GrowthSubCategory EXP 증가
6. EXP가 100 이상이면 Level up

아직 구현하지 않는 기능:

- AI API 연동
- Job Class 전직 시스템
- 로그인/회원가입
- 멀티 유저
- React 프론트엔드
- 복잡한 차트

## 패키지 구조

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
├── errorrecord
│   ├── domain
│   ├── repository
│   ├── service
│   └── web
└── global
    ├── config
    ├── exception
    └── common
```

## 문제 해결

### `docker ps`에서 PostgreSQL 컨테이너가 보이지 않는 경우

Docker Desktop이 실행 중인지 확인하세요. 컨테이너 이름은 개인 환경마다 다를 수 있으므로 실제 컨테이너 이름으로 명령어를 수정해야 합니다.

```bash
docker ps
```

### `database "<db_name>" does not exist`

DB가 아직 생성되지 않은 상태입니다. PostgreSQL에 접속한 뒤 생성합니다.

```bash
docker exec -it <postgres_container> psql -U <db_username> -d postgres
```

```sql
CREATE DATABASE <db_name>;
```

### `password authentication failed`

`application.yml`의 `username`, `password`가 실제 PostgreSQL 계정과 다릅니다. 컨테이너 생성 시 사용한 계정 정보에 맞게 수정하세요.

### `Connection refused`

확인할 내용:

- PostgreSQL 컨테이너가 실행 중인지
- 컨테이너가 `5432` 포트를 로컬로 노출하는지
- `application.yml`의 host/port가 맞는지
- WSL에서 `localhost:5432`로 접근 가능한지

### `./gradlew: Permission denied`

WSL에서 실행 권한을 부여합니다.

```bash
chmod +x ./gradlew
```

### 포트 8080이 이미 사용 중인 경우

[src/main/resources/application.yml](/home/jaekw/lab/codex/devgwon/pro/growth-simulator/src/main/resources/application.yml)의 서버 포트를 변경합니다.

```yaml
server:
  port: 8081
```

## 추후 확장 아이디어

- Quest 완료 트랜잭션 테스트 보강
- 캐릭터 말풍선 메시지 다양화
- 직무 전직 시스템
- AI 기반 회고 코멘트
- Daily Review와 ScheduleType.REVIEW 연결
- Daily Review 기반 AI Coach
- Weekly Report snapshot 저장
- Monster와 Quest 완료 자동 연동
- Monster 처치 보상 EXP 실제 반영
- Error Museum 태그/다대다 연결
- Blog Draft Generator와 Error Museum 연결

## 현재 구현 상태

현재 Dashboard는 `DashboardService`에서 화면 데이터를 조립하고, Thymeleaf + `game-ui.css` 기반 앱 홈형 게임 로비로 표시합니다.

- 기본 프로필 조회 또는 생성
- Profile Level/EXP 진행률 계산
- GrowthCategory sortOrder 기준 조회
- GrowthSubCategory sortOrder 기준 조회
- 전체 성장 요약 계산
- Category별 성장 카드 구성
- SubCategory별 `totalExp % 100` 기준 진행률 표시
- 최근 GrowthLog 표시
- 최근 완료 Quest 표시
- Dashboard Hero에서 백둥이 말풍선, 추천 액션, Level/EXP 표시
- Dashboard 루틴 chip으로 Quest, 일정, 회고, Error, Monster 요약 표시
- Dashboard 빠른 이동 메뉴로 Quest, Schedule, Monster, Error Museum, Daily Review, Weekly Report 이동
- 백둥이 기본 메시지와 Quest 완료 flash message 표시
- Schedule 목록/등록/수정/완료/삭제
- ScheduleType 목록/등록/수정/비활성화
- Daily Review 목록/작성/상세/수정/삭제
- Weekly Report 조회형 주간 리포트
- Monster 목록/등록/상세/수정/수동 공격/보관
- Error Museum 목록/등록/상세/수정/해결/보관/삭제
- Error Museum 상태 필터와 키워드 검색
- Dashboard 최근 ErrorRecord 3개 표시
- 공통 네비게이션 fragment 적용
- Schedule, Daily Review, Weekly Report, Growth Settings 화면에 Dashboard 계열 헤더 톤 적용

검증한 내용:

```bash
./gradlew test
```

`/dashboard`, `/schedules`, `/schedules/new`, `/daily-reviews`, `/daily-reviews/new`, `/weekly-reports`, `/growth-categories`, `/monsters`, `/monsters/new`, `/errors` 렌더링은 로컬 Spring Boot 실행 후 HTTP 200으로 확인했습니다.
