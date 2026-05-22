package com.devgwon.growthsimulator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devgwon.growthsimulator.dto.request.GrowthSubCategoryForm;
import com.devgwon.growthsimulator.entity.GrowthCategory;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import com.devgwon.growthsimulator.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.repository.GrowthLogRepository;
import com.devgwon.growthsimulator.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.repository.QuestRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GrowthSubCategoryServiceTest {
    @Mock
    private GrowthCategoryRepository categoryRepository;

    @Mock
    private GrowthSubCategoryRepository subCategoryRepository;

    @Mock
    private QuestRepository questRepository;

    @Mock
    private GrowthLogRepository growthLogRepository;

    @InjectMocks
    private GrowthSubCategoryService subCategoryService;

    @Test
    void createNormalizesInputAndConnectsCategory() {
        GrowthCategory category = new GrowthCategory("BACKEND", "백엔드", "desc", 1);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        GrowthSubCategoryForm form = new GrowthSubCategoryForm();
        form.setName(" api_design ");
        form.setDisplayName(" API 설계 ");
        form.setDescription(" 설계 연습 ");
        form.setSortOrder(2);
        subCategoryService.create(1L, form);
        ArgumentCaptor<GrowthSubCategory> captor = ArgumentCaptor.forClass(GrowthSubCategory.class);
        verify(subCategoryRepository).save(captor.capture());
        GrowthSubCategory saved = captor.getValue();
        assertThat(saved.getCategory()).isSameAs(category);
        assertThat(saved.getName()).isEqualTo("API_DESIGN");
        assertThat(saved.getDisplayName()).isEqualTo("API 설계");
        assertThat(saved.getDescription()).isEqualTo("설계 연습");
        assertThat(saved.getSortOrder()).isEqualTo(2);
    }

    @Test
    void deleteRejectsSubCategoryUsedByQuest() {
        GrowthCategory category = new GrowthCategory("BACKEND", "백엔드", "desc", 1);
        GrowthSubCategory subCategory = new GrowthSubCategory(category, "API", "API", "desc", 1);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));
        when(questRepository.existsBySubCategory(subCategory)).thenReturn(true);
        assertThatThrownBy(() -> subCategoryService.delete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용 중");
        verify(subCategoryRepository, never()).delete(any());
    }
}
