package com.devgwon.growthsimulator.schedule.repository;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.schedule.domain.Schedule;
import com.devgwon.growthsimulator.schedule.domain.ScheduleStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByProfileOrderByStartDateTimeAsc(DeveloperProfile profile);

    List<Schedule> findByProfileAndStatusOrderByStartDateTimeAsc(DeveloperProfile profile, ScheduleStatus status);

    List<Schedule> findByProfileAndStartDateTimeBetweenOrderByStartDateTimeAsc(
            DeveloperProfile profile,
            LocalDateTime start,
            LocalDateTime end
    );

    long countByProfileAndStatusInAndStartDateTimeBetween(
            DeveloperProfile profile,
            Collection<ScheduleStatus> statuses,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Schedule> findTop3ByProfileAndStatusInAndStartDateTimeBetweenOrderByStartDateTimeAsc(
            DeveloperProfile profile,
            Collection<ScheduleStatus> statuses,
            LocalDateTime start,
            LocalDateTime end
    );
}
