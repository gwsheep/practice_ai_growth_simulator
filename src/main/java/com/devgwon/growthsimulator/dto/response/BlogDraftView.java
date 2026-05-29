package com.devgwon.growthsimulator.dto.response;

public record BlogDraftView(
        String title,
        String sourceLabel,
        String draftTypeLabel,
        String markdown,
        String guideMessage,
        boolean empty
) {
    public static BlogDraftView empty(String guideMessage) {
        return new BlogDraftView(
                "아직 초안이 없습니다.",
                "선택한 소스 없음",
                "템플릿",
                "",
                guideMessage,
                true
        );
    }
}
