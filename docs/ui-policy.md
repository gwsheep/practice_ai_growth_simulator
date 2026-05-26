# UI Policy

## 방향

Developer Growth Simulator의 UI는 무거운 관리자 페이지가 아니라, 백둥이 캐릭터가 말풍선으로 안내하는 가벼운 게임형 성장 시뮬레이터를 지향한다.

핵심 느낌:

- 밝고 부드러운 성장 게임
- 매일 들어와도 부담 없는 앱 홈
- 목록을 길게 늘어놓는 관리 화면보다 요약과 빠른 이동 중심
- 캐릭터, 말풍선, Level, EXP, 성장판이 중심인 경험

## 백둥이 메시지 예시

- 오늘은 작은 퀘스트 하나만 깨도 충분해요.
- 코테를 풀었군요. 알고리즘 근육이 조금 붙었어요.
- 휴식도 성장입니다.
- 오늘도 출근한 것만으로 기본 EXP +5입니다.

## Dashboard 원칙

- Dashboard URL은 `/dashboard`다.
- `/`도 Dashboard로 연결된다.
- Dashboard는 앱 홈형 게임 로비로 유지한다.
- Dashboard에는 전체 목록을 길게 보여주지 않는다.
- 상세 목록과 CRUD는 각 기능 화면으로 분리한다.
- 최근/요약 데이터는 필요한 경우 최대 1~3개 중심으로 보여준다.

Dashboard 중심 정보:

- 캐릭터 말풍선
- 닉네임
- 캐릭터 이름
- 현재 타이틀
- Level
- EXP progress bar
- 오늘의 추천 행동
- 오늘의 루틴 chip
- Quest, Schedule, Monster, Error Museum, Daily Review, Weekly Report 빠른 이동 메뉴

## 공통 UI 원칙

- 둥근 모서리와 부드러운 색감을 사용한다.
- 모바일에서도 보기 좋은 레이아웃을 유지한다.
- 공통 navigation fragment를 사용한다.
- 각 기능 화면은 게임형 헤더 톤을 유지하되, 입력 폼은 명확하고 단순하게 둔다.
- Bootstrap CDN 기반 흐름을 유지한다.
- 과한 애니메이션은 피하고, Quest 완료/Level up 같은 중요한 순간에만 강조한다.

## 기능별 UI 기준

- Quest: 등록과 완료 액션이 명확해야 한다.
- Growth Settings: 성장 항목을 관리하되 관리자 페이지처럼 딱딱하게 만들지 않는다.
- Schedule: 오늘/이번 주 일정과 상태가 빠르게 보이게 한다.
- Daily Review: 하루 1회 작성 흐름이 가볍게 느껴져야 한다.
- Weekly Report: 데이터가 없어도 빈 화면 대신 안내 메시지를 제공한다.
- Monster: HP, 상태, 난이도가 전투장 느낌으로 보이게 한다.
- Error Museum: 실패 기록이 아니라 해결 수집품처럼 보이게 한다.
- AI Coach: 백둥이 말풍선, 최근 성장 요약, 최근 회고 요약, 응원 메시지, 다음 행동 추천을 가볍게 보여준다.

## 디자인 주의사항

- 특정 상용 서비스 디자인을 그대로 복제하지 않는다.
- React, Vue, Canvas, 복잡한 차트는 첫 버전에 도입하지 않는다.
- Dashboard를 긴 CRUD 목록 화면으로 되돌리지 않는다.
- 캐릭터와 말풍선은 기능 안내자이자 성장 피드백의 중심으로 유지한다.
- 새 UI를 추가할 때 기존 `game-ui.css`, `dashboard.css`, 공통 fragment와 충돌하지 않게 한다.
- AI Coach 화면은 실제 AI 채팅처럼 보이게 과장하지 않고, 현재 Fake/Rule 기반 MVP임을 전제로 간결한 코칭 화면으로 유지한다.

## 확인 필요

- 하단 네비게이션은 현재 문서 요구사항에는 표현되어 있으나 실제 구현은 공통 navigation fragment 중심이다. 하단 고정형 navigation으로 볼 수 있는지 확인 필요.
- Dynamic Character UI의 추가 상태 표현, 캐릭터 템플릿, 애니메이션 이벤트 모델은 아직 정책 확정 전이다.
