package com.example.coffeetica.coffee.models.enums;

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

    public String getDisplayName() {
        return displayName;
    }
}