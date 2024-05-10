package com.example.coffeetica.util;

import com.example.coffeetica.model.CoffeeDTO;

public class TestData {

    public static CoffeeDTO createTestCoffee() {
        CoffeeDTO coffeeDTO = new CoffeeDTO();
        coffeeDTO.setId(1L);
        coffeeDTO.setName("Nutty Brazil");
        coffeeDTO.setCountryOfOrigin("Brazil");
        coffeeDTO.setRegion("Minas Gerais");
        coffeeDTO.setRoastery("Best Roastery");
        coffeeDTO.setRoastLevel("Medium");
        coffeeDTO.setFlavorProfile("Nutty");
        coffeeDTO.setNotes("Notes of chocolate");
        coffeeDTO.setProcessingMethod("Washed");
        coffeeDTO.setProductionYear(2024);
        return coffeeDTO;
    }
}
