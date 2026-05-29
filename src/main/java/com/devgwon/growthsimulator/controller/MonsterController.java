package com.devgwon.growthsimulator.controller;

import com.devgwon.growthsimulator.dto.request.MonsterAttackRequest;
import com.devgwon.growthsimulator.dto.request.MonsterCreateRequest;
import com.devgwon.growthsimulator.dto.request.MonsterUpdateRequest;
import com.devgwon.growthsimulator.dto.response.GrowthSubCategoryOption;
import com.devgwon.growthsimulator.dto.response.MonsterAttackResult;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import com.devgwon.growthsimulator.entity.Monster;
import com.devgwon.growthsimulator.entity.MonsterDifficulty;
import com.devgwon.growthsimulator.entity.MonsterType;
import com.devgwon.growthsimulator.service.GrowthCategoryService;
import com.devgwon.growthsimulator.service.GrowthSubCategoryService;
import com.devgwon.growthsimulator.service.MonsterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@RequestMapping("/monsters")
public class MonsterController {
    private final MonsterService monsterService;
    private final GrowthCategoryService categoryService;
    private final GrowthSubCategoryService subCategoryService;

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
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return createForm(model);
        }
        Monster monster;
        try {
            monster = monsterService.create(request);
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("monster.invalid", exception.getMessage());
            return createForm(model);
        }
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
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return updateForm(id, model);
        }
        try {
            monsterService.update(id, request);
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("monster.invalid", exception.getMessage());
            return updateForm(id, model);
        }
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

    private String createForm(Model model) {
        model.addAttribute("editMode", false);
        addFormOptions(model);
        return "monsters/form";
    }

    private String updateForm(Long id, Model model) {
        model.addAttribute("monster", monsterService.getMonster(id));
        model.addAttribute("editMode", true);
        addFormOptions(model);
        return "monsters/form";
    }

    private void addFormOptions(Model model) {
        model.addAttribute("monsterTypes", MonsterType.values());
        model.addAttribute("monsterDifficulties", MonsterDifficulty.values());
        model.addAttribute("growthCategories", categoryService.findAll());
        model.addAttribute("growthSubCategories", subCategoryService.findAllForSelection().stream()
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
