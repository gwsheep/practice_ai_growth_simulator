# Refactoring Notes

## 2026-05-22 Java 패키지 구조 정리

목적:

- 기능 추가 없이 Java/Spring 패키지 구조를 layer-based 구조로 정리했다.
- Service 패키지 안에 섞여 있던 request/form/view/summary DTO를 `dto/request`, `dto/response`로 분리했다.
- Controller의 직접 Repository 조회를 Service 호출로 이동했다.

## 이동한 패키지

- `*/web/*Controller.java` -> `controller`
- `*/domain/*.java` -> `entity`
- `*/repository/*.java` -> `repository`
- `*/service/*Service.java` -> `service`
- `*/service/*Request.java`, `*/service/*Form.java` -> `dto/request`
- `*/service/*View.java`, `*/service/*Summary.java`, `*/service/*Result.java`, `*/service/*Option.java` -> `dto/response`
- `global/config` -> `config`
- `global/exception` -> `exception`
- `global/common` -> `common`
- `*SeedDataInitializer.java` -> `config`
- Service 테스트 -> `src/test/java/com/devgwon/growthsimulator/service`

## 책임 정리

- Controller는 Repository를 직접 호출하지 않도록 조정했다.
- Growth/Schedule 선택 목록 조회는 `GrowthCategoryService`, `GrowthSubCategoryService`, `ScheduleTypeService`를 통해 접근하도록 바꿨다.
- URL, Thymeleaf template 경로, model attribute 이름, Entity 필드, DB 정책은 변경하지 않았다.

## 확인 필요

- 현재는 Thymeleaf 화면 중심이라 response DTO가 View DTO 역할도 한다.
- REST API가 추가되면 `dto/response`와 화면 전용 `dto/view` 분리 여부를 검토한다.
- Controller/Form 통합 테스트가 부족하므로 패키지 이동 이후 주요 화면 흐름 테스트를 보강한다.
