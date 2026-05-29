package com.devgwon.growthsimulator.controller;

import com.devgwon.growthsimulator.dto.request.BlogDraftGenerateRequest;
import com.devgwon.growthsimulator.dto.response.BlogDraftFormView;
import com.devgwon.growthsimulator.service.BlogDraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/blog-drafts")
public class BlogDraftController {
    private final BlogDraftService blogDraftService;

    @GetMapping
    public String blogDrafts(Model model) {
        BlogDraftFormView formView = blogDraftService.getFormView();
        model.addAttribute("draftRequest", formView.request());
        addFormView(model, formView);
        return "blog-drafts/index";
    }

    @PostMapping("/generate")
    public String generate(

            @ModelAttribute("draftRequest") BlogDraftGenerateRequest request,

            BindingResult bindingResult,

            Model model

    ) {
        if (bindingResult.hasErrors()) {
            addFormView(model, blogDraftService.getFormView());
            return "blog-drafts/index";
        }
        try {
            addFormView(model, blogDraftService.generate(request));
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("blogDraft.invalid", exception.getMessage());
            addFormView(model, blogDraftService.getFormView());
        }
        return "blog-drafts/index";
    }

    private void addFormView(Model model, BlogDraftFormView formView) {
        model.addAttribute("draft", formView.draft());
        model.addAttribute("sourceTypes", formView.sourceTypes());
        model.addAttribute("draftTypes", formView.draftTypes());
        model.addAttribute("dailyReviewOptions", formView.dailyReviewOptions());
        model.addAttribute("errorRecordOptions", formView.errorRecordOptions());
        model.addAttribute("growthLogOptions", formView.growthLogOptions());
        model.addAttribute("empty", formView.empty());
    }
}
