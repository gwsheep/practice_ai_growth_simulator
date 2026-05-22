package com.devgwon.growthsimulator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devgwon.growthsimulator.dto.request.GrowthCategoryForm;
import com.devgwon.growthsimulator.entity.GrowthCategory;
import com.devgwon.growthsimulator.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.repository.GrowthSubCategoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GrowthCategoryServiceTest {
    @Mock
    private GrowthCategoryRepository categoryRepository;

    @Mock
    private GrowthSubCategoryRepository subCategoryRepository;

    @InjectMocks
    private GrowthCategoryService categoryService;

    @Test
    void createNormalizesInputAndSavesCategory() {
        GrowthCategoryForm form = new GrowthCategoryForm();
        form.setName(" backend ");
        form.setDisplayName(" 백엔드 ");
        form.setDescription(" API 성장 ");
        form.setSortOrder(3);
        categoryService.create(form);
        ArgumentCaptor<GrowthCategory> captor = ArgumentCaptor.forClass(GrowthCategory.class);
        verify(categoryRepository).save(captor.capture());
        GrowthCategory saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("BACKEND");
        assertThat(saved.getDisplayName()).isEqualTo("백엔드");
        assertThat(saved.getDescription()).isEqualTo("API 성장");
        assertThat(saved.getSortOrder()).isEqualTo(3);
    }

    @Test
    void deleteRejectsCategoryWithSubCategories() {
        GrowthCategory category = new GrowthCategory("BACKEND", "백엔드", "desc", 1);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(subCategoryRepository.countByCategory(category)).thenReturn(1L);
        assertThatThrownBy(() -> categoryService.delete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("중분류");
        verify(categoryRepository, never()).delete(any());
    }
}
