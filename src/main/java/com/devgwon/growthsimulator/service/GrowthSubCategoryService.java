package com.devgwon.growthsimulator.service;

import com.devgwon.growthsimulator.dto.request.GrowthSubCategoryForm;
import com.devgwon.growthsimulator.entity.GrowthCategory;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import com.devgwon.growthsimulator.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.repository.GrowthLogRepository;
import com.devgwon.growthsimulator.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.repository.QuestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GrowthSubCategoryService {
    private final GrowthCategoryRepository categoryRepository;
    private final GrowthSubCategoryRepository subCategoryRepository;
    private final QuestRepository questRepository;
    private final GrowthLogRepository growthLogRepository;

    @Transactional(readOnly = true)
    public GrowthSubCategory get(Long id) {
        return subCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("성장 중분류를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<GrowthSubCategory> findAllForSelection() {
        return subCategoryRepository.findAllByOrderByCategory_SortOrderAscSortOrderAsc();
    }

    @Transactional
    public GrowthSubCategory create(Long categoryId, GrowthSubCategoryForm form) {
        validate(form);
        GrowthCategory category = getCategory(categoryId);
        GrowthSubCategory subCategory = new GrowthSubCategory(
                category,
                form.normalizedName(),
                form.normalizedDisplayName(),
                form.normalizedDescription(),
                form.normalizedSortOrder()
        );
        return subCategoryRepository.save(subCategory);
    }

    @Transactional
    public void update(Long id, GrowthSubCategoryForm form) {
        validate(form);
        GrowthSubCategory subCategory = get(id);
        GrowthCategory category = getCategory(form.getCategoryId());
        subCategory.update(
                category,
                form.normalizedName(),
                form.normalizedDisplayName(),
                form.normalizedDescription(),
                form.normalizedSortOrder()
        );
    }

    @Transactional
    public void delete(Long id) {
        GrowthSubCategory subCategory = get(id);
        if (questRepository.existsBySubCategory(subCategory) || growthLogRepository.existsBySubCategory(subCategory)) {
            throw new IllegalArgumentException("이 중분류는 이미 퀘스트 또는 성장 기록에서 사용 중이라 삭제할 수 없습니다.");
        }
        subCategoryRepository.delete(subCategory);
    }

    private GrowthCategory getCategory(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("대분류를 선택해 주세요.");
        }
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("성장 대분류를 찾을 수 없습니다."));
    }

    private void validate(GrowthSubCategoryForm form) {
        if (form.normalizedName() == null || form.normalizedName().isBlank()) {
            throw new IllegalArgumentException("중분류 name을 입력해 주세요.");
        }
        if (form.normalizedDisplayName() == null || form.normalizedDisplayName().isBlank()) {
            throw new IllegalArgumentException("중분류 표시 이름을 입력해 주세요.");
        }
    }
}
