# AGENTS.md

## 프로젝트 한 줄 요약

Developer Growth Simulator는 개발자의 공부, 업무, 회고, 이직 준비를 Quest/EXP/Level/GrowthCategory 기반으로 관리하는 Spring Boot + Thymeleaf 성장 시뮬레이터입니다.

## Codex 작업 원칙

- 기존 기능, URL, UI 흐름, 데이터 구조를 임의로 바꾸지 않습니다.
- 기능 개발 요청이 아니라 문서/하네스 작업인 경우에는 Java 코드, 템플릿, CSS, JS 동작을 변경하지 않습니다.
- Controller에 비즈니스 로직을 넣지 않고, Service 중심 구조를 유지합니다.
- Dashboard 데이터 조립은 `DashboardService` 중심으로 유지합니다.
- Quest 완료 로직은 Service에서 처리하고 트랜잭션 경계를 유지합니다.
- 사용자에게 보이는 메시지는 한국어로 작성하고, 코드 식별자는 영어로 작성합니다.
- 도메인 정책이 문서와 코드에서 확인되지 않으면 임의로 만들지 말고 `확인 필요` 또는 TODO로 표시합니다.
- 대규모 UI 변경은 한 번에 처리하지 말고 작고 검증 가능한 단위로 나눕니다.
- git add, commit, push는 사용자가 명시적으로 요청한 경우에만 수행합니다.
- DB 계정, 비밀번호, API Key, 토큰 같은 민감 정보는 새 문서나 커밋에 추가하지 않습니다.

## UI 톤앤매너

- 무거운 관리자 페이지보다 백둥이 캐릭터가 안내하는 가벼운 게임형 UI를 유지합니다.
- Dashboard는 전체 목록을 길게 보여주는 화면이 아니라 앱 홈형 게임 로비입니다.
- 상세 목록과 CRUD는 각 기능 화면에서 처리합니다.
- 둥근 모서리, 부드러운 색감, 모바일 대응, 캐릭터 말풍선, 성장판 느낌을 유지합니다.
- 특정 상용 서비스 디자인을 그대로 복제하지 않습니다.

자세한 UI 기준은 [docs/ui-policy.md](docs/ui-policy.md)를 따릅니다.

## 작업 전 반드시 읽을 문서

- [README.md](README.md): 프로젝트 소개와 실행 안내
- [docs/project-overview.md](docs/project-overview.md): 현재 구현 범위와 핵심 컨셉
- [docs/architecture.md](docs/architecture.md): 패키지 구조, 화면 구조, 작업 제약
- [docs/domain-policy.md](docs/domain-policy.md): 도메인 규칙과 미정 정책
- [docs/ui-policy.md](docs/ui-policy.md): 게임형 UI 방향
- [docs/verification.md](docs/verification.md): 빌드, 테스트, 수동 확인 방법
- [docs/roadmap.md](docs/roadmap.md): 진행 상태와 다음 작업 단위

## 작업 후 검증 체크리스트

- 변경 범위가 요청 범위를 벗어나지 않았는지 확인합니다.
- 기능 변경 시 `./gradlew test`를 실행합니다.
- 화면 변경 시 관련 URL을 로컬에서 열어 렌더링과 버튼 동작을 확인합니다.
- Quest 완료, EXP 증가, GrowthLog 저장, Level up처럼 핵심 성장 흐름을 건드린 경우 관련 테스트를 추가하거나 보강합니다.
- 문서 변경만 한 경우에도 링크, 경로, 완료 표시 규칙을 확인합니다.
- 실행하지 못한 검증은 최종 응답에 명확히 남깁니다.

## 문서 업데이트 규칙

- README.md에는 사람 개발자를 위한 짧은 소개, 실행 방법, 문서 링크만 둡니다.
- AGENTS.md에는 Codex 작업 규칙과 문서 목차만 둡니다.
- 세부 정책, 도메인 규칙, UI 방향, 검증 방법, 로드맵은 `/docs`에서 관리합니다.
- 기능을 추가하거나 정책을 바꾼 경우 관련 `/docs` 문서를 함께 갱신합니다.
- 문서와 코드가 다르면 단정하지 말고 `확인 필요`로 표시합니다.

## Roadmap 완료 표시 규칙

- 완료된 작업만 제목 우측에 `(완료)`를 붙입니다.
- 기존 ROADMAP.md에서 이미 `(완료)`가 붙어 있던 항목은 유지합니다.
- 완료 여부가 불확실한 항목에는 `(완료)`를 붙이지 않고 `확인 필요` 또는 TODO로 분리합니다.
- 루트 [ROADMAP.md](ROADMAP.md)는 안내 문서이며, 실제 로드맵은 [docs/roadmap.md](docs/roadmap.md)에서 관리합니다.
