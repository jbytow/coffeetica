package com.example.coffeetica.coffee.models.enums;

public enum RoastLevel {
    LIGHT("Light"),
    MEDIUM("Medium"),
    DARK("Dark");

    private final String displayName;

    RoastLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}