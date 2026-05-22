# Prompt Guide

Codex에게 작업을 요청할 때는 변경 범위, 보존해야 할 동작, 검증 방법, 문서 업데이트 여부를 함께 적는다.

## 기능 추가용

```text
growth-simulator에 [기능명]을 추가해주세요.

반드시 먼저 README.md, AGENTS.md, docs/project-overview.md, docs/architecture.md, docs/domain-policy.md, docs/ui-policy.md, docs/verification.md, docs/roadmap.md를 읽고 작업해주세요.

요구사항:
- 기존 URL, UI 톤, Quest/EXP/GrowthLog 정책은 변경하지 마세요.
- Controller에 비즈니스 로직을 넣지 마세요.
- Service 중심으로 구현하고 필요한 테스트를 추가하세요.
- 사용자에게 보이는 메시지는 한국어로 작성하세요.
- 정책이 불명확한 부분은 임의로 만들지 말고 확인 필요로 남겨주세요.

검증:
- ./gradlew test
- 관련 화면 수동 확인

작업 후 관련 docs와 docs/roadmap.md를 업데이트해주세요.
```

## UI 수정용

```text
growth-simulator의 [화면명/URL] UI를 수정해주세요.

반드시 docs/ui-policy.md와 docs/architecture.md를 먼저 읽어주세요.

요구사항:
- 백둥이 중심의 가벼운 게임형 UI 톤을 유지하세요.
- Dashboard를 긴 관리 목록 화면으로 만들지 마세요.
- 기존 form action과 route는 변경하지 마세요.
- Bootstrap CDN, 기존 CSS, Thymeleaf 구조를 우선 사용하세요.
- 모바일에서 텍스트와 버튼이 겹치지 않게 확인하세요.

검증:
- 관련 URL 렌더링 확인
- 핵심 버튼 동작 확인
- 필요 시 ./gradlew test
```

## 리팩토링용

```text
growth-simulator의 [대상 모듈]을 리팩토링해주세요.

반드시 README.md, AGENTS.md, docs/architecture.md, docs/domain-policy.md, docs/verification.md를 먼저 읽어주세요.

요구사항:
- 동작 변경 없는 리팩토링으로 제한하세요.
- 기존 URL, Entity 필드, DB 정책을 바꾸지 마세요.
- Controller/Service/Repository 책임 분리를 유지하세요.
- 테스트가 부족한 핵심 흐름은 보강하세요.
- 큰 구조 변경은 작은 단계로 나눠주세요.

검증:
- ./gradlew test
- 관련 기능 수동 확인
```

## 문서 업데이트용

```text
growth-simulator 문서를 업데이트해주세요.

반드시 기존 README.md, AGENTS.md, docs/*, ROADMAP.md를 먼저 읽어주세요.

요구사항:
- 기존 중요한 내용은 삭제하지 말고 적절한 문서로 이동하거나 요약하세요.
- README.md는 사람 개발자용 대표 문서로 유지하세요.
- AGENTS.md는 Codex 작업 규칙과 문서 목차 중심으로 유지하세요.
- 세부 정책은 docs/에 정리하세요.
- 문서와 코드가 다르면 단정하지 말고 확인 필요로 표시하세요.
- 완료된 작업에만 docs/roadmap.md 제목 우측에 (완료)를 붙이세요.

검증:
- 링크와 경로 확인
- 중복 내용 축소
- 확인 필요 항목 분리
```

## Harness Engineering 작업용

```text
growth-simulator에 Harness Engineering 관점의 작업 기반을 보강해주세요.

목표:
- Codex가 다음 기능을 안정적으로 추가할 수 있도록 문서, 검증 체크리스트, 작업 규칙을 정리합니다.

원칙:
- 기능 코드와 UI 동작은 변경하지 마세요.
- README.md, AGENTS.md, docs/* 역할을 분리하세요.
- 실제 코드로 확인 가능한 내용과 확인 필요 내용을 구분하세요.
- 로드맵은 docs/roadmap.md에서 관리하고 완료된 항목만 (완료) 표시하세요.
```
