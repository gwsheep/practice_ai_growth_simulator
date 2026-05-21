package com.devgwon.growthsimulator.quest.service;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.character.service.CharacterMessageService;
import com.devgwon.growthsimulator.character.service.DeveloperProfileService;
import com.devgwon.growthsimulator.global.exception.QuestAlreadyCompletedException;
import com.devgwon.growthsimulator.global.exception.QuestNotFoundException;
import com.devgwon.growthsimulator.growth.domain.GrowthLog;
import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.growth.repository.GrowthLogRepository;
import com.devgwon.growthsimulator.growth.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.quest.domain.Quest;
import com.devgwon.growthsimulator.quest.repository.QuestRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestService {

    private final QuestRepository questRepository;
    private final DeveloperProfileService profileService;
    private final GrowthSubCategoryRepository subCategoryRepository;
    private final GrowthLogRepository growthLogRepository;
    private final CharacterMessageService characterMessageService;

    public QuestService(
            QuestRepository questRepository,
            DeveloperProfileService profileService,
            GrowthSubCategoryRepository subCategoryRepository,
            GrowthLogRepository growthLogRepository,
            CharacterMessageService characterMessageService
    ) {
        this.questRepository = questRepository;
        this.profileService = profileService;
        this.subCategoryRepository = subCategoryRepository;
        this.growthLogRepository = growthLogRepository;
        this.characterMessageService = characterMessageService;
    }

    @Transactional
    public List<Quest> findQuestsForDefaultProfile() {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        return questRepository.findByProfileOrderByCreatedAtDesc(profile);
    }

    @Transactional(readOnly = true)
    public List<QuestListItemView> findQuestViewsForDefaultProfile() {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        return questRepository.findByProfileOrderByCreatedAtDesc(profile).stream()
                .map(QuestListItemView::from)
                .toList();
    }

    @Transactional
    public Quest createQuest(QuestCreateRequest request) {
        validateCreateRequest(request);
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        GrowthSubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("성장 항목을 찾을 수 없습니다."));
        Quest quest = new Quest(
                profile,
                subCategory,
                request.getTitle().trim(),
                request.getDescription(),
                request.getDifficulty()
        );
        return questRepository.save(quest);
    }

    private void validateCreateRequest(QuestCreateRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("퀘스트 제목을 입력해 주세요.");
        }
        if (request.getSubCategoryId() == null) {
            throw new IllegalArgumentException("성장 중분류를 선택해 주세요.");
        }
        if (request.getDifficulty() == null) {
            throw new IllegalArgumentException("퀘스트 난이도를 선택해 주세요.");
        }
    }

    @Transactional
    public QuestCompleteResult completeQuest(Long questId) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(QuestNotFoundException::new);
        if (quest.isCompleted()) {
            throw new QuestAlreadyCompletedException();
        }

        DeveloperProfile profile = quest.getProfile();
        GrowthSubCategory subCategory = resolveSubCategory(quest);
        int beforeLevel = profile.getLevel();

        quest.complete();
        profile.addExp(quest.getExpReward());
        subCategory.addExp(quest.getExpReward());

        boolean levelUp = profile.getLevel() > beforeLevel;
        String message = characterMessageService.completeMessage(subCategory, levelUp);
        growthLogRepository.save(new GrowthLog(
                profile,
                quest,
                subCategory,
                quest.getExpReward(),
                message
        ));

        return new QuestCompleteResult(
                quest.getId(),
                quest.getTitle(),
                subCategory.getCategory().getDisplayName(),
                subCategory.getDisplayName(),
                quest.getExpReward(),
                profile.getLevel(),
                profile.getExp(),
                levelUp,
                message
        );
    }

    private GrowthSubCategory resolveSubCategory(Quest quest) {
        if (quest.getSubCategory() != null) {
            return quest.getSubCategory();
        }

        String legacyQuestType = quest.getLegacyQuestType() == null ? "WORK" : quest.getLegacyQuestType();
        return switch (legacyQuestType) {
            case "ALGORITHM" -> findSubCategory("ALGORITHM", "CODING_TEST");
            case "SPRING" -> findSubCategory("FRAMEWORK", "SPRING_BOOT");
            case "DATABASE" -> findSubCategory("DATABASE", "SQL");
            case "INFRA" -> findSubCategory("INFRA", "DOCKER");
            case "CS" -> findSubCategory("CS", "HTTP");
            case "BLOG" -> findSubCategory("CAREER", "BLOG");
            case "CAREER" -> findSubCategory("CAREER", "PORTFOLIO");
            case "REST" -> findSubCategory("MENTAL", "REST");
            default -> findSubCategory("BACKEND", "API_DESIGN");
        };
    }

    private GrowthSubCategory findSubCategory(String categoryName, String subCategoryName) {
        return subCategoryRepository.findByCategory_NameAndName(categoryName, subCategoryName)
                .orElseThrow(() -> new IllegalArgumentException("성장 항목을 찾을 수 없습니다."));
    }
}
