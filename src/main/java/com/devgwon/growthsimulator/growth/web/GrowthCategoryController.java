package com.devgwon.growthsimulator.growth.web;

import com.devgwon.growthsimulator.growth.domain.GrowthCategory;
import com.devgwon.growthsimulator.growth.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.growth.service.GrowthCategoryForm;
import com.devgwon.growthsimulator.growth.service.GrowthCategoryService;
import com.devgwon.growthsimulator.growth.service.GrowthSubCategoryForm;
import com.devgwon.growthsimulator.growth.service.GrowthSubCategoryService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/growth-categories")
public class GrowthCategoryController {

    private final GrowthCategoryService categoryService;
    private final GrowthSubCategoryService subCategoryService;
    private final GrowthSubCategoryRepository subCategoryRepository;

    public GrowthCategoryController(
            GrowthCategoryService categoryService,
            GrowthSubCategoryService subCategoryService,
            GrowthSubCategoryRepository subCategoryRepository
    ) {
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;
        this.subCategoryRepository = subCategoryRepository;
    }

    @GetMapping
    public String categories(Model model) {
        List<GrowthCategory> categories = categoryService.findAll();
        model.addAttribute("categoryViews", categories.stream()
                .map(category -> new GrowthCategoryView(
                        category,
                        subCategoryRepository.findByCategoryOrderBySortOrderAsc(category)
                ))
                .toList());
        return "growth-categories/index";
    }

    @GetMapping("/new")
    public String newCategory(Model model) {
        model.addAttribute("categoryForm", new GrowthCategoryForm());
        return "growth-categories/form";
    }

    @PostMapping
    public String createCategory(
            @ModelAttribute GrowthCategoryForm categoryForm,
            RedirectAttributes redirectAttributes
    ) {
        categoryService.create(categoryForm);
        redirectAttributes.addFlashAttribute("noticeMessage", "새 성장 대분류가 추가되었습니다.");
        return "redirect:/growth-categories";
    }

    @GetMapping("/{id}/edit")
    public String editCategory(@PathVariable Long id, Model model) {
        GrowthCategory category = categoryService.get(id);
        model.addAttribute("category", category);
        model.addAttribute("categoryForm", GrowthCategoryForm.from(category));
        return "growth-categories/form";
    }

    @PostMapping("/{id}/edit")
    public String updateCategory(
            @PathVariable Long id,
            @ModelAttribute GrowthCategoryForm categoryForm,
            RedirectAttributes redirectAttributes
    ) {
        categoryService.update(id, categoryForm);
        redirectAttributes.addFlashAttribute("noticeMessage", "성장 대분류가 수정되었습니다.");
        return "redirect:/growth-categories";
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("noticeMessage", "성장 대분류가 삭제되었습니다.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/growth-categories";
    }

    @GetMapping("/{categoryId}/sub-categories/new")
    public String newSubCategory(@PathVariable Long categoryId, Model model) {
        GrowthCategory category = categoryService.get(categoryId);
        GrowthSubCategoryForm form = new GrowthSubCategoryForm();
        form.setCategoryId(category.getId());
        model.addAttribute("category", category);
        model.addAttribute("subCategoryForm", form);
        model.addAttribute("categories", categoryService.findAll());
        return "growth-sub-categories/form";
    }

    @PostMapping("/{categoryId}/sub-categories")
    public String createSubCategory(
            @PathVariable Long categoryId,
            @ModelAttribute GrowthSubCategoryForm subCategoryForm,
            RedirectAttributes redirectAttributes
    ) {
        subCategoryService.create(categoryId, subCategoryForm);
        redirectAttributes.addFlashAttribute("noticeMessage", "새 성장 중분류가 추가되었습니다.");
        return "redirect:/growth-categories";
    }
}
