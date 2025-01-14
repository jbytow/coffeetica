package com.example.coffeetica.coffee.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FlavorProfile {
    BERRY("Berry"),
    CHOCOLATE("Chocolate"),
    CITRUS("Citrus"),
    DRIED_FRUIT("Dried Fruit"),
    EARTHY("Earthy"),
    FLORAL("Floral"),
    HERBAL("Herbal"),
    NUTTY("Nutty"),
    SMOKY("Smoky"),
    SPICE("Spice"),
    TROPICAL("Tropical"),
    WINE("Wine");

    private final String displayName;

    FlavorProfile(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static FlavorProfile fromDisplayName(String displayName) {
        for (FlavorProfile profile : values()) {
            if (profile.displayName.equalsIgnoreCase(displayName)) {
                return profile;
            }
        }
        throw new IllegalArgumentException("Invalid FlavorProfile: " + displayName);
    }
}