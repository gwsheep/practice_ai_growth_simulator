package com.devgwon.growthsimulator.controller;

import com.devgwon.growthsimulator.dto.request.ScheduleTypeForm;
import com.devgwon.growthsimulator.entity.ScheduleType;
import com.devgwon.growthsimulator.service.ScheduleTypeService;
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
@RequestMapping("/schedule-types")
public class ScheduleTypeController {
    private final ScheduleTypeService scheduleTypeService;

    @GetMapping
    public String scheduleTypes(Model model) {
        model.addAttribute("scheduleTypes", scheduleTypeService.findAll());
        return "schedule-types/list";
    }

    @GetMapping("/new")
    public String newScheduleType(Model model) {
        model.addAttribute("scheduleTypeForm", new ScheduleTypeForm());
        model.addAttribute("editMode", false);
        return "schedule-types/form";
    }

    @PostMapping
    public String create(

            @ModelAttribute ScheduleTypeForm scheduleTypeForm,

            RedirectAttributes redirectAttributes

    ) {
        scheduleTypeService.create(scheduleTypeForm);
        redirectAttributes.addFlashAttribute("noticeMessage", "새 일정 타입이 추가되었습니다.");
        return "redirect:/schedule-types";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        ScheduleType scheduleType = scheduleTypeService.get(id);
        model.addAttribute("scheduleType", scheduleType);
        model.addAttribute("scheduleTypeForm", ScheduleTypeForm.from(scheduleType));
        model.addAttribute("editMode", true);
        return "schedule-types/form";
    }

    @PostMapping("/{id}/edit")
    public String update(

            @PathVariable Long id,

            @ModelAttribute ScheduleTypeForm scheduleTypeForm,

            RedirectAttributes redirectAttributes

    ) {
        scheduleTypeService.update(id, scheduleTypeForm);
        redirectAttributes.addFlashAttribute("noticeMessage", "일정 타입이 수정되었습니다.");
        return "redirect:/schedule-types";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        scheduleTypeService.deactivate(id);
        redirectAttributes.addFlashAttribute("noticeMessage", "일정 타입이 비활성화되었습니다.");
        return "redirect:/schedule-types";
    }
}
