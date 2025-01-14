package com.example.coffeetica.coffee.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Region {
    AFRICA("Africa"),
    ASIA("Asia"),
    SOUTH_AMERICA("South America"),
    CENTRAL_AMERICA("Central America");

    private final String displayName;

    Region(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static Region fromDisplayName(String displayName) {
        for (Region region : values()) {
            if (region.displayName.equalsIgnoreCase(displayName)) {
                return region;
            }
        }
        throw new IllegalArgumentException("Invalid Region: " + displayName);
    }
}