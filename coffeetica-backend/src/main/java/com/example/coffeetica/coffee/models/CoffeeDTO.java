package com.example.coffeetica.coffee.models;

import com.example.coffeetica.coffee.models.enums.FlavorProfile;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.models.enums.RoastLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CoffeeDTO {

    private Long id;
    private String name;
    private String countryOfOrigin;
    private Region region;
    private RoastLevel roastLevel;
    private FlavorProfile flavorProfile;
    private Set<String> flavorNotes;
    private String processingMethod;
    private Integer productionYear;

    private String imageUrl; // URL of the uploaded image

    private RoasteryDTO roastery;

    private List<ReviewDTO> reviews = new ArrayList<>();

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public RoastLevel getRoastLevel() {
        return roastLevel;
    }

    public void setRoastLevel(RoastLevel roastLevel) {
        this.roastLevel = roastLevel;
    }

    public FlavorProfile getFlavorProfile() {
        return flavorProfile;
    }

    public void setFlavorProfile(FlavorProfile flavorProfile) {
        this.flavorProfile = flavorProfile;
    }

    // Getter
    public Set<String> getFlavorNotes() {
        return flavorNotes;
    }

    // Setter
    public void setFlavorNotes(Set<String> flavorNotes) {
        this.flavorNotes = flavorNotes;
    }

    public String getProcessingMethod() {
        return processingMethod;
    }

    public void setProcessingMethod(String processingMethod) {
        this.processingMethod = processingMethod;
    }

    public Integer getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(Integer productionYear) {
        this.productionYear = productionYear;
    }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public RoasteryDTO getRoastery() {
        return roastery;
    }

    public void setRoastery(RoasteryDTO roastery) {
        this.roastery = roastery;
    }

    public List<ReviewDTO> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDTO> reviews) {
        this.reviews = reviews;
    }
}

