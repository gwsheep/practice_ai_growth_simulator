package com.devgwon.growthsimulator.controller;

import com.devgwon.growthsimulator.dto.response.DashboardView;
import com.devgwon.growthsimulator.service.CharacterMessageService;
import com.devgwon.growthsimulator.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class DashboardController {
    private final DashboardService dashboardService;
    private final CharacterMessageService characterMessageService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        DashboardView dashboard = dashboardService.getDashboard();
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("profile", dashboard.profile());
        model.addAttribute("growthCategories", dashboard.growthCategories());
        model.addAttribute("quests", dashboard.quests());
        if (!model.containsAttribute("characterMessage")) {
            model.addAttribute("characterMessage", characterMessageService.defaultMessage());
        }
        return "dashboard/index";
    }
}
