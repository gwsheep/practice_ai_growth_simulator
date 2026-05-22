package com.devgwon.growthsimulator.controller;

import com.devgwon.growthsimulator.dto.request.QuestCreateRequest;
import com.devgwon.growthsimulator.dto.response.QuestCompleteResult;
import com.devgwon.growthsimulator.entity.Difficulty;
import com.devgwon.growthsimulator.service.GrowthCategoryService;
import com.devgwon.growthsimulator.service.GrowthSubCategoryService;
import com.devgwon.growthsimulator.service.QuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@RequestMapping("/quests")
public class QuestController {
    private final QuestService questService;
    private final GrowthCategoryService categoryService;
    private final GrowthSubCategoryService subCategoryService;

    @GetMapping
    public String quests(Model model) {
        model.addAttribute("quests", questService.findQuestViewsForDefaultProfile());
        return "quests/index";
    }

    @GetMapping("/new")
    public String newQuest(Model model) {
        model.addAttribute("questCreateRequest", new QuestCreateRequest());
        model.addAttribute("growthCategories", categoryService.findAll());
        model.addAttribute("growthSubCategories", subCategoryService.findAllForSelection());
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
