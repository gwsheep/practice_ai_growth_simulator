# Developer Growth Simulator Roadmap

Developer Growth Simulator는 개발자의 공부, 업무, 회고, 이직 준비를 RPG처럼 관리하는 가벼운 성장 게임형 웹 앱이다.

사용자는 Quest를 완료하고 EXP를 얻으며, Level up하고, AI + 백엔드 개발자로서 성장한다. UI는 무거운 관리자 페이지가 아니라 백둥이가 말풍선으로 안내하는 밝고 가벼운 성장 시뮬레이터를 지향한다.

현재 핵심 구조는 `DeveloperProfile`, `Quest`, `GrowthCategory`, `GrowthSubCategory`, `GrowthLog`, `Schedule`, `DailyReview`, `WeeklyReport`, `Monster`, `ErrorRecord`, `CharacterMessageService`, `Dashboard`, `AiCoachService`, Thymeleaf 기반 UI, PostgreSQL 로컬 DB를 중심으로 한다.

성장 항목은 고정 필드가 아니라 `GrowthCategory` / `GrowthSubCategory` 기반으로 관리한다.

## 완료 표시 규칙

- 완료된 작업만 제목 우측에 `(완료)`를 붙인다.
- 기존 ROADMAP.md에서 제목 우측에 `(완료)`가 붙은 항목은 유지한다.
- 완료 여부가 불확실한 항목에는 `(완료)`를 붙이지 않는다.
- 문서와 코드가 다르거나 현재 실행으로 재검증하지 못한 내용은 `확인 필요`로 분리한다.

## 기본 성장 영역 (완료)

1. Backend: API 설계, 트랜잭션, 예외 처리, 배치, 메시징, 도메인 설계
2. Framework: Spring Boot, Spring MVC, JPA, Validation, AOP, Scheduler
3. Database: SQL, 인덱스, 실행계획, 락, 트랜잭션, PostgreSQL/MySQL/Altibase
4. Infra: Docker, Redis, Kafka, RabbitMQ, ELK, CI/CD, 서버, 네트워크 운영
5. Security: JWT, OAuth2, 인증/인가, 세션/쿠키, CORS, CSRF, 암호화, 권한
6. AI: Codex, OpenAI API, AI Agent, RAG, 프롬프트, AI 기능 설계
7. Algorithm: 코딩테스트, DFS/BFS, 정렬, 이분탐색, DP, 자료구조 문제풀이
8. CS: 운영체제, 네트워크, 컴퓨터 구조, HTTP, 프로세스/스레드, 메모리
9. System Design: MSA, Outbox, Circuit Breaker, Rate Limiting, Saga, 장애 대응, 확장성
10. Testing: JUnit, Mockito, 통합 테스트, Testcontainers, 테스트 전략
11. Career: 이력서, 포트폴리오, 블로그, 면접 답변, 커뮤니케이션
12. Mental: 휴식, 회고, 번아웃 방지, 꾸준함, 자기관리

## GrowthCategory / GrowthSubCategory 구조 안정화 (완료)

목적:

- 고정 Stat 필드 중심의 성장 구조를 DB 기반 성장 항목 구조로 전환한다.
- Quest가 `GrowthSubCategory`를 참조하고, Quest 완료 시 Profile EXP와 SubCategory EXP가 함께 성장하도록 만든다.

주요 기능:

- `GrowthCategory`, `GrowthSubCategory`, `GrowthLog` Entity 추가
- 기본 12개 성장 영역과 중분류 seed data 추가
- Quest 등록 시 대분류/중분류 선택
- Quest 완료 시 `GrowthSubCategory.totalExp` 증가
- SubCategory도 100 EXP 기준으로 level up
- Quest 완료 시 `GrowthLog` 저장
- `CharacterMessageService`가 SubCategory displayName 기반 메시지 반환

주의사항:

- 기존 `DeveloperStat`은 아직 삭제하지 않고 유지한다.
- Quest 완료 비즈니스 로직은 유지하면서 성장 항목 연결만 `GrowthSubCategory` 중심으로 점진 전환했다.

## Dashboard 성장판 개선 (완료)

목적:

- Dashboard를 단순 Quest 목록 화면이 아니라, 개발자의 성장 상태를 한눈에 보는 성장판으로 개선한다.
- 기존 캐릭터 UI와 Quest 완료 흐름은 유지하면서 `GrowthCategory` / `GrowthSubCategory` 기반 진행률을 보여준다.

주요 기능:

- `DashboardService` 중심 화면 데이터 조립
- `DashboardView`와 Dashboard 전용 View DTO 추가
- 전체 성장 요약 표시
- Category별 성장 카드 표시
- SubCategory별 level, totalExp, EXP progress bar 표시
- 최근 GrowthLog 표시
- 최근 완료 Quest 표시
- 기존 Quest 목록과 완료 버튼 유지
- 백둥이 메시지와 Quest 완료 결과 카드 유지

주의사항:

- Controller는 가볍게 유지하고 `DashboardService`에서 데이터를 조립한다.
- Quest 등록/완료 비즈니스 로직은 건드리지 않고, Thymeleaf + CSS 중심으로 화면만 개선했다.

## Schedule Management (완료)

목적:

- Quest와 별개로 공부 일정, 코딩테스트 계획, 방통대 시험 일정, 이직 준비 일정, 회고 일정 등을 등록한다.
- 사용자가 해야 할 일과 실제 성장 Quest를 연결할 수 있게 한다.

주요 기능:

- 일정 등록/목록/수정/완료/삭제
- 오늘 일정 조회
- 이번 주 일정 조회
- ScheduleType 관리
- ScheduleStatus 관리
- Dashboard 오늘/이번 주 일정 요약
- 시작/종료 일시 날짜와 시/분 입력 분리

주의사항:

- Google Calendar 연동, 반복 일정, 알림, 외부 캘린더 연동은 구현하지 않았다.
- Schedule 완료는 EXP를 지급하지 않고, EXP와 GrowthLog는 기존 Quest 완료 흐름에서만 반영한다.

## Daily Review (완료)

목적:

- 하루 동안 배운 것, 힘들었던 것, 내일 할 일, 기분 점수를 기록한다.
- 단순 성장 수치만으로는 남지 않는 맥락을 쌓고, AI Coach와 Weekly Report의 입력 데이터로 활용한다.

주요 기능:

- DailyReview Entity 추가
- 오늘 배운 것, 힘들었던 점, 내일 할 일 기록
- 기분 점수 1~5 입력
- 관련 GrowthSubCategory 1개 선택 연결
- 하루 1개 회고 중복 작성 제한
- 회고 목록/상세/수정/삭제
- Dashboard 오늘 회고 작성 여부 표시
- Dashboard 최근 Daily Review 표시

주의사항:

- AI 요약, Weekly Report snapshot, Monster System, Blog Draft Generator는 처음부터 붙이지 않았다.
- Mental EXP와 GrowthLog 반영도 이번 MVP에서는 제외했다.

## Weekly Report (완료)

목적:

- 한 주 동안 완료한 Quest, 성장한 SubCategory, Level up, 회고 내용을 요약해 사용자가 자신의 성장을 한눈에 볼 수 있게 한다.

주요 기능:

- 주간 완료 Quest 요약
- 주간 GrowthLog 요약
- Category별 주간 성장 요약
- 가장 많이 성장한 SubCategory 표시
- Daily Review 기반 회고 요약
- 평균 기분 점수 표시
- 카드형 주간 리포트 화면
- Dashboard Weekly Report 요약 표시
- `weekStart` 파라미터 기반 특정 주차 조회

주의사항:

- 저장형 리포트보다 조회형 요약으로 시작했다.
- AI 요약이나 리포트 보관 기능이 필요해지면 snapshot Entity를 추가한다.

## Monster System (완료)

목적:

- 개발자가 마주치는 스트레스, 장애, 요구사항 변경, 물경력 불안 같은 감정과 문제를 게임의 Monster로 표현한다.
- 막연한 불안을 구체적인 대상으로 바꾸고, 수동 공격을 통해 HP를 줄이며 처치했다는 감각을 주기 위한 기능이다.

주요 기능:

- Monster 등록/목록/상세/수정
- 몬스터 난이도, HP, 상태 관리
- 수동 공격
- HP progress bar 표시
- HP 0 이하 시 `DEFEATED` 처리
- `ARCHIVED` 보관 처리
- Dashboard Monster 요약 표시
- 보상 EXP 표시

주의사항:

- Quest 완료 로직에 몬스터 처리를 섞지 않았다.
- Monster 처치 보상 EXP는 표시용이며 실제 Profile EXP나 GrowthLog에는 반영하지 않는다.
- Quest 완료 시 자동 HP 감소, DailyReview 자동 연동, Monster 공격 로그는 추후 확장으로 남겼다.

## Error Museum (완료)

목적:

- 개발 중 만난 에러와 해결 과정을 기록한다.
- 에러를 실패 기록이 아니라 성장 수집품처럼 저장해, 나중에 같은 문제를 다시 만났을 때 빠르게 복기할 수 있게 한다.

주요 기능:

- ErrorRecord Entity 추가
- 에러 이름, 발생 상황, 원인, 해결 방법 기록
- 심각도 관리
- 상태 관리: OPEN, RESOLVED, ARCHIVED
- 관련 GrowthSubCategory 연결
- 관련 Quest 연결
- 목록/상세/등록/수정/삭제
- 해결 처리
- 보관 처리
- 상태 필터
- 키워드 검색
- Dashboard 최근 ErrorRecord 3개 표시

추후 확장 후보:

- ErrorTag
- ErrorGrowthLink
- ErrorQuestLink
- 자주 만난 에러 목록
- Blog Draft Generator와 연결

주의사항:

- 처음에는 제목, 상황, 해결 방법, 연결 성장 항목 중심으로 시작했다.
- GrowthLog 자동 생성, Monster 자동 연동, Blog Draft Generator, AI Coach 연동은 제외했다.

## Game Lobby / App Home UI 정리 (완료)

목적:

- Dashboard와 주요 기능 화면이 무거운 관리자 페이지처럼 보이지 않도록 정리한다.
- Dashboard는 전체 목록을 길게 보여주는 화면이 아니라, 백둥이가 안내하는 앱 홈형 게임 로비로 사용한다.
- 상세 관리 기능은 각 전용 화면으로 분리한다.

주요 기능:

- Dashboard Hero 영역 개선
- 백둥이 캐릭터와 말풍선 중심 배치
- Level/EXP 진행도 표시
- 오늘의 루틴 chip 표시
- Quest, Schedule, Monster, Error Museum, Daily Review, Weekly Report 빠른 이동 메뉴
- 공통 네비게이션 fragment 추가
- `game-ui.css` 공통 게임형 UI 스타일 추가
- Error Museum 목록/상세 카드형 UI 개선
- Monster 목록 전투장형 UI 개선
- Schedule, Daily Review, Weekly Report, Growth Settings 화면에 공통 헤더 톤 적용

주의사항:

- React, Vue, Canvas를 도입하지 않고 Thymeleaf, HTML, CSS, 최소 JavaScript로 구현했다.
- Entity와 핵심 비즈니스 로직은 변경하지 않고, 기존 URL을 유지했다.

## Growth Category Management (완료)

목적:

- GrowthCategory와 GrowthSubCategory를 웹 화면에서 추가/수정/삭제할 수 있게 한다.
- 기본 12개 성장 영역을 제공하되, 사용자가 자신의 성장 방향에 맞게 확장할 수 있도록 한다.

주요 기능:

- GrowthCategory 목록
- GrowthCategory 추가/수정/삭제
- GrowthSubCategory 목록
- GrowthSubCategory 추가/수정/삭제
- 연결된 Quest 또는 GrowthLog가 있으면 삭제 제한
- 성장 항목 편집 화면

주의사항:

- 관리자 페이지처럼 딱딱하게 만들지 않는다.
- 삭제는 반드시 연결된 Quest/GrowthLog를 확인한 뒤 제한한다.
- `totalExp`와 `level`은 성장 기록 값이므로 관리 화면에서 직접 수정하지 않는 것이 기본이다.

## Dynamic Character UI

목적:

- 현재 캐릭터를 더 살아 움직이는 오리지널 마스코트 백둥이로 발전시킨다.
- 사용자가 퀘스트를 완료하거나 레벨업할 때 캐릭터 반응을 보여줘 성장 게임 느낌을 강화한다.

현재 완료된 범위:

- Dashboard 캐릭터 영역을 mascot-zone 구조로 정리
- idle float 애니메이션
- hover wiggle 애니메이션
- Quest 완료 후 success bounce 애니메이션
- Level up 후 level-up pop 애니메이션
- 캐릭터 말풍선 UI
- `CharacterMessageService` 메시지 연동
- `dashboard.css`, `dashboard.js` 분리
- 모바일 반응형 처리

추가 후보:

- idle 애니메이션 고도화
- hover 애니메이션 고도화
- quest complete 애니메이션 고도화
- level up 애니메이션 고도화
- mental low 상태 표현
- CharacterMessageService와 메시지 연동 강화

예상 Entity:

- CharacterState
- CharacterMessageTemplate
- CharacterAnimationEvent

주의사항:

- React로 전환하지 않고 Thymeleaf, CSS animation, 간단한 JavaScript로 구현한다.
- 애니메이션은 과하면 피로해지므로 완료/레벨업 같은 중요한 순간에만 강조한다.

확인 필요:

- 기존 ROADMAP에서는 일부 UI 범위가 완료로 표시되어 있었지만 최상위 제목에는 `(완료)`가 없었다. 따라서 최상위 항목은 미완료/고도화 항목으로 유지한다.

## AI Coach

목적:

- Daily Review, Quest 기록, GrowthLog를 기반으로 AI가 응원 메시지, 퀘스트 추천, 회고 요약을 제공한다.
- 사용자가 혼자 공부한다는 느낌을 줄이고, 다음 행동을 가볍게 제안한다.

주요 기능 후보:

- 오늘의 응원 메시지 생성 (Fake Client MVP 완료)
- Quest 추천 (Fake Client MVP 완료)
- Daily Review 요약 (Fake Client MVP 완료)
- Weekly Report 코멘트
- 성장 정체 구간 안내
- FakeAiCoachClient 기반 초기 구현 (완료)
- OpenAI API 연동 확장

예상 Entity:

- AiCoachMessage
- AiCoachRequestLog
- AiCoachRecommendation

주의사항:

- 초기에는 `FakeAiCoachClient`로 시작한다. (완료)
- 현재 MVP는 실제 AI API를 호출하지 않고, 사용자 기록을 외부로 전송하지 않는다.
- 실제 AI API 연동 시에만 API Key 관리 방식을 별도 정책으로 확정한다.
- Controller에서 직접 API를 호출하지 않는다.
- AI 응답 실패 시에도 기본 백둥이 메시지로 fallback되어야 한다.

## Blog Draft Generator

목적:

- Daily Review, Error Museum, GrowthLog를 기반으로 블로그 초안을 생성한다.
- 사용자가 성장 기록을 외부 포트폴리오 자산으로 바꾸기 쉽게 돕는다.

주요 기능 후보:

- 템플릿 기반 블로그 초안 생성
- Error Museum 기반 문제 해결 글 초안
- Daily Review 기반 회고 글 초안
- GrowthLog 기반 학습 기록 요약
- AI Coach와 연결한 초안 개선
- Markdown 형태 출력

예상 Entity:

- BlogDraft
- BlogDraftSource
- BlogDraftStatus

주의사항:

- 처음부터 완성된 글을 생성하려고 하지 않는다.
- 초안을 만드는 기능으로 범위를 제한하고, 사용자가 수정할 수 있는 구조를 둔다.

## Job Class / Career Path

목적:

- 성장 상태에 따라 개발자 직업/타이틀이 바뀌도록 만들어 장기 목표를 제공한다.
- 주니어 백엔드에서 AI 백엔드 개발자로 성장하는 식의 RPG 전직 감각을 만든다.

주요 기능 후보:

- 직업/타이틀 목록 관리
- 전직 조건 정의
- GrowthSubCategory level 기반 조건
- 총 EXP 기반 조건
- 전직 가능 알림
- 전직 달성 시 캐릭터 메시지와 애니메이션
- 예시: 주니어 백엔드, 운영형 백엔드, MSA 탐험가, 금융권 도전자, AI 백엔드 개발자

예상 Entity:

- JobClass
- CareerPath
- JobClassRequirement
- ProfileJobClassHistory

주의사항:

- DeveloperProfile의 `title`과 충돌하지 않도록 한다.
- 처음에는 `title`을 덮어쓰기보다 별도 JobClass를 연결하고, 표시만 Dashboard에서 조합하는 방식이 안전하다.

## 다음 작업 후보

Codex가 한 번에 처리하기 좋은 단위로 쪼갠다.

1. Java/Spring layer-based 패키지 구조 정리 (완료)
2. Lombok 의존성 추가와 생성자 주입 정리 (완료)
3. `QuestService.completeQuest` 트랜잭션 테스트 추가 (완료)
4. Daily Review Controller/Form 통합 테스트 추가 (완료)
5. 완료 메시지와 Dashboard flash message 흐름 보강
6. Monster Controller/Form 통합 테스트 추가 (완료)
7. Error Museum Controller/Form 통합 테스트 추가
8. Error Museum 태그 정책 초안 작성
9. Blog Draft Generator 템플릿 기반 초안 설계
10. AI Coach Fake client 설계 (완료)
11. AI Coach Fake Client MVP 구현 (완료)
12. Dynamic Character UI 고도화 범위 확정
13. application.yml 민감 정보 처리 방식 정리 (완료)

## 구조 개선 TODO

- `dto/response`에 화면 View DTO와 result DTO가 함께 있다. REST API가 추가되면 `dto/view` 또는 `dto/internal` 분리 여부 확인 필요.
- Controller/Form 통합 테스트가 부족하다. 패키지 이동 이후 주요 화면 POST/redirect 흐름 테스트 보강 필요.
- `service` 패키지가 기능별 하위 패키지 없이 평면 구조다. 클래스가 더 늘어나면 `service/quest`, `service/schedule` 같은 가벼운 하위 분리 검토 필요.
- 단순 DTO의 Lombok 적용은 아직 전체 변환하지 않았다. Thymeleaf form binding 동작을 확인하며 파일별로 검토 필요.

<!-- 이전 번호 목록 보존용 메모: 아래 항목은 위 작은 작업 단위 목록으로 재정리했다. -->
<!--
1. `QuestService.completeQuest` 트랜잭션 테스트 추가
2. Daily Review Controller/Form 통합 테스트 추가
3. 완료 메시지와 Dashboard flash message 흐름 보강
4. Monster Controller/Form 통합 테스트 추가
5. Error Museum Controller/Form 통합 테스트 추가
6. Error Museum 태그 정책 초안 작성
7. Blog Draft Generator 템플릿 기반 초안 설계
8. AI Coach Fake client 설계
9. AI Coach Fake Client MVP 구현
10. Dynamic Character UI 고도화 범위 확정
11. application.yml 민감 정보 처리 방식 정리 (완료)
-->

## 확인 필요 항목

- 기존 수동 HTTP 200 검증 결과는 현재 실행으로 재검증 필요
- 로컬 `application.yml`의 실제 DB 값이 작업자 환경에서 올바른지 여부
- Dynamic Character UI 최상위 완료 여부
- 하단 네비게이션 요구와 현재 공통 navigation fragment 구현의 차이
- 실제 AI Coach API 연동 시 사용자 동의, 익명화, 저장 기간, API Key 관리 정책

## 추천 구현 순서

1. GrowthCategory / GrowthSubCategory 구조 안정화 (완료)
2. Dashboard 성장판 개선 (완료)
3. Schedule Management (완료)
4. Daily Review (완료)
5. Weekly Report (완료)
6. Monster System (완료)
7. Error Museum (완료)
8. Game Lobby / App Home UI 정리 (완료)
9. Quest 완료 트랜잭션 테스트 (완료)
10. Daily Review Controller/Form 통합 테스트 (완료)
11. Monster Controller/Form 통합 테스트 (완료)
12. AI Coach Fake Client MVP (완료)
13. Error Museum Controller/Form 통합 테스트
14. Blog Draft Generator
15. Job Class / Career Path
16. Dynamic Character UI 고도화
17. Growth Category Management 고도화

## 진행 메모

- GrowthCategory / GrowthSubCategory 기반 구조는 핵심 성장 모델이다.
- Quest 완료, GrowthLog, Dashboard 성장판은 이 구조 위에서 동작한다.
- Quest 완료 트랜잭션 테스트는 PostgreSQL 기반 `QuestServiceTransactionTest`에서 성공, 롤백, 중복 완료, 미존재 Quest를 검증한다.
- Daily Review Controller/Form 통합 테스트는 PostgreSQL 기반 `DailyReviewControllerTest`에서 목록/상세/수정 폼, 생성/수정/삭제 성공, 생성/수정 validation 실패 시 form 반환을 검증한다.
- Monster Controller/Form 통합 테스트는 PostgreSQL 기반 `MonsterControllerTest`에서 목록/상세/생성/수정 폼, 생성/수정 validation 실패 시 form 반환, 공격/처치/보관 흐름을 검증한다.
- AI Coach Fake Client MVP는 `/ai-coach`에서 DailyReview, Quest, GrowthLog 기반 규칙 메시지와 다음 행동 추천을 제공하며, 외부 AI API와 사용자 데이터 외부 전송은 사용하지 않는다.
- Dashboard 성장판은 전체 성장 요약, Category별 성장 카드, SubCategory 진행률, 최근 GrowthLog, 최근 완료 Quest를 표시한다.
- 현재 Dashboard는 앱 홈형 게임 로비로 정리되어 Hero, Level/EXP, 루틴 chip, 빠른 이동 메뉴 중심으로 동작한다.
- 전체 목록과 CRUD는 Quest, Schedule, Daily Review, Weekly Report, Monster, Error Museum, Growth Settings 각 전용 화면에서 처리한다.
- 확장 기능은 재미 요소보다 매일 쓰는 흐름을 먼저 만든다.
- 백둥이 캐릭터는 기능 안내자이자 성장 피드백의 중심으로 유지한다.
