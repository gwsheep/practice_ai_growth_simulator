package com.devgwon.growthsimulator.dto.response;

import com.devgwon.growthsimulator.common.BlogDraftSourceType;
import com.devgwon.growthsimulator.common.BlogDraftType;
import com.devgwon.growthsimulator.dto.request.BlogDraftGenerateRequest;
import java.util.List;

public record BlogDraftFormView(
        BlogDraftGenerateRequest request,
        BlogDraftView draft,
        List<BlogDraftSourceType> sourceTypes,
        List<BlogDraftType> draftTypes,
        List<BlogDraftSourceOption> dailyReviewOptions,
        List<BlogDraftSourceOption> errorRecordOptions,
        List<BlogDraftSourceOption> growthLogOptions,
        boolean empty
) {
}
