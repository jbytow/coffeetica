package com.example.coffeetica.coffee.models;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ReviewRequestDTO {
    @NotNull(message = "Coffee ID is required")
    private Long coffeeId;

    @Min(0) @Max(5)
    @NotNull(message = "Rating is required")
    private Double rating;

    @NotBlank(message = "Review content cannot be empty")
    private String content;

    @NotBlank(message = "Brewing method is required")
    private String brewingMethod;

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