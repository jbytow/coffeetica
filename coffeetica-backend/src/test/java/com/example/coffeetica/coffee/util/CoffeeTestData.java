package com.example.coffeetica.coffee.util;

import com.example.coffeetica.coffee.models.*;
import com.example.coffeetica.coffee.models.enums.FlavorProfile;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.models.enums.RoastLevel;
import com.example.coffeetica.user.util.UserTestData;

import java.util.HashSet;
import java.util.List;


public final class CoffeeTestData {

    private CoffeeTestData() {
    }

    public static RoasteryEntity createTestRoasteryEntity() {
        RoasteryEntity roastery = new RoasteryEntity();
        roastery.setId(1L);
        roastery.setName("Best Roastery");
        roastery.setCountry("Brazil, Minas Gerais");
        roastery.setFoundingYear(2010);
        roastery.setWebsiteUrl("http://bestroastery.com");
        return roastery;
    }

    public static RoasteryDTO createTestRoasteryDTO() {
        RoasteryDTO roasteryDTO = new RoasteryDTO();
        roasteryDTO.setId(1L);
        roasteryDTO.setName("Best Roastery");
        roasteryDTO.setCountry("Brazil, Minas Gerais");
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
        coffeeEntity.setFlavorNotes(new HashSet<>(List.of("Chocolate", "Nuts")));
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
        coffeeDTO.setFlavorNotes(new HashSet<>(List.of("Chocolate", "Nuts")));
        coffeeDTO.setProcessingMethod("Washed");
        coffeeDTO.setProductionYear(2024);
        coffeeDTO.setRoastery(createTestRoasteryDTO());
        return coffeeDTO;
    }

    public static CoffeeDetailsDTO createTestCoffeeDetailsDTO() {
        CoffeeDetailsDTO coffeeDetailsDTO = new CoffeeDetailsDTO();
        coffeeDetailsDTO.setId(1L);
        coffeeDetailsDTO.setName("Nutty Brazil");
        coffeeDetailsDTO.setCountryOfOrigin("Brazil");
        coffeeDetailsDTO.setRegion(Region.SOUTH_AMERICA);
        coffeeDetailsDTO.setRoastLevel(RoastLevel.MEDIUM);
        coffeeDetailsDTO.setFlavorProfile(FlavorProfile.NUTTY);
        coffeeDetailsDTO.setFlavorNotes(new HashSet<>(List.of("Chocolate", "Nuts")));
        coffeeDetailsDTO.setProcessingMethod("Washed");
        coffeeDetailsDTO.setProductionYear(2024);
        coffeeDetailsDTO.setRoastery(createTestRoasteryDTO());

        coffeeDetailsDTO.setAverageRating(4.8);
        coffeeDetailsDTO.setTotalReviewsCount(123);
        coffeeDetailsDTO.setLatestReviews(List.of(createTestReviewDTO()));

        return coffeeDetailsDTO;
    }

    public static ReviewEntity createTestReviewEntity() {
        ReviewEntity review = new ReviewEntity();
        review.setId(1L);
        review.setContent("Great coffee with a nutty flavor.");
        review.setBrewingMethod("Espresso");
        review.setBrewingDescription("Brewed with a professional espresso machine.");
        review.setRating(5D);
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
        reviewDTO.setRating(5D);
        reviewDTO.setCoffeeId(1L);
        return reviewDTO;
    }

    public static ReviewRequestDTO createTestReviewRequestDTO() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setCoffeeId(1L);
        request.setRating(5.0);
        request.setContent("Great coffee with a nutty flavor.");
        request.setBrewingMethod("Espresso");
        request.setBrewingDescription("Brewed with a professional espresso machine.");
        return request;
    }
}
