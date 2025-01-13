package com.example.coffeetica.coffee.models.enums;

public enum Region {
    AFRICA("Africa"),
    ASIA("Asia"),
    SOUTH_AMERICA("South America"),
    CENTRAL_AMERICA("Central America");

    private final String displayName;

    Region(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}