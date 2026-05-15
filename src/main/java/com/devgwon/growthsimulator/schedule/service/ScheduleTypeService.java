package com.devgwon.growthsimulator.schedule.service;

import com.devgwon.growthsimulator.schedule.domain.ScheduleType;
import com.devgwon.growthsimulator.schedule.repository.ScheduleTypeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduleTypeService {

    private final ScheduleTypeRepository scheduleTypeRepository;

    public ScheduleTypeService(ScheduleTypeRepository scheduleTypeRepository) {
        this.scheduleTypeRepository = scheduleTypeRepository;
    }

    @Transactional(readOnly = true)
    public List<ScheduleType> findAll() {
        return scheduleTypeRepository.findAllByOrderBySortOrderAscNameAsc();
    }

    @Transactional(readOnly = true)
    public ScheduleType get(Long id) {
        return scheduleTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일정 타입을 찾을 수 없습니다."));
    }

    @Transactional
    public ScheduleType create(ScheduleTypeForm form) {
        validate(form);
        scheduleTypeRepository.findByCode(form.normalizedCode())
                .ifPresent(scheduleType -> {
                    throw new IllegalArgumentException("이미 사용 중인 일정 타입 코드입니다.");
                });
        return scheduleTypeRepository.save(new ScheduleType(
                form.normalizedCode(),
                form.normalizedName(),
                form.normalizedDescription(),
                form.normalizedColor(),
                form.getSortOrder()
        ));
    }

    @Transactional
    public void update(Long id, ScheduleTypeForm form) {
        validate(form);
        ScheduleType scheduleType = get(id);
        scheduleTypeRepository.findByCode(form.normalizedCode())
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new IllegalArgumentException("이미 사용 중인 일정 타입 코드입니다.");
                });
        scheduleType.update(
                form.normalizedCode(),
                form.normalizedName(),
                form.normalizedDescription(),
                form.normalizedColor(),
                form.getSortOrder(),
                form.isActive()
        );
    }

    @Transactional
    public void deactivate(Long id) {
        get(id).deactivate();
    }

    private void validate(ScheduleTypeForm form) {
        if (form.normalizedCode().isBlank()) {
            throw new IllegalArgumentException("일정 타입 코드를 입력해 주세요.");
        }
        if (form.normalizedName().isBlank()) {
            throw new IllegalArgumentException("일정 타입 이름을 입력해 주세요.");
        }
    }
}
