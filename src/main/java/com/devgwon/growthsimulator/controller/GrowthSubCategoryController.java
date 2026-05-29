package com.devgwon.growthsimulator.controller;

import com.devgwon.growthsimulator.dto.request.GrowthSubCategoryForm;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import com.devgwon.growthsimulator.service.GrowthCategoryService;
import com.devgwon.growthsimulator.service.GrowthSubCategoryService;
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
@RequestMapping("/growth-sub-categories")
public class GrowthSubCategoryController {
    private final GrowthCategoryService categoryService;
    private final GrowthSubCategoryService subCategoryService;

    @GetMapping("/{id}/edit")
    public String editSubCategory(@PathVariable Long id, Model model) {
        GrowthSubCategory subCategory = subCategoryService.get(id);
        model.addAttribute("subCategory", subCategory);
        model.addAttribute("subCategoryForm", GrowthSubCategoryForm.from(subCategory));
        model.addAttribute("categories", categoryService.findAll());
        return "growth-sub-categories/form";
    }

    @PostMapping("/{id}/edit")
    public String updateSubCategory(
            @PathVariable Long id,
            @ModelAttribute GrowthSubCategoryForm subCategoryForm,
            RedirectAttributes redirectAttributes
    ) {
        subCategoryService.update(id, subCategoryForm);
        redirectAttributes.addFlashAttribute("noticeMessage", "성장 중분류가 수정되었습니다.");
        return "redirect:/growth-categories";
    }

    @PostMapping("/{id}/delete")
    public String deleteSubCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            subCategoryService.delete(id);
            redirectAttributes.addFlashAttribute("noticeMessage", "성장 중분류가 삭제되었습니다.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/growth-categories";
    }
}
