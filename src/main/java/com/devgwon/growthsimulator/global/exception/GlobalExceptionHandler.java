package com.devgwon.growthsimulator.global.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ProfileNotFoundException.class,
            QuestNotFoundException.class,
            QuestAlreadyCompletedException.class
    })
    public String handleGrowthSimulatorException(RuntimeException exception, Model model) {
        model.addAttribute("errorTitle", "백둥이가 잠깐 멈췄어요.");
        model.addAttribute("errorMessage", exception.getMessage());
        model.addAttribute("guideMessage", "대시보드로 돌아가서 현재 퀘스트 상태를 다시 확인해 주세요.");
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException exception, Model model) {
        model.addAttribute("errorTitle", "입력값을 다시 확인해 주세요.");
        model.addAttribute("errorMessage", exception.getMessage());
        model.addAttribute("guideMessage", "필수 항목이 빠졌거나 선택값이 올바르지 않을 수 있습니다.");
        return "error";
    }
}
