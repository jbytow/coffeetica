package com.example.coffeetica.coffee.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoastLevel {
    LIGHT("Light"),
    MEDIUM("Medium"),
    DARK("Dark");

    private final String displayName;

    RoastLevel(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static RoastLevel fromDisplayName(String displayName) {
        for (RoastLevel level : values()) {
            if (level.displayName.equalsIgnoreCase(displayName)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid RoastLevel: " + displayName);
    }
}