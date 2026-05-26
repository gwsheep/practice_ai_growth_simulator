# Project Overview

## 목적

Developer Growth Simulator는 개발자의 공부, 업무, 회고, 이직 준비를 RPG처럼 관리하는 가벼운 웹 앱이다.

사용자는 매일 Quest를 완료하고 EXP를 얻으며, Level up하고, GrowthCategory/GrowthSubCategory 기반 성장 항목을 키운다. 단순 TODO 앱이 아니라 백둥이의 말풍선, Quest 보드, 성장판, Monster, Error Museum이 있는 개발자 성장 시뮬레이터를 지향한다.

## 핵심 경험

- `/dashboard`에 접속하면 백둥이가 오늘의 행동을 안내한다.
- 사용자는 Quest를 등록하고 완료한다.
- Quest 완료 시 Profile EXP, GrowthSubCategory EXP, GrowthLog가 함께 반영된다.
- EXP가 100 이상이면 Level이 오른다.
- Schedule로 계획을 잡고, Daily Review로 하루를 회고하고, Weekly Report로 한 주를 확인한다.
- Monster는 스트레스와 문제를 게임 요소로 표현한다.
- Error Museum은 개발 중 만난 에러와 해결 과정을 성장 수집품처럼 저장한다.
- AI Coach는 Daily Review, Quest, GrowthLog를 기반으로 백둥이의 규칙 기반 코칭 메시지를 보여준다.

## 현재 구현된 주요 내용

실제 파일 구조 기준으로 아래 기능 파일과 화면이 존재한다.

- DeveloperProfile/DeveloperStat 기본 구조
- Quest 등록, 목록, 완료
- GrowthCategory/GrowthSubCategory/GrowthLog 기반 성장 구조
- Dashboard 앱 홈형 게임 로비
- Schedule와 ScheduleType 관리
- Daily Review 목록/작성/상세/수정/삭제
- 조회형 Weekly Report
- Monster 목록/등록/상세/수정/수동 공격/보관
- Error Museum 목록/등록/상세/수정/해결/보관/삭제
- AI Coach Fake Client MVP 화면
- 공통 게임형 네비게이션 fragment
- `game-ui.css`, `dashboard.css`, `dashboard.js`, `quest-form.js`

## 현재 화면 구성

확인된 Controller route 기준 주요 화면은 다음과 같다.

- `/` 및 `/dashboard`: Dashboard
- `/quests`, `/quests/new`: Quest 목록/등록
- `/growth-categories`: 성장 대분류/중분류 관리
- `/growth-categories/new`, `/growth-categories/{id}/edit`
- `/growth-categories/{categoryId}/sub-categories/new`
- `/growth-sub-categories/{id}/edit`
- `/schedules`, `/schedules/new`, `/schedules/{id}/edit`
- `/schedule-types`, `/schedule-types/new`, `/schedule-types/{id}/edit`
- `/daily-reviews`, `/daily-reviews/new`, `/daily-reviews/{id}`, `/daily-reviews/{id}/edit`
- `/weekly-reports`, `/weekly-reports/current`
- `/monsters`, `/monsters/new`, `/monsters/{id}`, `/monsters/{id}/edit`
- `/errors`, `/errors/new`, `/errors/{id}`, `/errors/{id}/edit`
- `/ai-coach`: 백둥이 AI Coach

## MVP 범위

첫 번째 MVP 핵심 흐름:

1. `/dashboard` 접속
2. 캐릭터 말풍선, Level, EXP, 성장판, Quest 목록 확인
3. Quest 등록
4. Quest 완료
5. Profile EXP와 GrowthSubCategory EXP 증가
6. EXP가 100 이상이면 Level up

MVP에서 제외하는 기능:

- 실제 AI API 연동
- Job Class 전직 시스템
- 로그인/회원가입
- 멀티 유저
- React 프론트엔드
- 복잡한 차트

AI Coach MVP는 현재 Fake/Rule 기반 메시지만 제공한다. 실제 AI API 호출, API Key 설정, 외부 네트워크 호출, 사용자 기록 외부 전송은 제외한다.

## 확인 필요

- README에 적혀 있던 수동 HTTP 200 검증 결과는 과거 기록이다. 현재 실행 환경에서 동일하게 재검증하려면 `./gradlew bootRun` 후 [verification.md](docs/verification.md)의 체크리스트를 따른다.
- 로컬 전용 `src/main/resources/application.yml`은 `.gitignore`에 포함되어 있다. 실제 커밋에는 placeholder 기반 [src/main/resources/application-example.yml](src/main/resources/application-example.yml)을 사용한다.
