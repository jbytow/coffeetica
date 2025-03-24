package com.example.coffeetica.coffee.models;

import com.example.coffeetica.coffee.models.enums.FlavorProfile;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.models.enums.RoastLevel;

import java.util.List;
import java.util.Set;

/**
 * A specialized DTO for providing detailed coffee information,
 * including aggregated fields such as average rating and the latest reviews.
 */
public class CoffeeDetailsDTO {

    private Long id;
    private String name;
    private String countryOfOrigin;
    private Region region;
    private RoastLevel roastLevel;
    private FlavorProfile flavorProfile;
    private Set<String> flavorNotes;
    private String processingMethod;
    private Integer productionYear;
    private String imageUrl;

    private RoasteryDTO roastery;
    private Double averageRating;
    private Integer totalReviewsCount;
    private List<ReviewDTO> latestReviews;

    public CoffeeDetailsDTO() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public Region getRegion() {
        return region;
    }

    public RoastLevel getRoastLevel() {
        return roastLevel;
    }

    public FlavorProfile getFlavorProfile() {
        return flavorProfile;
    }

    public Set<String> getFlavorNotes() {
        return flavorNotes;
    }

    public String getProcessingMethod() {
        return processingMethod;
    }

    public Integer getProductionYear() {
        return productionYear;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public RoasteryDTO getRoastery() {
        return roastery;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public Integer getTotalReviewsCount() {
        return totalReviewsCount;
    }

    public List<ReviewDTO> getLatestReviews() {
        return latestReviews;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public void setRoastLevel(RoastLevel roastLevel) {
        this.roastLevel = roastLevel;
    }

    public void setFlavorProfile(FlavorProfile flavorProfile) {
        this.flavorProfile = flavorProfile;
    }

    public void setFlavorNotes(Set<String> flavorNotes) {
        this.flavorNotes = flavorNotes;
    }

    public void setProcessingMethod(String processingMethod) {
        this.processingMethod = processingMethod;
    }

    public void setProductionYear(Integer productionYear) {
        this.productionYear = productionYear;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setRoastery(RoasteryDTO roastery) {
        this.roastery = roastery;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public void setTotalReviewsCount(Integer totalReviewsCount) {
        this.totalReviewsCount = totalReviewsCount;
    }

    public void setLatestReviews(List<ReviewDTO> latestReviews) {
        this.latestReviews = latestReviews;
    }
}