package com.devgwon.growthsimulator.repository;

import com.devgwon.growthsimulator.entity.ScheduleType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleTypeRepository extends JpaRepository<ScheduleType, Long> {

    List<ScheduleType> findByActiveTrueOrderBySortOrderAscNameAsc();

    List<ScheduleType> findAllByOrderBySortOrderAscNameAsc();

    Optional<ScheduleType> findByCode(String code);
}
