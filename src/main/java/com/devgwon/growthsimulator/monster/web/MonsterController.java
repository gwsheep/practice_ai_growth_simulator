package com.devgwon.growthsimulator.monster.web;

import com.devgwon.growthsimulator.growth.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.growth.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.monster.domain.Monster;
import com.devgwon.growthsimulator.monster.domain.MonsterDifficulty;
import com.devgwon.growthsimulator.monster.domain.MonsterType;
import com.devgwon.growthsimulator.monster.service.MonsterAttackRequest;
import com.devgwon.growthsimulator.monster.service.MonsterAttackResult;
import com.devgwon.growthsimulator.monster.service.MonsterCreateRequest;
import com.devgwon.growthsimulator.monster.service.MonsterService;
import com.devgwon.growthsimulator.monster.service.MonsterUpdateRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/monsters")
public class MonsterController {

    private final MonsterService monsterService;
    private final GrowthCategoryRepository categoryRepository;
    private final GrowthSubCategoryRepository subCategoryRepository;

    public MonsterController(
            MonsterService monsterService,
            GrowthCategoryRepository categoryRepository,
            GrowthSubCategoryRepository subCategoryRepository
    ) {
        this.monsterService = monsterService;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    @GetMapping
    public String monsters(Model model) {
        model.addAttribute("monsters", monsterService.findMonstersForDefaultProfile());
        return "monsters/list";
    }

    @GetMapping("/new")
    public String newMonster(Model model) {
        model.addAttribute("monsterForm", monsterService.newCreateRequest());
        model.addAttribute("editMode", false);
        addFormOptions(model);
        return "monsters/form";
    }

    @PostMapping
    public String createMonster(
            @ModelAttribute("monsterForm") MonsterCreateRequest request,
            RedirectAttributes redirectAttributes
    ) {
        Monster monster = monsterService.create(request);
        redirectAttributes.addFlashAttribute("noticeMessage", "새 몬스터가 등장했습니다.");
        return "redirect:/monsters/" + monster.getId();
    }

    @GetMapping("/{id}")
    public String monster(@PathVariable Long id, Model model) {
        model.addAttribute("monster", monsterService.getView(id));
        model.addAttribute("attackForm", new MonsterAttackRequest());
        return "monsters/detail";
    }

    @GetMapping("/{id}/edit")
    public String editMonster(@PathVariable Long id, Model model) {
        Monster monster = monsterService.getMonster(id);
        model.addAttribute("monster", monster);
        model.addAttribute("monsterForm", MonsterUpdateRequest.from(monster));
        model.addAttribute("editMode", true);
        addFormOptions(model);
        return "monsters/form";
    }

    @PostMapping("/{id}/edit")
    public String updateMonster(
            @PathVariable Long id,
            @ModelAttribute("monsterForm") MonsterUpdateRequest request,
            RedirectAttributes redirectAttributes
    ) {
        monsterService.update(id, request);
        redirectAttributes.addFlashAttribute("noticeMessage", "몬스터 정보가 수정되었습니다.");
        return "redirect:/monsters/" + id;
    }

    @PostMapping("/{id}/attack")
    public String attackMonster(
            @PathVariable Long id,
            @ModelAttribute("attackForm") MonsterAttackRequest request,
            RedirectAttributes redirectAttributes
    ) {
        MonsterAttackResult result = monsterService.attack(id, request);
        redirectAttributes.addFlashAttribute("noticeMessage", result.message());
        return "redirect:/monsters/" + id;
    }

    @PostMapping("/{id}/archive")
    public String archiveMonster(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        monsterService.archive(id);
        redirectAttributes.addFlashAttribute("noticeMessage", "몬스터를 보관함으로 이동했습니다.");
        return "redirect:/monsters";
    }

    private void addFormOptions(Model model) {
        model.addAttribute("monsterTypes", MonsterType.values());
        model.addAttribute("monsterDifficulties", MonsterDifficulty.values());
        model.addAttribute("growthCategories", categoryRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("growthSubCategories", subCategoryRepository.findAllByOrderByCategory_SortOrderAscSortOrderAsc().stream()
                .map(GrowthSubCategoryOption::from)
                .toList());
    }

    private record GrowthSubCategoryOption(
            Long id,
            String label
    ) {

        static GrowthSubCategoryOption from(GrowthSubCategory subCategory) {
            return new GrowthSubCategoryOption(
                    subCategory.getId(),
                    subCategory.getCategory().getDisplayName() + " · " + subCategory.getDisplayName()
            );
        }
    }
}
