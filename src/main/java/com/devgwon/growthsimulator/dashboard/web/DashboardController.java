package com.devgwon.growthsimulator.dashboard.web;

import com.devgwon.growthsimulator.character.service.CharacterMessageService;
import com.devgwon.growthsimulator.dashboard.service.DashboardService;
import com.devgwon.growthsimulator.dashboard.service.DashboardView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final CharacterMessageService characterMessageService;

    public DashboardController(
            DashboardService dashboardService,
            CharacterMessageService characterMessageService
    ) {
        this.dashboardService = dashboardService;
        this.characterMessageService = characterMessageService;
    }

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
