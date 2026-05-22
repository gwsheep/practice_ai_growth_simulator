package com.devgwon.growthsimulator.exception;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException() {
        super("프로필을 찾을 수 없습니다.");
    }

    public ProfileNotFoundException(String message) {
        super(message);
    }
}
