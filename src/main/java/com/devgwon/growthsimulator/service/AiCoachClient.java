package com.devgwon.growthsimulator.service;

import com.devgwon.growthsimulator.dto.request.AiCoachRequest;
import com.devgwon.growthsimulator.dto.response.AiCoachResponse;

public interface AiCoachClient {
    AiCoachResponse generate(AiCoachRequest request);
}
