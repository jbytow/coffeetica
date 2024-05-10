package com.example.coffeetica.util;

import com.example.coffeetica.model.CoffeeDTO;
import com.example.coffeetica.model.CoffeeEntity;

public final class TestData {

    private TestData() {
    }
    public static CoffeeEntity createTestCoffeeEntity() {
        CoffeeEntity coffeeEntity = new CoffeeEntity();
        coffeeEntity.setId(1L);
        coffeeEntity.setName("Nutty Brazil");
        coffeeEntity.setCountryOfOrigin("Brazil");
        coffeeEntity.setRegion("Minas Gerais");
        coffeeEntity.setRoastery("Best Roastery");
        coffeeEntity.setRoastLevel("Medium");
        coffeeEntity.setFlavorProfile("Nutty");
        coffeeEntity.setNotes("Notes of chocolate");
        coffeeEntity.setProcessingMethod("Washed");
        coffeeEntity.setProductionYear(2024);
        return coffeeEntity;
    }

    public static CoffeeDTO createTestCoffeeDTO() {
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
