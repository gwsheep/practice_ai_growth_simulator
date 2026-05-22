package com.devgwon.growthsimulator.repository;

import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.Schedule;
import com.devgwon.growthsimulator.entity.ScheduleStatus;
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
