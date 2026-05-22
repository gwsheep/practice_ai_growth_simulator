package com.devgwon.growthsimulator.service;

import com.devgwon.growthsimulator.dto.request.GrowthCategoryForm;
import com.devgwon.growthsimulator.dto.response.GrowthCategoryView;
import com.devgwon.growthsimulator.entity.GrowthCategory;
import com.devgwon.growthsimulator.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.repository.GrowthSubCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GrowthCategoryService {
    private final GrowthCategoryRepository categoryRepository;
    private final GrowthSubCategoryRepository subCategoryRepository;

    @Transactional(readOnly = true)
    public List<GrowthCategory> findAll() {
        return categoryRepository.findAllByOrderBySortOrderAsc();
    }

    @Transactional(readOnly = true)
    public List<GrowthCategoryView> findCategoryViews() {
        return findAll().stream()
                .map(category -> new GrowthCategoryView(
                        category,
                        subCategoryRepository.findByCategoryOrderBySortOrderAsc(category)
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public GrowthCategory get(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("성장 대분류를 찾을 수 없습니다."));
    }

    @Transactional
    public GrowthCategory create(GrowthCategoryForm form) {
        validate(form);
        GrowthCategory category = new GrowthCategory(
                form.normalizedName(),
                form.normalizedDisplayName(),
                form.normalizedDescription(),
                form.normalizedSortOrder()
        );
        return categoryRepository.save(category);
    }

    @Transactional
    public void update(Long id, GrowthCategoryForm form) {
        validate(form);
        GrowthCategory category = get(id);
        category.update(
                form.normalizedName(),
                form.normalizedDisplayName(),
                form.normalizedDescription(),
                form.normalizedSortOrder()
        );
    }

    @Transactional
    public void delete(Long id) {
        GrowthCategory category = get(id);
        if (subCategoryRepository.countByCategory(category) > 0) {
            throw new IllegalArgumentException("이 대분류에는 중분류가 있어 삭제할 수 없습니다.");
        }
        categoryRepository.delete(category);
    }

    private void validate(GrowthCategoryForm form) {
        if (form.normalizedName() == null || form.normalizedName().isBlank()) {
            throw new IllegalArgumentException("대분류 name을 입력해 주세요.");
        }
        if (form.normalizedDisplayName() == null || form.normalizedDisplayName().isBlank()) {
            throw new IllegalArgumentException("대분류 표시 이름을 입력해 주세요.");
        }
    }
}
