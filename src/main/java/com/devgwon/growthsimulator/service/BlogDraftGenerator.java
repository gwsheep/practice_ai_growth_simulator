package com.devgwon.growthsimulator.service;

import com.devgwon.growthsimulator.common.BlogDraftType;
import com.devgwon.growthsimulator.dto.response.BlogDraftView;
import com.devgwon.growthsimulator.entity.DailyReview;
import com.devgwon.growthsimulator.entity.ErrorRecord;
import com.devgwon.growthsimulator.entity.GrowthLog;
import java.util.List;

public interface BlogDraftGenerator {
    BlogDraftView generateFromDailyReview(DailyReview review, BlogDraftType draftType);

    BlogDraftView generateFromErrorRecord(ErrorRecord errorRecord, BlogDraftType draftType);

    BlogDraftView generateFromGrowthLogs(List<GrowthLog> growthLogs, BlogDraftType draftType);
}
