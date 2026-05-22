# Lombok Policy

## 목적

Lombok은 반복적인 생성자, getter, boilerplate 코드를 줄이기 위해 사용한다. 기능 로직, API 스펙, DB 구조를 바꾸는 용도로 사용하지 않는다.

## 의존성 관리

- Gradle `compileOnly`, `annotationProcessor`로 Lombok을 추가한다.
- 테스트에서도 `testCompileOnly`, `testAnnotationProcessor`를 사용한다.
- Spring Boot Gradle dependency management에 버전 관리를 맡기고, 특별한 이유가 없으면 Lombok 버전을 직접 고정하지 않는다.

## 계층별 권장 사용

Controller, Service, Component:

```java
@RequiredArgsConstructor
@Service
public class QuestService {
    private final QuestRepository questRepository;
}
```

- 생성자 주입은 `final` 필드와 `@RequiredArgsConstructor`를 기본으로 한다.
- 필드 주입은 사용하지 않는다.
- 생성자에 검증이나 변환 로직이 있으면 Lombok으로 바꾸지 않는다.

DTO:

```java
@Getter
public class QuestCreateRequest {
    private String title;
}
```

- DTO에는 필요한 annotation만 사용한다.
- Thymeleaf form binding에 setter 또는 no-args constructor가 필요한지 먼저 확인한다.
- 기존 getter/setter 동작이 명확하지 않으면 수동 코드를 유지한다.

Entity:

```java
@Entity
public class Quest {
    // Entity는 명시적인 생성자와 도메인 메서드를 우선한다.
}
```

- Entity에는 `@Data`를 사용하지 않는다.
- Entity에는 `@Setter`를 남발하지 않는다.
- 식별자, 연관관계, 도메인 상태 변경 메서드는 명시적으로 유지한다.

## 금지/주의 annotation

- `@Data`: Entity 금지. DTO에서도 필요성이 명확할 때만 검토한다.
- `@Setter`: Entity에는 기본적으로 금지. form DTO에 필요한 경우에만 제한적으로 사용한다.
- `@EqualsAndHashCode`: JPA Entity와 연관관계가 있는 클래스에서는 신중히 검토한다.
- `@ToString`: 양방향 연관관계나 lazy loading 대상에서는 사용하지 않는다.
- `@Builder`: 생성 규칙이 단순하고 테스트가 충분할 때만 적용한다.
- `@NoArgsConstructor`, `@AllArgsConstructor`: framework binding 요구가 명확할 때만 적용한다.

## 이번 적용 범위

- Lombok 의존성을 추가했다.
- 생성자 주입이 필요한 Controller, Service, Component에 `@RequiredArgsConstructor`를 적용했다.
- DTO와 Entity는 대규모 변환하지 않았다.

## 후속 TODO

- 단순 DTO의 getter/setter를 Lombok으로 바꿀지 파일별로 검토한다.
- Thymeleaf form binding DTO는 setter 필요 여부를 확인한 뒤 신중하게 적용한다.
- Entity는 Lombok 적용보다 명시적 도메인 메서드 유지가 우선이다.
