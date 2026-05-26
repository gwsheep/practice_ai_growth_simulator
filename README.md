# Developer Growth Simulator

Developer Growth Simulator는 개발자의 공부, 업무, 회고, 이직 준비를 RPG처럼 관리하는 가벼운 웹 앱입니다.

사용자는 Quest를 등록하고 완료하면서 EXP를 얻고, Level과 GrowthCategory/GrowthSubCategory 기반 성장 항목을 키웁니다. 단순 TODO 앱이 아니라 백둥이 캐릭터, 말풍선, 성장판, Quest 보드가 있는 개발자 성장 시뮬레이터를 목표로 합니다.

## 핵심 컨셉

- 오늘 할 일을 `Quest`로 만들고 완료하면 EXP를 얻습니다.
- EXP가 100 이상이면 Level up이 발생합니다.
- 성장 항목은 `GrowthCategory`와 `GrowthSubCategory`로 관리합니다.
- Dashboard는 관리 목록 화면이 아니라 백둥이가 안내하는 앱 홈형 게임 로비입니다.
- Schedule, Daily Review, Weekly Report, Monster, Error Museum은 성장 기록을 돕는 보조 시스템입니다.

## 주요 기능

- DeveloperProfile 생성/조회와 Level/EXP 표시
- Quest 등록, 목록 조회, 완료 처리
- Quest 완료 시 Profile EXP, GrowthSubCategory EXP, GrowthLog 반영
- GrowthCategory/GrowthSubCategory 관리
- Dashboard 성장판과 백둥이 말풍선
- Schedule 및 ScheduleType 관리
- Daily Review 작성/조회/수정/삭제
- 조회형 Weekly Report
- Monster 등록/공격/보관
- Error Museum 기록/검색/해결/보관
- Fake/Rule 기반 백둥이 AI Coach 화면

AI Coach MVP는 실제 AI API를 호출하지 않으며, 사용자 기록을 외부로 전송하지 않습니다. 현재는 Daily Review, Quest, GrowthLog 기반 규칙 메시지만 제공합니다.

## 기술 스택

- Java 21
- Spring Boot 3.3.x
- Spring MVC
- Spring Data JPA
- PostgreSQL
- Thymeleaf
- Bootstrap CDN
- Lombok
- Gradle Wrapper

첫 버전에서는 React를 사용하지 않습니다. 현재 저장소에는 `package.json`, Vite, TypeScript 설정이 없습니다.

## 로컬 실행

이 프로젝트는 Windows + WSL + Docker Desktop 환경을 기준으로 로컬 실행합니다. PostgreSQL은 Docker 컨테이너로 이미 실행 중이라고 가정하며, 새 `docker-compose.yml`은 만들지 않습니다.

Java 확인:

```bash
java -version
```

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

접속 URL:

```text
http://localhost:8080/dashboard
```

## PostgreSQL 연결

DB 연결 정보는 로컬 전용 `src/main/resources/application.yml`에 둡니다. 이 파일은 `.gitignore`에 포함되어 Git에 커밋하지 않습니다.

예시는 [src/main/resources/application-example.yml](src/main/resources/application-example.yml)에 있습니다. 로컬에서 실행할 때 예시 파일을 참고해 `application.yml`을 만들고, 아래 항목을 개인 환경에 맞게 수정하세요.

- host: `localhost`
- port: `5432`
- database: `<db_name>`
- username: `<db_username>`
- password: `<db_password>`

실제 DB 계정, 비밀번호, API Key, 토큰은 문서나 커밋에 남기지 않는 것을 원칙으로 합니다.

PostgreSQL 컨테이너 확인:

```bash
docker ps
```

접속 확인 예시:

```bash
docker exec -it <postgres_container> psql -U <db_username> -d <db_name>
```

## 기본 디렉토리 구조

```text
src/main/java/com/devgwon/growthsimulator
├── common
├── config
├── controller
├── dto
│   ├── request
│   └── response
├── entity
├── exception
├── repository
└── service

src/main/resources
├── static
│   ├── css
│   └── js
└── templates
```

자세한 구조와 작업 규칙은 [docs/architecture.md](docs/architecture.md)를 참고하세요.

Lombok을 사용하므로 IDE에서 annotation processing을 활성화해야 할 수 있습니다. 자세한 사용 정책은 [docs/lombok-policy.md](docs/lombok-policy.md)를 참고하세요.

## 관련 문서

- [프로젝트 개요](docs/project-overview.md)
- [아키텍처와 작업 제약](docs/architecture.md)
- [Lombok 사용 정책](docs/lombok-policy.md)
- [도메인 정책](docs/domain-policy.md)
- [UI 정책](docs/ui-policy.md)
- [검증 방법](docs/verification.md)
- [로드맵](docs/roadmap.md)
- [Codex 프롬프트 가이드](docs/prompt-guide.md)
