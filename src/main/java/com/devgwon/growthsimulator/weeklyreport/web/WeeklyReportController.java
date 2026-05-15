package com.devgwon.growthsimulator.weeklyreport.web;

import com.devgwon.growthsimulator.weeklyreport.service.WeeklyReportService;
import com.devgwon.growthsimulator.weeklyreport.service.WeeklyReportView;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WeeklyReportController {

    private final WeeklyReportService weeklyReportService;

    public WeeklyReportController(WeeklyReportService weeklyReportService) {
        this.weeklyReportService = weeklyReportService;
    }

    @GetMapping("/weekly-reports")
    public String report(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate weekStart,
            Model model
    ) {
        WeeklyReportView report = weekStart == null
                ? weeklyReportService.getCurrentWeekReport()
                : weeklyReportService.getReport(weekStart);
        model.addAttribute("report", report);
        return "weekly-reports/detail";
    }

    @GetMapping("/weekly-reports/current")
    public String current(Model model) {
        model.addAttribute("report", weeklyReportService.getCurrentWeekReport());
        return "weekly-reports/detail";
    }
}
