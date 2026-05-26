# Domain Policy

## 핵심 도메인 개념

Developer Growth Simulator는 개발자의 성장 활동을 게임 요소로 표현한다.

- DeveloperProfile: 사용자 캐릭터의 성장 상태
- DeveloperStat: 초기 Stat 구조. 현재도 유지한다.
- GrowthCategory: 성장 대분류
- GrowthSubCategory: 성장 중분류와 누적 EXP/Level
- Quest: 실제 성장 보상을 지급하는 작업 단위
- GrowthLog: Quest 완료로 생기는 성장 로그
- Schedule: 계획과 일정
- DailyReview: 하루 회고
- WeeklyReport: 한 주 조회형 성장 요약
- Monster: 스트레스, 장애, 불안, 문제를 게임형 대상으로 표현
- ErrorRecord: 개발 중 만난 에러와 해결 과정

## DeveloperProfile

기본 정책:

- 기본 level: `1`
- 기본 exp: `0`
- 기본 title: `주니어 백엔드`
- 기본 characterName: `백둥이`

Level rule:

```text
100 EXP = 1 Level

if exp >= 100:
  level += exp / 100
  exp = exp % 100
```

## Quest

Quest는 Profile EXP와 GrowthSubCategory EXP를 실제로 증가시키는 핵심 성장 단위다.

Difficulty별 EXP:

- `EASY`: 10
- `NORMAL`: 30
- `HARD`: 60
- `BOSS`: 100

QuestStatus:

- `READY`
- `COMPLETED`
- `CANCELED`

완료 정책:

- 완료 로직은 하나의 트랜잭션에서 처리한다.
- 이미 완료된 Quest는 다시 완료할 수 없다.
- Quest 완료 시 Profile EXP, GrowthSubCategory EXP, GrowthLog를 함께 반영한다.
- 사용자에게 반환되는 완료 메시지는 한국어로 작성한다.

## Growth

GrowthCategory/GrowthSubCategory는 고정 Stat 필드가 아니라 DB 기반 성장 항목 구조다.

기본 성장 영역으로 문서화된 항목:

1. Backend
2. Framework
3. Database
4. Infra
5. Security
6. AI
7. Algorithm
8. CS
9. System Design
10. Testing
11. Career
12. Mental

정책:

- SubCategory 진행률은 `totalExp % 100` 기준으로 표시한다.
- GrowthSubCategory도 100 EXP 기준 Level up한다.
- 연결된 Quest 또는 GrowthLog가 있으면 GrowthCategory/GrowthSubCategory 삭제를 제한한다.
- `totalExp`와 `level`은 성장 기록 값이므로 관리 화면에서 직접 수정하지 않는 것이 기본이다.

## Schedule

Schedule은 계획 관리 기능이다.

ScheduleStatus:

- `PLANNED`
- `IN_PROGRESS`
- `DONE`
- `CANCELED`

정책:

- Schedule 완료는 EXP를 지급하지 않는다.
- Schedule 완료는 GrowthLog를 생성하지 않는다.
- EXP와 GrowthLog는 기존처럼 Quest 완료에서만 반영한다.
- 반복 일정, 알림, 외부 캘린더 연동은 현재 범위에서 제외한다.

## ScheduleType

ScheduleType은 enum이 아니라 DB Entity로 관리한다.

정책:

- `/schedule-types` 화면에서 추가, 수정, 비활성화할 수 있다.
- 타입 삭제는 물리 삭제하지 않고 `active=false`로 soft delete 처리한다.
- Schedule 등록/수정 화면에서는 `active=true`인 ScheduleType만 선택 목록으로 보여준다.

## DailyReview

DailyReview는 하루 동안 배운 것, 힘들었던 것, 내일 할 일, 기분 점수를 기록한다.

정책:

- 하루에 하나만 작성한다.
- 작성은 EXP를 지급하지 않는다.
- 작성은 GrowthLog를 생성하지 않는다.
- moodScore는 1~5 정수로 관리한다.
- MVP에서는 하나의 GrowthSubCategory만 선택적으로 연결한다.

## WeeklyReport

MVP에서는 저장형 Entity를 만들지 않는다.

정책:

- `WeeklyReportService`가 Quest, GrowthLog, DailyReview를 월요일~일요일 기준으로 조회해 View DTO로 조립한다.
- 조회 데이터는 주간 완료 Quest, 주간 GrowthLog, Category/SubCategory별 주간 EXP, Daily Review 목록, 평균 moodScore다.
- AI 요약이나 과거 리포트 고정 보관이 필요하면 `WeeklyReportSnapshot` 계열 Entity를 추가한다.

## Monster

Monster는 재미 요소이며 Quest 완료/GrowthLog 핵심 성장 로직을 복잡하게 만들지 않는다.

MonsterType:

- `CAREER_ANXIETY`
- `SPEC_CHANGE`
- `BUG`
- `LEGACY_CODE`
- `COMMUNICATION`
- `ENVIRONMENT`
- `STUDY_BLOCKER`
- `ETC`

MonsterStatus:

- `ACTIVE`
- `WEAKENED`
- `DEFEATED`
- `ARCHIVED`

MonsterDifficulty:

- `EASY`
- `NORMAL`
- `HARD`
- `BOSS`

정책:

- MVP에서는 Monster CRUD, 수동 공격, HP 0 이하 시 `DEFEATED`, 보관 시 `ARCHIVED` 처리만 한다.
- Monster 처치 보상 EXP는 표시용이다.
- Monster 처치 보상 EXP는 실제 Profile EXP나 GrowthLog에 반영하지 않는다.
- Quest 완료나 DailyReview 작성과 자동 연동하지 않는다.

## Error Museum

Error Museum은 개발 중 만난 에러와 해결 과정을 성장 수집품처럼 저장하는 기능이다.

ErrorStatus:

- `OPEN`
- `RESOLVED`
- `ARCHIVED`

ErrorSeverity:

- `LOW`
- `MEDIUM`
- `HIGH`
- `CRITICAL`

정책:

- GrowthSubCategory는 필수로 연결한다.
- Quest는 선택 연결로 둔다.
- 해결 처리 시 `resolvedAt`을 자동 기록한다.
- ErrorTag, ErrorGrowthLink, ErrorQuestLink 같은 별도 Entity는 아직 만들지 않는다.
- GrowthLog 자동 생성, Monster 자동 연동, AI Coach/Blog Draft Generator 연동은 현재 범위에서 제외한다.

## AI Coach

AI Coach는 DailyReview, Quest, GrowthLog를 기반으로 백둥이의 코칭 메시지와 다음 행동 추천을 제공한다.

MVP 정책:

- 실제 AI API를 호출하지 않는다.
- OpenAI API Key, token, 외부 네트워크 호출, OpenAI 관련 의존성을 추가하지 않는다.
- 사용자 DailyReview/Quest/GrowthLog 데이터를 외부로 전송하지 않는다.
- `AiCoachClient` 인터페이스를 두고, 현재 구현은 `FakeAiCoachClient`의 규칙 기반 메시지만 사용한다.
- Controller는 `AiCoachClient`를 직접 호출하지 않고 `AiCoachService`를 통해 화면 데이터를 조회한다.
- Fake client 실패 시 기본 백둥이 메시지로 fallback한다.
- MVP에서는 AiCoachMessage 같은 저장형 Entity를 만들지 않고 조회형 화면으로 시작한다.

확인 필요:

- 향후 실제 AI API 연동 시 사용자 기록 전송 동의와 익명화 정책
- AI Coach 메시지 저장 여부와 보관 기간
- 추천 Quest를 실제 Quest 생성 흐름과 연결할지 여부

## 정책 확인 필요

- AI Coach의 실제 AI 연동 시 Entity와 저장 정책
- Job Class/Career Path가 DeveloperProfile.title과 공존하는 방식
- Blog Draft Generator의 저장형 Entity 범위
- Dynamic Character UI의 CharacterState/CharacterMessageTemplate 도입 여부
- Monster 보상 EXP를 실제 성장 EXP로 반영할지 여부
- Error Museum 태그/다대다 연결 도입 시점
