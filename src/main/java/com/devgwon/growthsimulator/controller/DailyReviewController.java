package com.devgwon.growthsimulator.controller;

import com.devgwon.growthsimulator.dto.request.DailyReviewCreateRequest;
import com.devgwon.growthsimulator.dto.request.DailyReviewUpdateRequest;
import com.devgwon.growthsimulator.entity.DailyReview;
import com.devgwon.growthsimulator.service.DailyReviewService;
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
@RequestMapping("/daily-reviews")
public class DailyReviewController {
    private final DailyReviewService dailyReviewService;
    private final GrowthCategoryService categoryService;
    private final GrowthSubCategoryService subCategoryService;

    @GetMapping
    public String reviews(Model model) {
        model.addAttribute("reviews", dailyReviewService.findReviewsForDefaultProfile());
        return "daily-reviews/list";
    }

    @GetMapping("/new")
    public String newReview(Model model, RedirectAttributes redirectAttributes) {
        return dailyReviewService.findTodayReviewForDefaultProfile()
                .map(review -> {
                    redirectAttributes.addFlashAttribute("noticeMessage", "오늘 회고는 이미 작성되어 있어요. 필요하면 수정해 주세요.");
                    return "redirect:/daily-reviews/" + review.getId() + "/edit";
                })
                .orElseGet(() -> {
                    model.addAttribute("reviewForm", dailyReviewService.newTodayRequest());
                    model.addAttribute("editMode", false);
                    addFormOptions(model);
                    return "daily-reviews/form";
                });
    }

    @PostMapping
    public String createReview(

            @ModelAttribute("reviewForm") DailyReviewCreateRequest request,

            RedirectAttributes redirectAttributes

    ) {
        DailyReview review = dailyReviewService.create(request);
        redirectAttributes.addFlashAttribute("noticeMessage", "오늘의 회고가 저장되었습니다.");
        return "redirect:/daily-reviews/" + review.getId();
    }

    @GetMapping("/{id}")
    public String reviewDetail(@PathVariable Long id, Model model) {
        model.addAttribute("review", dailyReviewService.getView(id));
        return "daily-reviews/detail";
    }

    @GetMapping("/{id}/edit")
    public String editReview(@PathVariable Long id, Model model) {
        DailyReview review = dailyReviewService.get(id);
        model.addAttribute("review", review);
        model.addAttribute("reviewForm", DailyReviewUpdateRequest.from(review));
        model.addAttribute("editMode", true);
        addFormOptions(model);
        return "daily-reviews/form";
    }

    @PostMapping("/{id}/edit")
    public String updateReview(

            @PathVariable Long id,

            @ModelAttribute("reviewForm") DailyReviewUpdateRequest request,

            RedirectAttributes redirectAttributes

    ) {
        dailyReviewService.update(id, request);
        redirectAttributes.addFlashAttribute("noticeMessage", "회고가 수정되었습니다.");
        return "redirect:/daily-reviews/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        dailyReviewService.delete(id);
        redirectAttributes.addFlashAttribute("noticeMessage", "회고가 삭제되었습니다.");
        return "redirect:/daily-reviews";
    }

    private void addFormOptions(Model model) {
        model.addAttribute("growthCategories", categoryService.findAll());
        model.addAttribute("growthSubCategories", subCategoryService.findAllForSelection());
        model.addAttribute("moodScores", new int[]{1, 2, 3, 4, 5});
    }
}
