package com.devgwon.growthsimulator.controller;

import com.devgwon.growthsimulator.dto.request.ScheduleCreateRequest;
import com.devgwon.growthsimulator.dto.request.ScheduleUpdateRequest;
import com.devgwon.growthsimulator.entity.Schedule;
import com.devgwon.growthsimulator.entity.ScheduleStatus;
import com.devgwon.growthsimulator.entity.ScheduleType;
import com.devgwon.growthsimulator.service.GrowthCategoryService;
import com.devgwon.growthsimulator.service.GrowthSubCategoryService;
import com.devgwon.growthsimulator.service.ScheduleService;
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
@RequestMapping("/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final ScheduleTypeService scheduleTypeService;
    private final GrowthCategoryService categoryService;
    private final GrowthSubCategoryService subCategoryService;

    @GetMapping
    public String schedules(Model model) {
        model.addAttribute("schedules", scheduleService.findSchedulesForDefaultProfile());
        model.addAttribute("todaySchedules", scheduleService.findTodaySchedulesForDefaultProfile());
        model.addAttribute("weekSchedules", scheduleService.findWeekSchedulesForDefaultProfile());
        return "schedules/list";
    }

    @GetMapping("/new")
    public String newSchedule(Model model) {
        ScheduleCreateRequest request = new ScheduleCreateRequest();
        scheduleTypeService.findActiveTypes().stream()
                .findFirst()
                .map(ScheduleType::getId)
                .ifPresent(request::setScheduleTypeId);
        model.addAttribute("scheduleForm", request);
        addFormOptions(model);
        model.addAttribute("editMode", false);
        return "schedules/form";
    }

    @PostMapping
    public String createSchedule(

            @ModelAttribute("scheduleForm") ScheduleCreateRequest request,

            RedirectAttributes redirectAttributes

    ) {
        scheduleService.create(request);
        redirectAttributes.addFlashAttribute("noticeMessage", "새 일정이 등록되었습니다.");
        return "redirect:/schedules";
    }

    @GetMapping("/{id}/edit")
    public String editSchedule(@PathVariable Long id, Model model) {
        Schedule schedule = scheduleService.getSchedule(id);
        model.addAttribute("schedule", schedule);
        model.addAttribute("scheduleForm", ScheduleUpdateRequest.from(schedule));
        addFormOptions(model);
        model.addAttribute("editMode", true);
        return "schedules/form";
    }

    @PostMapping("/{id}/edit")
    public String updateSchedule(

            @PathVariable Long id,

            @ModelAttribute("scheduleForm") ScheduleUpdateRequest request,

            RedirectAttributes redirectAttributes

    ) {
        scheduleService.update(id, request);
        redirectAttributes.addFlashAttribute("noticeMessage", "일정이 수정되었습니다.");
        return "redirect:/schedules";
    }

    @PostMapping("/{id}/done")
    public String doneSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        scheduleService.done(id);
        redirectAttributes.addFlashAttribute("noticeMessage", "일정을 완료했습니다. 필요하면 연결된 퀘스트도 클리어해 주세요.");
        return "redirect:/schedules";
    }

    @PostMapping("/{id}/delete")
    public String deleteSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        scheduleService.delete(id);
        redirectAttributes.addFlashAttribute("noticeMessage", "일정이 삭제되었습니다.");
        return "redirect:/schedules";
    }

    private void addFormOptions(Model model) {
        model.addAttribute("scheduleTypes", scheduleTypeService.findActiveTypes());
        model.addAttribute("scheduleStatuses", ScheduleStatus.values());
        model.addAttribute("growthCategories", categoryService.findAll());
        model.addAttribute("growthSubCategories", subCategoryService.findAllForSelection());
    }
}
