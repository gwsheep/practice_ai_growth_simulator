package com.devgwon.growthsimulator.quest.web;

import com.devgwon.growthsimulator.growth.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.growth.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.quest.domain.Difficulty;
import com.devgwon.growthsimulator.quest.service.QuestCreateRequest;
import com.devgwon.growthsimulator.quest.service.QuestCompleteResult;
import com.devgwon.growthsimulator.quest.service.QuestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/quests")
public class QuestController {

    private final QuestService questService;
    private final GrowthCategoryRepository categoryRepository;
    private final GrowthSubCategoryRepository subCategoryRepository;

    public QuestController(
            QuestService questService,
            GrowthCategoryRepository categoryRepository,
            GrowthSubCategoryRepository subCategoryRepository
    ) {
        this.questService = questService;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    @GetMapping
    public String quests(Model model) {
        model.addAttribute("quests", questService.findQuestsForDefaultProfile());
        return "quests/index";
    }

    @GetMapping("/new")
    public String newQuest(Model model) {
        model.addAttribute("questCreateRequest", new QuestCreateRequest());
        model.addAttribute("growthCategories", categoryRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("growthSubCategories", subCategoryRepository.findAllByOrderByCategory_SortOrderAscSortOrderAsc());
        model.addAttribute("difficulties", Difficulty.values());
        return "quests/new";
    }

    @PostMapping
    public String createQuest(@ModelAttribute QuestCreateRequest request, RedirectAttributes redirectAttributes) {
        questService.createQuest(request);
        redirectAttributes.addFlashAttribute("noticeMessage", "새 퀘스트가 등록되었습니다.");
        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/complete")
    public String completeQuest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        QuestCompleteResult result = questService.completeQuest(id);
        redirectAttributes.addFlashAttribute("noticeMessage", result.getSummaryMessage());
        redirectAttributes.addFlashAttribute("completedMessage", result.getSummaryMessage());
        redirectAttributes.addFlashAttribute("gainedExp", result.getExpReward());
        redirectAttributes.addFlashAttribute("gainedStatName", result.getGainedStatName());
        redirectAttributes.addFlashAttribute("gainedStatAmount", result.getGainedStatAmount());
        redirectAttributes.addFlashAttribute("levelUp", result.isLevelUp());
        redirectAttributes.addFlashAttribute("characterMessage", result.getCharacterMessage());
        return "redirect:/dashboard";
    }
}
