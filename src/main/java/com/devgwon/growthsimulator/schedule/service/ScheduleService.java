package com.devgwon.growthsimulator.schedule.service;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.character.service.DeveloperProfileService;
import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.growth.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.schedule.domain.Schedule;
import com.devgwon.growthsimulator.schedule.domain.ScheduleStatus;
import com.devgwon.growthsimulator.schedule.domain.ScheduleType;
import com.devgwon.growthsimulator.schedule.repository.ScheduleRepository;
import com.devgwon.growthsimulator.schedule.repository.ScheduleTypeRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleTypeRepository scheduleTypeRepository;
    private final DeveloperProfileService profileService;
    private final GrowthSubCategoryRepository subCategoryRepository;

    public ScheduleService(
            ScheduleRepository scheduleRepository,
            ScheduleTypeRepository scheduleTypeRepository,
            DeveloperProfileService profileService,
            GrowthSubCategoryRepository subCategoryRepository
    ) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleTypeRepository = scheduleTypeRepository;
        this.profileService = profileService;
        this.subCategoryRepository = subCategoryRepository;
    }

    @Transactional(readOnly = true)
    public List<ScheduleView> findSchedulesForDefaultProfile() {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        return scheduleRepository.findByProfileOrderByStartDateTimeAsc(profile).stream()
                .map(ScheduleView::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduleView> findSchedulesByStatusForDefaultProfile(ScheduleStatus status) {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        return scheduleRepository.findByProfileAndStatusOrderByStartDateTimeAsc(profile, status).stream()
                .map(ScheduleView::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduleView> findTodaySchedulesForDefaultProfile() {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        LocalDate today = LocalDate.now();
        return findSchedulesBetween(profile, today.atStartOfDay(), today.plusDays(1).atStartOfDay());
    }

    @Transactional(readOnly = true)
    public List<ScheduleView> findWeekSchedulesForDefaultProfile() {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        LocalDate today = LocalDate.now();
        return findSchedulesBetween(profile, today.atStartOfDay(), today.plusDays(7).atStartOfDay());
    }

    private List<ScheduleView> findSchedulesBetween(DeveloperProfile profile, LocalDateTime start, LocalDateTime end) {
        return scheduleRepository.findByProfileAndStartDateTimeBetweenOrderByStartDateTimeAsc(profile, start, end).stream()
                .map(ScheduleView::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Schedule getSchedule(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));
    }

    @Transactional
    public Schedule create(ScheduleCreateRequest request) {
        validateCreate(request);
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        GrowthSubCategory subCategory = findSubCategory(request.getRelatedGrowthSubCategoryId());
        ScheduleType scheduleType = findActiveScheduleType(request.getScheduleTypeId());
        LocalDateTime startDateTime = request.toStartDateTime();
        LocalDateTime endDateTime = request.toEndDateTime();
        Schedule schedule = new Schedule(
                profile,
                subCategory,
                request.getTitle().trim(),
                normalizeDescription(request.getDescription()),
                startDateTime,
                endDateTime,
                scheduleType
        );
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void update(Long id, ScheduleUpdateRequest request) {
        validateUpdate(request);
        Schedule schedule = getSchedule(id);
        GrowthSubCategory subCategory = findSubCategory(request.getRelatedGrowthSubCategoryId());
        ScheduleType scheduleType = findActiveScheduleType(request.getScheduleTypeId());
        LocalDateTime startDateTime = request.toStartDateTime();
        LocalDateTime endDateTime = request.toEndDateTime();
        schedule.update(
                subCategory,
                request.getTitle().trim(),
                normalizeDescription(request.getDescription()),
                startDateTime,
                endDateTime,
                scheduleType,
                request.getStatus()
        );
    }

    @Transactional
    public void done(Long id) {
        Schedule schedule = getSchedule(id);
        if (schedule.getStatus() == ScheduleStatus.DONE) {
            throw new IllegalArgumentException("이미 완료된 일정입니다.");
        }
        schedule.done();
    }

    @Transactional
    public void delete(Long id) {
        Schedule schedule = getSchedule(id);
        scheduleRepository.delete(schedule);
    }

    private GrowthSubCategory findSubCategory(Long subCategoryId) {
        if (subCategoryId == null) {
            return null;
        }
        return subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("성장 중분류를 찾을 수 없습니다."));
    }

    private ScheduleType findActiveScheduleType(Long scheduleTypeId) {
        if (scheduleTypeId == null) {
            throw new IllegalArgumentException("일정 타입을 선택해 주세요.");
        }
        ScheduleType scheduleType = scheduleTypeRepository.findById(scheduleTypeId)
                .orElseThrow(() -> new IllegalArgumentException("일정 타입을 찾을 수 없습니다."));
        if (!scheduleType.isActive()) {
            throw new IllegalArgumentException("비활성화된 일정 타입은 선택할 수 없습니다.");
        }
        return scheduleType;
    }

    private void validateCreate(ScheduleCreateRequest request) {
        if (request.getScheduleTypeId() == null) {
            throw new IllegalArgumentException("일정 타입을 선택해 주세요.");
        }
        validateRequiredFields(
                request.getTitle(),
                request.getStartDate(),
                request.getStartHour(),
                request.getStartMinute(),
                request.getEndDate(),
                request.getEndHour(),
                request.getEndMinute()
        );
    }

    private void validateUpdate(ScheduleUpdateRequest request) {
        if (request.getScheduleTypeId() == null) {
            throw new IllegalArgumentException("일정 타입을 선택해 주세요.");
        }
        if (request.getStatus() == null) {
            throw new IllegalArgumentException("일정 상태를 선택해 주세요.");
        }
        validateRequiredFields(
                request.getTitle(),
                request.getStartDate(),
                request.getStartHour(),
                request.getStartMinute(),
                request.getEndDate(),
                request.getEndHour(),
                request.getEndMinute()
        );
    }

    private void validateRequiredFields(
            String title,
            LocalDate startDate,
            Integer startHour,
            Integer startMinute,
            LocalDate endDate,
            Integer endHour,
            Integer endMinute
    ) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("일정 제목을 입력해 주세요.");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("시작 날짜를 입력해 주세요.");
        }
        validateTime("시작", startHour, startMinute);
        if (endDate != null) {
            validateTime("종료", endHour, endMinute);
        }
        LocalDateTime startDateTime = LocalDateTime.of(startDate, java.time.LocalTime.of(startHour, startMinute));
        LocalDateTime endDateTime = endDate == null
                ? null
                : LocalDateTime.of(endDate, java.time.LocalTime.of(endHour, endMinute));
        if (startDateTime == null) {
            throw new IllegalArgumentException("시작 일시를 입력해 주세요.");
        }
        if (endDateTime != null && endDateTime.isBefore(startDateTime)) {
            throw new IllegalArgumentException("종료 일시는 시작 일시보다 빠를 수 없습니다.");
        }
    }

    private void validateTime(String label, Integer hour, Integer minute) {
        if (hour == null || minute == null) {
            throw new IllegalArgumentException(label + " 시간을 입력해 주세요.");
        }
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException(label + " 시는 0부터 23 사이로 입력해 주세요.");
        }
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException(label + " 분은 0부터 59 사이로 입력해 주세요.");
        }
    }

    private String normalizeDescription(String description) {
        return description == null ? "" : description.trim();
    }
}
