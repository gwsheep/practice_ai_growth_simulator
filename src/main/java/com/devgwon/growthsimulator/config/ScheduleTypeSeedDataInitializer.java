package com.devgwon.growthsimulator.config;

import com.devgwon.growthsimulator.entity.ScheduleType;
import com.devgwon.growthsimulator.repository.ScheduleTypeRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class ScheduleTypeSeedDataInitializer implements ApplicationRunner {
    private final ScheduleTypeRepository scheduleTypeRepository;
    private final EntityManager entityManager;

    @Override

    @Transactional
    public void run(ApplicationArguments args) {
        seed("STUDY", "공부", "일반 학습 일정", "#4d78b8", 10);
        seed("ALGORITHM", "알고리즘", "코딩테스트와 알고리즘 풀이 일정", "#39a56a", 20);
        seed("SCHOOL", "방통대", "학교 과제와 시험 일정", "#7952b3", 30);
        seed("CAREER", "이직 준비", "이력서, 면접, 포트폴리오 준비 일정", "#ef6d58", 40);
        seed("REVIEW", "회고", "일간/주간 회고 일정", "#f0ad4e", 50);
        seed("PROJECT", "프로젝트", "개인 프로젝트와 사이드 프로젝트 일정", "#20a2b8", 60);
        seed("ETC", "기타", "기타 성장 관련 일정", "#647084", 70);
        migrateLegacyScheduleTypes();
    }

    private void seed(String code, String name, String description, String color, int sortOrder) {
        if (scheduleTypeRepository.findByCode(code).isPresent()) {
            return;
        }
        scheduleTypeRepository.save(new ScheduleType(code, name, description, color, sortOrder));
    }

    private void migrateLegacyScheduleTypes() {
        entityManager.createNativeQuery("""
                update schedule s
                   set schedule_type_id = st.id
                  from schedule_type st
                 where s.schedule_type_id is null
                   and s.schedule_type = st.code
                """)
                .executeUpdate();
    }
}
