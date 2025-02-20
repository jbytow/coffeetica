package com.example.coffeetica.coffee.models;

import javax.validation.constraints.*;

public class ReviewRequestDTO {
    @NotNull(message = "Coffee ID is required")
    private Long coffeeId;

    @Min(0) @Max(5)
    @NotNull(message = "Rating is required")
    private Double rating;

    @NotBlank(message = "Review content cannot be empty")
    private String content;

    @Size(max = 50, message = "Brewing Method cannot exceed 50 characters")
    @NotBlank(message = "Brewing method is required")
    private String brewingMethod;

    @Size(max = 200, message = "Brewing Description cannot exceed 200 characters")
    @NotNull(message = "Description is required")
    private String brewingDescription;

    public Long getCoffeeId() {
        return coffeeId;
    }

    public void setCoffeeId(Long coffeeId) {
        this.coffeeId = coffeeId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBrewingMethod() {
        return brewingMethod;
    }

    public void setBrewingMethod(String brewingMethod) {
        this.brewingMethod = brewingMethod;
    }

    public String getBrewingDescription() {
        return brewingDescription;
    }

    public void setBrewingDescription(String brewingDescription) {
        this.brewingDescription = brewingDescription;
    }

}