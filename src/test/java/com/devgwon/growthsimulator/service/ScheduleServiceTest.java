package com.devgwon.growthsimulator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devgwon.growthsimulator.dto.request.ScheduleCreateRequest;
import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.Schedule;
import com.devgwon.growthsimulator.entity.ScheduleType;
import com.devgwon.growthsimulator.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.repository.ScheduleRepository;
import com.devgwon.growthsimulator.repository.ScheduleTypeRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {
    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ScheduleTypeRepository scheduleTypeRepository;

    @Mock
    private DeveloperProfileService profileService;

    @Mock
    private GrowthSubCategoryRepository subCategoryRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    @Test
    void createUsesActiveScheduleTypeEntity() {
        DeveloperProfile profile = new DeveloperProfile("devgwon");
        ScheduleType scheduleType = new ScheduleType("STUDY", "공부", "desc", "#4d78b8", 1);
        ScheduleCreateRequest request = new ScheduleCreateRequest();
        request.setTitle("코딩 공부");
        request.setDescription("MVC 정리");
        request.setScheduleTypeId(1L);
        request.setStartDate(LocalDate.of(2026, 5, 14));
        request.setStartHour(19);
        request.setStartMinute(30);
        when(profileService.getOrCreateDefaultProfile()).thenReturn(profile);
        when(scheduleTypeRepository.findById(1L)).thenReturn(Optional.of(scheduleType));
        scheduleService.create(request);
        ArgumentCaptor<Schedule> captor = ArgumentCaptor.forClass(Schedule.class);
        verify(scheduleRepository).save(captor.capture());
        Schedule saved = captor.getValue();
        assertThat(saved.getProfile()).isSameAs(profile);
        assertThat(saved.getScheduleType()).isSameAs(scheduleType);
        assertThat(saved.getLegacyScheduleType()).isEqualTo("STUDY");
        assertThat(saved.getStartDateTime()).isEqualTo("2026-05-14T19:30");
    }

    @Test
    void createRejectsInactiveScheduleType() {
        ScheduleType scheduleType = new ScheduleType("OLD", "예전 타입", "desc", "#647084", 1);
        scheduleType.deactivate();
        ScheduleCreateRequest request = new ScheduleCreateRequest();
        request.setTitle("코딩 공부");
        request.setScheduleTypeId(1L);
        request.setStartDate(LocalDate.of(2026, 5, 14));
        request.setStartHour(19);
        request.setStartMinute(0);
        when(profileService.getOrCreateDefaultProfile()).thenReturn(new DeveloperProfile("devgwon"));
        when(scheduleTypeRepository.findById(1L)).thenReturn(Optional.of(scheduleType));
        assertThatThrownBy(() -> scheduleService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비활성화");
        verify(scheduleRepository, never()).save(any());
    }
}
