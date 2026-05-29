package com.devgwon.growthsimulator.controller;

import com.devgwon.growthsimulator.dto.request.ErrorRecordForm;
import com.devgwon.growthsimulator.dto.response.ErrorRecordFormView;
import com.devgwon.growthsimulator.entity.ErrorStatus;
import com.devgwon.growthsimulator.service.ErrorRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@RequestMapping("/errors")
public class ErrorRecordController {
    private final ErrorRecordService errorRecordService;

    @GetMapping
    public String errors(
            @RequestParam(required = false) ErrorStatus status,
            @RequestParam(required = false) String keyword,
            Model model
    ) {
        model.addAttribute("errors", errorRecordService.findList(status, keyword));
        model.addAttribute("statuses", ErrorStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);
        return "errors/list";
    }

    @GetMapping("/new")
    public String newError(Model model) {
        addFormView(model, errorRecordService.createFormView());
        return "errors/form";
    }

    @PostMapping
    public String create(
            @ModelAttribute("errorForm") ErrorRecordForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return createForm(model);
        }
        Long id;
        try {
            id = errorRecordService.create(form).getId();
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("errorRecord.invalid", exception.getMessage());
            return createForm(model);
        }
        redirectAttributes.addFlashAttribute("noticeMessage", "새 에러 수집품을 박물관에 올렸어요.");
        return "redirect:/errors/" + id;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("errorRecord", errorRecordService.findDetail(id));
        return "errors/detail";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        addFormView(model, errorRecordService.updateFormView(id));
        return "errors/form";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @ModelAttribute("errorForm") ErrorRecordForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return updateForm(id, model);
        }
        try {
            errorRecordService.update(id, form);
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("errorRecord.invalid", exception.getMessage());
            return updateForm(id, model);
        }
        redirectAttributes.addFlashAttribute("noticeMessage", "에러 기록을 수정했습니다.");
        return "redirect:/errors/" + id;
    }

    @PostMapping("/{id}/resolve")
    public String resolve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        errorRecordService.resolve(id);
        redirectAttributes.addFlashAttribute("noticeMessage", "에러를 해결됨으로 표시했습니다.");
        return "redirect:/errors/" + id;
    }

    @PostMapping("/{id}/archive")
    public String archive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        errorRecordService.archive(id);
        redirectAttributes.addFlashAttribute("noticeMessage", "에러 기록을 보관함으로 옮겼습니다.");
        return "redirect:/errors";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        errorRecordService.delete(id);
        redirectAttributes.addFlashAttribute("noticeMessage", "에러 기록을 삭제했습니다.");
        return "redirect:/errors";
    }

    private void addFormView(Model model, ErrorRecordFormView formView) {
        model.addAttribute("errorForm", formView.form());
        model.addAttribute("editMode", formView.editMode());
        model.addAttribute("errorRecordId", formView.errorRecordId());
        addFormOptions(model, formView);
    }

    private String createForm(Model model) {
        ErrorRecordFormView formView = errorRecordService.createFormView();
        model.addAttribute("editMode", false);
        model.addAttribute("errorRecordId", null);
        addFormOptions(model, formView);
        return "errors/form";
    }

    private String updateForm(Long id, Model model) {
        ErrorRecordFormView formView = errorRecordService.updateFormView(id);
        model.addAttribute("editMode", true);
        model.addAttribute("errorRecordId", id);
        addFormOptions(model, formView);
        return "errors/form";
    }

    private void addFormOptions(Model model, ErrorRecordFormView formView) {
        model.addAttribute("statuses", formView.statuses());
        model.addAttribute("severities", formView.severities());
        model.addAttribute("growthSubCategories", formView.growthSubCategories());
        model.addAttribute("quests", formView.quests());
    }
}
