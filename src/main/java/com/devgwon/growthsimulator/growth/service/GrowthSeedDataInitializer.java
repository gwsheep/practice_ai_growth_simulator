package com.devgwon.growthsimulator.growth.service;

import com.devgwon.growthsimulator.growth.domain.GrowthCategory;
import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.growth.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.growth.repository.GrowthSubCategoryRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GrowthSeedDataInitializer implements ApplicationRunner {

    private final GrowthCategoryRepository categoryRepository;
    private final GrowthSubCategoryRepository subCategoryRepository;

    public GrowthSeedDataInitializer(
            GrowthCategoryRepository categoryRepository,
            GrowthSubCategoryRepository subCategoryRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        for (CategorySeed seed : defaultSeeds()) {
            GrowthCategory category = categoryRepository.save(new GrowthCategory(
                    seed.name(),
                    seed.displayName(),
                    seed.description(),
                    seed.sortOrder()
            ));

            List<GrowthSubCategory> subCategories = new ArrayList<>();
            int subSortOrder = 1;
            for (SubCategorySeed subSeed : seed.subCategories()) {
                subCategories.add(new GrowthSubCategory(
                        category,
                        subSeed.name(),
                        subSeed.displayName(),
                        subSeed.displayName() + " 성장 항목",
                        subSortOrder++
                ));
            }
            subCategoryRepository.saveAll(subCategories);
        }
    }

    private List<CategorySeed> defaultSeeds() {
        return List.of(
                category(
                        "BACKEND",
                        "백엔드",
                        "API 설계, 트랜잭션, 예외 처리, 배치, 메시징, 도메인 설계",
                        1,
                        sub("API_DESIGN", "API 설계"),
                        sub("TRANSACTION", "트랜잭션"),
                        sub("EXCEPTION_HANDLING", "예외 처리"),
                        sub("BATCH", "배치"),
                        sub("MESSAGING", "메시징"),
                        sub("DOMAIN_DESIGN", "도메인 설계")
                ),
                category(
                        "FRAMEWORK",
                        "프레임워크",
                        "Spring Boot, Spring MVC, JPA, Validation, AOP, Scheduler",
                        2,
                        sub("SPRING_BOOT", "Spring Boot"),
                        sub("SPRING_MVC", "Spring MVC"),
                        sub("JPA", "JPA"),
                        sub("VALIDATION", "Validation"),
                        sub("AOP", "AOP"),
                        sub("SCHEDULER", "Scheduler")
                ),
                category(
                        "DATABASE",
                        "데이터베이스",
                        "SQL, 인덱스, 실행계획, 락, 트랜잭션, PostgreSQL/MySQL/Altibase",
                        3,
                        sub("SQL", "SQL"),
                        sub("INDEX", "인덱스"),
                        sub("EXECUTION_PLAN", "실행계획"),
                        sub("LOCK", "락"),
                        sub("TRANSACTION", "트랜잭션"),
                        sub("POSTGRESQL_MYSQL_ALTIBASE", "PostgreSQL/MySQL/Altibase")
                ),
                category(
                        "INFRA",
                        "인프라",
                        "Docker, Redis, Kafka, RabbitMQ, ELK, CI/CD, 서버, 네트워크 운영",
                        4,
                        sub("DOCKER", "Docker"),
                        sub("REDIS", "Redis"),
                        sub("KAFKA", "Kafka"),
                        sub("RABBITMQ", "RabbitMQ"),
                        sub("ELK", "ELK"),
                        sub("CI_CD", "CI/CD"),
                        sub("SERVER_OPERATION", "서버 운영"),
                        sub("NETWORK_OPERATION", "네트워크 운영")
                ),
                category(
                        "SECURITY",
                        "보안",
                        "JWT, OAuth2, 인증/인가, 세션/쿠키, CORS, CSRF, 암호화, 권한",
                        5,
                        sub("JWT", "JWT"),
                        sub("OAUTH2", "OAuth2"),
                        sub("AUTHENTICATION_AUTHORIZATION", "인증/인가"),
                        sub("SESSION_COOKIE", "세션/쿠키"),
                        sub("CORS", "CORS"),
                        sub("CSRF", "CSRF"),
                        sub("ENCRYPTION", "암호화"),
                        sub("PERMISSION", "권한")
                ),
                category(
                        "AI",
                        "AI",
                        "Codex, OpenAI API, AI Agent, RAG, 프롬프트, AI 기능 설계",
                        6,
                        sub("CODEX", "Codex"),
                        sub("OPENAI_API", "OpenAI API"),
                        sub("AI_AGENT", "AI Agent"),
                        sub("RAG", "RAG"),
                        sub("PROMPT", "프롬프트"),
                        sub("AI_FEATURE_DESIGN", "AI 기능 설계")
                ),
                category(
                        "ALGORITHM",
                        "알고리즘",
                        "코딩테스트, DFS/BFS, 정렬, 이분탐색, DP, 자료구조 문제풀이",
                        7,
                        sub("CODING_TEST", "코딩테스트"),
                        sub("DFS_BFS", "DFS/BFS"),
                        sub("SORTING", "정렬"),
                        sub("BINARY_SEARCH", "이분탐색"),
                        sub("DP", "DP"),
                        sub("DATA_STRUCTURE_PROBLEM_SOLVING", "자료구조 문제풀이")
                ),
                category(
                        "CS",
                        "CS",
                        "운영체제, 네트워크, 컴퓨터 구조, HTTP, 프로세스/스레드, 메모리",
                        8,
                        sub("OPERATING_SYSTEM", "운영체제"),
                        sub("NETWORK", "네트워크"),
                        sub("COMPUTER_ARCHITECTURE", "컴퓨터 구조"),
                        sub("HTTP", "HTTP"),
                        sub("PROCESS_THREAD", "프로세스/스레드"),
                        sub("MEMORY", "메모리")
                ),
                category(
                        "SYSTEM_DESIGN",
                        "시스템 설계",
                        "MSA, Outbox, Circuit Breaker, Rate Limiting, Saga, 장애 대응, 확장성",
                        9,
                        sub("MSA", "MSA"),
                        sub("OUTBOX", "Outbox"),
                        sub("CIRCUIT_BREAKER", "Circuit Breaker"),
                        sub("RATE_LIMITING", "Rate Limiting"),
                        sub("SAGA", "Saga"),
                        sub("FAILURE_RESPONSE", "장애 대응"),
                        sub("SCALABILITY", "확장성")
                ),
                category(
                        "TESTING",
                        "테스트",
                        "JUnit, Mockito, 통합 테스트, Testcontainers, 테스트 전략",
                        10,
                        sub("JUNIT", "JUnit"),
                        sub("MOCKITO", "Mockito"),
                        sub("INTEGRATION_TEST", "통합 테스트"),
                        sub("TESTCONTAINERS", "Testcontainers"),
                        sub("TEST_STRATEGY", "테스트 전략")
                ),
                category(
                        "CAREER",
                        "커리어",
                        "이력서, 포트폴리오, 블로그, 면접 답변, 커뮤니케이션",
                        11,
                        sub("RESUME", "이력서"),
                        sub("PORTFOLIO", "포트폴리오"),
                        sub("BLOG", "블로그"),
                        sub("INTERVIEW_ANSWER", "면접 답변"),
                        sub("COMMUNICATION", "커뮤니케이션")
                ),
                category(
                        "MENTAL",
                        "멘탈",
                        "휴식, 회고, 번아웃 방지, 꾸준함, 자기관리",
                        12,
                        sub("REST", "휴식"),
                        sub("RETROSPECTIVE", "회고"),
                        sub("BURNOUT_PREVENTION", "번아웃 방지"),
                        sub("CONSISTENCY", "꾸준함"),
                        sub("SELF_MANAGEMENT", "자기관리")
                )
        );
    }

    private CategorySeed category(
            String name,
            String displayName,
            String description,
            int sortOrder,
            SubCategorySeed... subCategories
    ) {
        return new CategorySeed(name, displayName, description, sortOrder, List.of(subCategories));
    }

    private SubCategorySeed sub(String name, String displayName) {
        return new SubCategorySeed(name, displayName);
    }

    private record CategorySeed(
            String name,
            String displayName,
            String description,
            int sortOrder,
            List<SubCategorySeed> subCategories
    ) {
    }

    private record SubCategorySeed(String name, String displayName) {
    }
}
