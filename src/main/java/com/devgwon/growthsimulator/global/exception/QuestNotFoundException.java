package com.devgwon.growthsimulator.global.exception;

public class QuestNotFoundException extends RuntimeException {

    public QuestNotFoundException() {
        super("퀘스트를 찾을 수 없습니다.");
    }
}
