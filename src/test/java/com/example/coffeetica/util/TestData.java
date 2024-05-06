package com.example.coffeetica.util;

import com.example.coffeetica.model.Coffee;

public class TestData {

    public static Coffee createTestCoffee() {
        Coffee coffee = new Coffee();
        coffee.setId(1L);
        coffee.setName("Nutty Brazil");
        coffee.setCountryOfOrigin("Brazil");
        coffee.setRegion("Minas Gerais");
        coffee.setRoastery("Best Roastery");
        coffee.setRoastLevel("Medium");
        coffee.setFlavorProfile("Nutty");
        coffee.setNotes("Notes of chocolate");
        coffee.setProcessingMethod("Washed");
        coffee.setProductionYear(2024);
        return coffee;
    }
}
