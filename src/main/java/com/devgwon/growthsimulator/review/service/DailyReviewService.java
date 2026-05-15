package com.devgwon.growthsimulator.review.service;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.character.service.DeveloperProfileService;
import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.growth.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.review.domain.DailyReview;
import com.devgwon.growthsimulator.review.repository.DailyReviewRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DailyReviewService {

    private final DailyReviewRepository dailyReviewRepository;
    private final DeveloperProfileService profileService;
    private final GrowthSubCategoryRepository subCategoryRepository;

    public DailyReviewService(
            DailyReviewRepository dailyReviewRepository,
            DeveloperProfileService profileService,
            GrowthSubCategoryRepository subCategoryRepository
    ) {
        this.dailyReviewRepository = dailyReviewRepository;
        this.profileService = profileService;
        this.subCategoryRepository = subCategoryRepository;
    }

    @Transactional(readOnly = true)
    public List<DailyReviewView> findReviewsForDefaultProfile() {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        return dailyReviewRepository.findByProfileOrderByReviewDateDesc(profile).stream()
                .map(DailyReviewView::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DailyReviewSummaryView> findRecentReviewsForDefaultProfile() {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        return dailyReviewRepository.findTop3ByProfileOrderByReviewDateDesc(profile).stream()
                .map(DailyReviewSummaryView::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DailyReview get(Long id) {
        return dailyReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회고를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public DailyReviewView getView(Long id) {
        return DailyReviewView.from(get(id));
    }

    @Transactional(readOnly = true)
    public Optional<DailyReview> findTodayReviewForDefaultProfile() {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        return dailyReviewRepository.findByProfileAndReviewDate(profile, LocalDate.now());
    }

    @Transactional
    public DailyReview create(DailyReviewCreateRequest request) {
        validateCreate(request);
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        if (dailyReviewRepository.existsByProfileAndReviewDate(profile, request.getReviewDate())) {
            throw new IllegalArgumentException("해당 날짜의 회고는 이미 작성되었습니다. 기존 회고를 수정해 주세요.");
        }
        GrowthSubCategory subCategory = findSubCategory(request.getRelatedGrowthSubCategoryId());
        return dailyReviewRepository.save(new DailyReview(
                profile,
                request.getReviewDate(),
                normalize(request.getLearnedText()),
                normalize(request.getDifficultyText()),
                normalize(request.getTomorrowPlanText()),
                request.getMoodScore(),
                subCategory
        ));
    }

    @Transactional
    public void update(Long id, DailyReviewUpdateRequest request) {
        validateUpdate(request);
        DailyReview review = get(id);
        DeveloperProfile profile = review.getProfile();
        dailyReviewRepository.findByProfileAndReviewDate(profile, request.getReviewDate())
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new IllegalArgumentException("해당 날짜의 회고는 이미 작성되었습니다.");
                });
        GrowthSubCategory subCategory = findSubCategory(request.getRelatedGrowthSubCategoryId());
        review.update(
                request.getReviewDate(),
                normalize(request.getLearnedText()),
                normalize(request.getDifficultyText()),
                normalize(request.getTomorrowPlanText()),
                request.getMoodScore(),
                subCategory
        );
    }

    @Transactional
    public void delete(Long id) {
        dailyReviewRepository.delete(get(id));
    }

    public DailyReviewCreateRequest newTodayRequest() {
        DailyReviewCreateRequest request = new DailyReviewCreateRequest();
        request.setReviewDate(LocalDate.now());
        return request;
    }

    private GrowthSubCategory findSubCategory(Long subCategoryId) {
        if (subCategoryId == null) {
            return null;
        }
        return subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("성장 중분류를 찾을 수 없습니다."));
    }

    private void validateCreate(DailyReviewCreateRequest request) {
        validateRequiredFields(
                request.getReviewDate(),
                request.getLearnedText(),
                request.getDifficultyText(),
                request.getTomorrowPlanText(),
                request.getMoodScore()
        );
    }

    private void validateUpdate(DailyReviewUpdateRequest request) {
        validateRequiredFields(
                request.getReviewDate(),
                request.getLearnedText(),
                request.getDifficultyText(),
                request.getTomorrowPlanText(),
                request.getMoodScore()
        );
    }

    private void validateRequiredFields(
            LocalDate reviewDate,
            String learnedText,
            String difficultyText,
            String tomorrowPlanText,
            int moodScore
    ) {
        if (reviewDate == null) {
            throw new IllegalArgumentException("회고 날짜를 입력해 주세요.");
        }
        if (moodScore < 1 || moodScore > 5) {
            throw new IllegalArgumentException("기분 점수는 1부터 5 사이로 입력해 주세요.");
        }
        if (normalize(learnedText).isBlank()
                && normalize(difficultyText).isBlank()
                && normalize(tomorrowPlanText).isBlank()) {
            throw new IllegalArgumentException("배운 것, 힘들었던 점, 내일 할 일 중 하나는 입력해 주세요.");
        }
    }

    private String normalize(String text) {
        return text == null ? "" : text.trim();
    }
}
