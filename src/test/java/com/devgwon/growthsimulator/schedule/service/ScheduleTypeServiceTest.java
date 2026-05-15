package com.devgwon.growthsimulator.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devgwon.growthsimulator.schedule.domain.ScheduleType;
import com.devgwon.growthsimulator.schedule.repository.ScheduleTypeRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleTypeServiceTest {

    @Mock
    private ScheduleTypeRepository scheduleTypeRepository;

    @InjectMocks
    private ScheduleTypeService scheduleTypeService;

    @Test
    void createNormalizesCodeAndSavesActiveType() {
        ScheduleTypeForm form = new ScheduleTypeForm();
        form.setCode(" certificate ");
        form.setName(" 자격증 ");
        form.setDescription(" 시험 준비 ");
        form.setColor("#123456");
        form.setSortOrder(8);
        when(scheduleTypeRepository.findByCode("CERTIFICATE")).thenReturn(Optional.empty());

        scheduleTypeService.create(form);

        ArgumentCaptor<ScheduleType> captor = ArgumentCaptor.forClass(ScheduleType.class);
        verify(scheduleTypeRepository).save(captor.capture());
        ScheduleType saved = captor.getValue();
        assertThat(saved.getCode()).isEqualTo("CERTIFICATE");
        assertThat(saved.getName()).isEqualTo("자격증");
        assertThat(saved.getDescription()).isEqualTo("시험 준비");
        assertThat(saved.getColor()).isEqualTo("#123456");
        assertThat(saved.getSortOrder()).isEqualTo(8);
        assertThat(saved.isActive()).isTrue();
    }

    @Test
    void createRejectsDuplicateCode() {
        ScheduleTypeForm form = new ScheduleTypeForm();
        form.setCode("STUDY");
        form.setName("공부");
        when(scheduleTypeRepository.findByCode("STUDY"))
                .thenReturn(Optional.of(new ScheduleType("STUDY", "공부", "desc", "#4d78b8", 1)));

        assertThatThrownBy(() -> scheduleTypeService.create(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 사용 중");
    }

    @Test
    void deactivateSoftDeletesType() {
        ScheduleType scheduleType = new ScheduleType("STUDY", "공부", "desc", "#4d78b8", 1);
        when(scheduleTypeRepository.findById(1L)).thenReturn(Optional.of(scheduleType));

        scheduleTypeService.deactivate(1L);

        assertThat(scheduleType.isActive()).isFalse();
    }
}
