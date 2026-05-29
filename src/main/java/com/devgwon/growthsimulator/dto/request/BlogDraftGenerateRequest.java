package com.devgwon.growthsimulator.dto.request;

import com.devgwon.growthsimulator.common.BlogDraftSourceType;
import com.devgwon.growthsimulator.common.BlogDraftType;

public class BlogDraftGenerateRequest {
    private BlogDraftSourceType sourceType;
    private BlogDraftType draftType;
    private Long sourceId;

    public static BlogDraftGenerateRequest createDefault() {
        BlogDraftGenerateRequest request = new BlogDraftGenerateRequest();
        request.setSourceType(BlogDraftSourceType.DAILY_REVIEW);
        request.setDraftType(BlogDraftType.RETROSPECTIVE);
        return request;
    }

    public BlogDraftSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(BlogDraftSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public BlogDraftType getDraftType() {
        return draftType;
    }

    public void setDraftType(BlogDraftType draftType) {
        this.draftType = draftType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }
}
