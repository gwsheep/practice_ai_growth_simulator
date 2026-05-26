package com.devgwon.growthsimulator.controller;

import com.devgwon.growthsimulator.service.AiCoachService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/ai-coach")
public class AiCoachController {
    private final AiCoachService aiCoachService;

    @GetMapping
    public String aiCoach(Model model) {
        model.addAttribute("coach", aiCoachService.getCoachForDefaultProfile());
        return "ai-coach/index";
    }
}
