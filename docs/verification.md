# Verification

## 로컬 실행 전 확인

기준 환경:

- Windows
- WSL
- Docker Desktop
- PostgreSQL Docker 컨테이너가 이미 실행 중
- 새 `docker-compose.yml`은 만들지 않음

Java 확인:

```bash
java -version
```

PostgreSQL 컨테이너 확인:

```bash
docker ps
```

PostgreSQL 접속 확인 예시:

```bash
docker exec -it <postgres_container> psql -U <db_username> -d <db_name>
```

DB가 없다면 PostgreSQL에 접속한 뒤 생성한다.

```sql
CREATE DATABASE <db_name>;
```

## 설정

DB 연결 정보는 로컬 전용 `src/main/resources/application.yml`에 둔다. 이 파일은 `.gitignore`에 포함되어 Git에 커밋하지 않는다.

예시 설정은 [src/main/resources/application-example.yml](src/main/resources/application-example.yml)에 있다.

환경에 맞게 아래 값을 확인한다.

- host
- port
- database
- username
- password

실제 비밀번호는 문서나 커밋에 남기지 않는다.

## 빌드와 테스트

테스트:

```bash
./gradlew test
```

애플리케이션 실행:

```bash
./gradlew bootRun
```

Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

접속:

```text
http://localhost:8080/dashboard
```

## 수동 화면 확인 체크리스트

기본 렌더링:

- `/dashboard`
- `/quests`
- `/quests/new`
- `/growth-categories`
- `/schedules`
- `/schedules/new`
- `/schedule-types`
- `/daily-reviews`
- `/daily-reviews/new`
- `/weekly-reports`
- `/weekly-reports/current`
- `/monsters`
- `/monsters/new`
- `/errors`
- `/errors/new`
- `/blog-drafts`
- `/ai-coach`

핵심 흐름:

- Quest 등록
- Quest 완료
- Profile EXP 증가 확인
- GrowthSubCategory EXP 증가 확인
- EXP 100 이상일 때 Level up 확인
- GrowthLog 생성 여부 확인
- Dashboard flash message 확인

보조 기능:

- Schedule 등록/수정/완료/삭제
- ScheduleType 등록/수정/비활성화
- Daily Review 작성/상세/수정/삭제
- Weekly Report 주간 요약 조회
- Monster 등록/수정/수동 공격/보관
- ErrorRecord 등록/수정/해결/보관/삭제
- Error Museum 상태 필터와 검색
- Blog Draft Generator DailyReview/ErrorRecord/GrowthLog 기반 Markdown 초안 생성
- AI Coach 최근 성장 요약, 회고 요약, 응원 메시지, 다음 행동 추천 표시

## 화면 변경 시 확인할 항목

- 모바일 폭에서 텍스트가 겹치지 않는지 확인한다.
- Dashboard가 긴 관리 목록 화면으로 변하지 않았는지 확인한다.
- 공통 navigation fragment가 깨지지 않는지 확인한다.
- 버튼과 form action URL이 Controller route와 맞는지 확인한다.
- 백둥이 말풍선, Level/EXP, 빠른 이동 메뉴가 정상 표시되는지 확인한다.

## 기능 변경 시 확인할 항목

- Controller에 비즈니스 로직이 들어가지 않았는지 확인한다.
- Service 트랜잭션 경계를 확인한다.
- 핵심 성장 흐름 변경 시 단위 테스트 또는 통합 테스트를 추가한다.
- EXP/GrowthLog 발생 정책이 [domain-policy.md](docs/domain-policy.md)와 맞는지 확인한다.
- 새 route를 추가했다면 README 또는 관련 docs에 링크를 반영한다.
- AI Coach 관련 변경 시 외부 API 호출, API Key 설정, AI 의존성이 추가되지 않았는지 확인한다.

## 문제 해결

`docker ps`에서 PostgreSQL 컨테이너가 보이지 않는 경우:

- Docker Desktop이 실행 중인지 확인한다.
- 컨테이너 이름은 개인 환경마다 다를 수 있으므로 실제 컨테이너 이름으로 명령어를 수정한다.

`database "<db_name>" does not exist`:

- DB가 아직 생성되지 않은 상태다.
- PostgreSQL에 접속한 뒤 `CREATE DATABASE <db_name>;`을 실행한다.

`password authentication failed`:

- `application.yml`의 `username`, `password`가 실제 PostgreSQL 계정과 다른지 확인한다.

`Connection refused`:

- PostgreSQL 컨테이너 실행 여부
- 컨테이너의 `5432` 포트 노출 여부
- `application.yml`의 host/port
- WSL에서 `localhost:5432` 접근 가능 여부

`./gradlew: Permission denied`:

```bash
chmod +x ./gradlew
```

포트 8080이 이미 사용 중인 경우:

```yaml
server:
  port: 8081
```

## 현재 검증 기록

기존 문서에는 아래 검증 기록이 있었다.

- Java 21 설치 확인
- Gradle Wrapper 추가
- `./gradlew test` 성공
- `/dashboard`, `/schedules`, `/schedules/new`, `/daily-reviews`, `/daily-reviews/new`, `/weekly-reports`, `/growth-categories`, `/monsters`, `/monsters/new`, `/errors` HTTP 200 확인
- Schedule 등록, 완료, 삭제 POST 흐름 수동 확인
- Daily Review 생성/삭제 POST 흐름 수동 확인
- 주요 Service 단위 테스트 추가
- Quest 완료 트랜잭션 테스트 추가
- Daily Review Controller/Form 통합 테스트 추가
- Monster Controller/Form 통합 테스트 추가
- Error Museum Controller/Form 통합 테스트 추가
- AI Coach Fake Client MVP Service/Controller 테스트 추가
- Blog Draft Generator MVP Service/Controller 테스트 추가

최근 문서 반영 시점의 검증 기록:

- `./gradlew clean test` 성공
- `./gradlew test --tests com.devgwon.growthsimulator.controller.ErrorRecordControllerTest` 성공
- `./gradlew test --tests com.devgwon.growthsimulator.service.BlogDraftServiceTest --tests com.devgwon.growthsimulator.controller.BlogDraftControllerTest` 성공
- `./gradlew bootRun` 후 `/blog-drafts` HTTP 200 확인
- 실행 중 `/usr/lib/jvm/openjdk-21` 경로가 유효하지 않다는 Gradle 경고가 표시되었으나 테스트는 통과함

이 기록은 과거 시점의 검증 결과다. 현재 상태에서 릴리스 또는 큰 변경 전에는 다시 실행해 확인한다.
