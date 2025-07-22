package com.samyukgu.what2wear.codi.model;

public enum CodiScope {
    PUBLIC(0), FRIENDS(1), PRIVATE(2);

    private final int value;

    CodiScope(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CodiScope fromInt(int value) {
        return switch (value) {
            case 0 -> PUBLIC;
            case 1 -> FRIENDS;
            case 2 -> PRIVATE;
            default -> throw new IllegalArgumentException("Unknown scope value: " + value);
        };
    }
}