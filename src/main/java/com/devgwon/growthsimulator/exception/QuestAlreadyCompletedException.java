package com.devgwon.growthsimulator.exception;

public class QuestAlreadyCompletedException extends RuntimeException {
    public QuestAlreadyCompletedException() {
        super("이미 완료된 퀘스트입니다.");
    }
}
