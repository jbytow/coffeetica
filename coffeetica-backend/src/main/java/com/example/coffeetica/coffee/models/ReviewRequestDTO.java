package com.example.coffeetica.coffee.models;

import jakarta.validation.constraints.*;

/**
 * A specialized request DTO used when creating or updating reviews.
 * Includes validation constraints to ensure required fields are present and valid.
 */
public class ReviewRequestDTO {

    @NotNull(message = "Coffee ID is required")
    private Long coffeeId;

    @NotNull(message = "Rating is required")
    @Min(value = 0, message = "Rating cannot be negative")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Double rating;

    @NotBlank(message = "Review content cannot be empty")
    private String content;

    @NotBlank(message = "Brewing method is required")
    @Size(max = 50, message = "Brewing method cannot exceed 50 characters")
    private String brewingMethod;

    @NotNull(message = "Description is required")
    @Size(max = 200, message = "Brewing Description cannot exceed 200 characters")
    private String brewingDescription;

    public ReviewRequestDTO() {
    }

    public Long getCoffeeId() {
        return coffeeId;
    }

    public Double getRating() {
        return rating;
    }

    public String getContent() {
        return content;
    }

    public String getBrewingMethod() {
        return brewingMethod;
    }

    public String getBrewingDescription() {
        return brewingDescription;
    }

    public void setCoffeeId(Long coffeeId) {
        this.coffeeId = coffeeId;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setBrewingMethod(String brewingMethod) {
        this.brewingMethod = brewingMethod;
    }

    public void setBrewingDescription(String brewingDescription) {
        this.brewingDescription = brewingDescription;
    }
}