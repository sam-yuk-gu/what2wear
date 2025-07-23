package com.samyukgu.what2wear.codi.model;

import lombok.Getter;

@Getter
public enum CodiScope {
    PUBLIC(0), FRIENDS(1), PRIVATE(2);

    private final int value;

    CodiScope(int value) {
        this.value = value;
    }

    public static CodiScope fromInt(int value) {
        return switch (value) {
            case 0 -> PUBLIC;
            case 1 -> FRIENDS;
            case 2 -> PRIVATE;
            default -> throw new IllegalArgumentException("Unknown scope value: " + value);
        };
    }

    public static CodiScope fromString(String code) {
        return switch (code) {
            case "0" -> PUBLIC;
            case "1" -> FRIENDS;
            case "2" -> PRIVATE;
            default -> PRIVATE;
        };
    }

    public String toLabel() {
        return switch (this) {
            case PUBLIC -> "전체공개";
            case FRIENDS -> "친구공개";
            case PRIVATE -> "비공개";
        };
    }
}