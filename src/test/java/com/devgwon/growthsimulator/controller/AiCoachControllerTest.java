package com.devgwon.growthsimulator.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
class AiCoachControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void aiCoachShowsCoachModel() throws Exception {
        mockMvc.perform(get("/ai-coach"))
                .andExpect(status().isOk())
                .andExpect(view().name("ai-coach/index"))
                .andExpect(model().attributeExists("coach"));
    }
}
