package com.example.coffeetica.coffee.util;

import com.example.coffeetica.coffee.models.*;
import com.example.coffeetica.coffee.models.enums.FlavorProfile;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.models.enums.RoastLevel;
import com.example.coffeetica.user.util.UserTestData;


public final class CoffeeTestData {

    private CoffeeTestData() {
    }

    public static RoasteryEntity createTestRoasteryEntity() {
        RoasteryEntity roastery = new RoasteryEntity();
        roastery.setId(1L);
        roastery.setName("Best Roastery");
        roastery.setLocation("Brazil, Minas Gerais");
        roastery.setFoundingYear(2010);
        roastery.setWebsiteUrl("http://bestroastery.com");
        return roastery;
    }

    public static RoasteryDTO createTestRoasteryDTO() {
        RoasteryDTO roasteryDTO = new RoasteryDTO();
        roasteryDTO.setId(1L);
        roasteryDTO.setName("Best Roastery");
        roasteryDTO.setLocation("Brazil, Minas Gerais");
        roasteryDTO.setFoundingYear(2010);
        roasteryDTO.setWebsiteUrl("http://bestroastery.com");
        return roasteryDTO;
    }

    public static CoffeeEntity createTestCoffeeEntity() {
        CoffeeEntity coffeeEntity = new CoffeeEntity();
        coffeeEntity.setId(1L);
        coffeeEntity.setName("Nutty Brazil");
        coffeeEntity.setCountryOfOrigin("Brazil");
        coffeeEntity.setRegion(Region.SOUTH_AMERICA);
        coffeeEntity.setRoastLevel(RoastLevel.MEDIUM);
        coffeeEntity.setFlavorProfile(FlavorProfile.NUTTY);
        coffeeEntity.setNotes("Notes of chocolate");
        coffeeEntity.setProcessingMethod("Washed");
        coffeeEntity.setProductionYear(2024);
        coffeeEntity.setRoastery(createTestRoasteryEntity());
        return coffeeEntity;
    }

    public static CoffeeDTO createTestCoffeeDTO() {
        CoffeeDTO coffeeDTO = new CoffeeDTO();
        coffeeDTO.setId(1L);
        coffeeDTO.setName("Nutty Brazil");
        coffeeDTO.setCountryOfOrigin("Brazil");
        coffeeDTO.setRegion(Region.SOUTH_AMERICA);
        coffeeDTO.setRoastLevel(RoastLevel.MEDIUM);
        coffeeDTO.setFlavorProfile(FlavorProfile.NUTTY);
        coffeeDTO.setNotes("Notes of chocolate");
        coffeeDTO.setProcessingMethod("Washed");
        coffeeDTO.setProductionYear(2024);
        coffeeDTO.setRoastery(createTestRoasteryDTO());
        return coffeeDTO;
    }

    public static ReviewEntity createTestReviewEntity() {
        ReviewEntity review = new ReviewEntity();
        review.setId(1L);
        review.setContent("Great coffee with a nutty flavor.");
        review.setBrewingMethod("Espresso");
        review.setBrewingDescription("Brewed with a professional espresso machine.");
        review.setRating(5);
        review.setCoffee(createTestCoffeeEntity());
        review.setUser(UserTestData.createTestUserEntity());
        return review;
    }

    public static ReviewDTO createTestReviewDTO() {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setId(1L);
        reviewDTO.setContent("Great coffee with a nutty flavor.");
        reviewDTO.setBrewingMethod("Espresso");
        reviewDTO.setBrewingDescription("Brewed with a professional espresso machine.");
        reviewDTO.setRating(5);
        reviewDTO.setCoffeeId(1L);
        return reviewDTO;
    }
}
