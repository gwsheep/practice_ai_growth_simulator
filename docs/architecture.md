# Architecture

## 기술 구조

- Java 21
- Spring Boot 3.3.x
- Spring MVC
- Spring Data JPA
- PostgreSQL
- Thymeleaf
- Bootstrap CDN
- Lombok
- Gradle Wrapper

현재 저장소에는 `package.json`, Vite, TypeScript 설정이 없다. 첫 버전에서는 React를 사용하지 않는다.

## Java 패키지 구조

현재 Java 코드는 feature-based 패키지 대신 Spring Boot 백엔드에서 익숙한 layer-based 패키지로 정리한다.

```text
src/main/java/com/devgwon/growthsimulator
├── GrowthSimulatorApplication.java
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
```

테스트 코드는 Service 테스트와 Controller/Form 통합 테스트를 함께 둔다.

```text
src/test/java/com/devgwon/growthsimulator
├── controller
└── service
```

## 리소스 구조

```text
src/main/resources
├── application-example.yml
├── application.yml          # 로컬 전용, Git 추적 제외
├── static
│   ├── css
│   └── js
└── templates
    ├── ai-coach
    ├── blog-drafts
    ├── dashboard
    ├── daily-reviews
    ├── errors
    ├── fragments
    ├── growth-categories
    ├── growth-sub-categories
    ├── monsters
    ├── quests
    ├── schedule-types
    ├── schedules
    └── weekly-reports
```

## 패키지 역할

- `controller`: Spring MVC Controller. 요청 매핑, form binding, redirect, Model attribute 연결만 담당한다.
- `service`: 비즈니스 로직, 트랜잭션, Entity 조회/저장 흐름, View DTO 조립을 담당한다.
- `repository`: Spring Data JPA Repository. DB 접근 메서드만 둔다.
- `entity`: JPA Entity와 enum. DB 매핑과 도메인 상태 변경 메서드를 둔다.
- `dto/request`: 화면 form 또는 요청 입력 객체. 예: `QuestCreateRequest`, `ScheduleUpdateRequest`.
- `dto/response`: 화면 View DTO, summary DTO, result DTO. 예: `DashboardView`, `QuestCompleteResult`.
- `config`: Spring 설정과 seed data initializer.
- `exception`: 전역 예외 처리와 custom exception.
- `common`: 전역 공통 타입. 상태를 가지는 비즈니스 로직은 두지 않는다.

Lombok 사용 기준은 [lombok-policy.md](lombok-policy.md)를 따른다.

## 의존 방향

권장 의존 방향:

```text
controller -> service -> repository -> entity
controller -> dto/request, dto/response
service -> dto/request, dto/response, entity
repository -> entity
config -> service/repository/entity
exception -> exception 대상 타입
```

금지하거나 피해야 하는 방향:

- `controller -> repository` 직접 호출 금지
- `repository -> service` 의존 금지
- `entity -> controller/service/dto` 의존 금지
- `dto -> service/repository` 의존 금지
- `service` 패키지에 request/response DTO 배치 금지

## 데이터 흐름

일반 CRUD 흐름:

1. Controller가 요청을 받는다.
2. Controller는 request DTO 또는 form 객체를 Service에 전달한다.
3. Service가 Repository를 통해 Entity를 조회/저장한다.
4. Service가 필요한 response/View DTO를 조립한다.
5. Controller는 Model에 DTO를 넣고 Thymeleaf template을 반환한다.

Quest 완료 흐름:

1. Quest 조회
2. 이미 완료된 Quest인지 검증
3. Quest status를 `COMPLETED`로 변경
4. `completedAt` 저장
5. Profile exp 증가
6. Quest에 연결된 GrowthSubCategory exp 증가
7. GrowthLog 저장
8. Level up 여부 계산
9. 결과 메시지 반환

AI Coach Fake Client MVP 흐름:

1. Controller가 `/ai-coach` 요청을 받는다.
2. Controller는 `AiCoachService`에서 화면 데이터를 조회한다.
3. Service는 DailyReview, Quest, GrowthLog 데이터를 최소 범위로 조회한다.
4. Service는 `AiCoachClient` 인터페이스를 통해 규칙 기반 코칭 응답을 요청한다.
5. 현재 구현체는 외부 API를 호출하지 않는 `FakeAiCoachClient`다.
6. Client 응답 실패 시 Service가 기본 백둥이 fallback 메시지를 사용한다.
7. Controller는 `AiCoachView`를 Model에 담아 Thymeleaf template을 반환한다.

## 화면 구조

- Dashboard는 `DashboardController`와 `DashboardService`를 중심으로 동작한다.
- Dashboard template은 `templates/dashboard/index.html`이다.
- 공통 네비게이션은 `templates/fragments/navigation.html`을 사용한다.
- Quest, Schedule, Daily Review, Weekly Report, Monster, Error Museum, Growth Settings는 각 전용 template 디렉토리를 가진다.
- AI Coach 화면은 `templates/ai-coach/index.html`을 사용한다.
- Blog Draft Generator 화면은 `templates/blog-drafts/index.html`을 사용한다.
- Blog Draft Generator는 저장형 Entity 없이 `BlogDraftController -> BlogDraftService -> BlogDraftGenerator` 흐름으로 Markdown 초안을 조립한다.
- Blog Draft Generator는 실제 AI API, 외부 네트워크 호출, API Key, OpenAI 의존성을 사용하지 않는다.

## 새 파일 위치 기준

- 새 Controller: `controller`
- 새 Service: `service`
- 새 Repository: `repository`
- 새 JPA Entity 또는 enum: `entity`
- 새 form/create/update request 객체: `dto/request`
- 새 화면 표시용 View/Summary/Result DTO: `dto/response`
- Spring 설정, `ApplicationRunner` seed data: `config`
- custom exception 또는 `@ControllerAdvice`: `exception`
- 여러 계층에서 공유하는 상수/간단한 공통 타입: `common`
- 상태 없는 순수 보조 함수가 필요할 때: `util` 패키지 생성 검토

## 금지 규칙

- Controller에서 Repository를 직접 호출하지 않는다.
- AI Coach Controller에서 `AiCoachClient`를 직접 호출하지 않는다.
- Controller에서 트랜잭션성 비즈니스 로직을 처리하지 않는다.
- Service 패키지에 DTO, Form, View, Summary, Result 클래스를 새로 만들지 않는다.
- Entity를 새 API 응답이나 화면 응답 DTO 대신 직접 확장해서 사용하지 않는다.
- Entity에 Lombok `@Data`를 사용하지 않는다.
- DB 테이블 구조, URL, request field, response field는 구조 정리 중 임의로 변경하지 않는다.
- 대규모 구조 변경과 기능 변경을 한 커밋에 섞지 않는다.
- 민감 정보가 들어 있는 설정 파일을 커밋하지 않는다.
- 실제 AI API 호출, 외부 네트워크 호출, AI API Key 설정, AI 관련 의존성은 명시적 정책 확정 전에는 추가하지 않는다.

## 구조 변경 시 주의사항

- `DeveloperStat`은 현재 남아 있다. GrowthCategory/GrowthSubCategory 구조가 있어도 임의로 제거하지 않는다.
- Weekly Report는 저장형 Entity 없이 조회형 DTO로 조립한다.
- Monster 보상 EXP는 표시용이며 실제 Profile EXP나 GrowthLog에 반영하지 않는다.
- Schedule 완료는 EXP/GrowthLog를 발생시키지 않는다.
- Daily Review 작성은 EXP/GrowthLog를 발생시키지 않는다.
- Error Museum은 GrowthSubCategory 필수, Quest 선택 연결 구조를 유지한다.

## 확인 필요

- 현재 Thymeleaf 화면 중심 프로젝트라 `dto/response`에는 API response뿐 아니라 View DTO도 함께 둔다.
- 추후 REST API가 추가되면 `dto/response` 안에서 API 응답과 화면 View DTO를 더 나눌지 검토한다.
