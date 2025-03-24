package com.example.coffeetica.coffee.models;

/**
 * A Data Transfer Object (DTO) representing a review,
 * typically used in GET responses.
 * It includes user/coffee relationship fields like userId, coffeeId,
 * userName, and coffeeName, which are often set manually after entity mapping.
 */
public class ReviewDTO {

    private Long id;
    private String content;
    private String brewingMethod;
    private String brewingDescription;
    private Double rating;
    private String createdAt;     // e.g., mapped from LocalDateTime if needed
    private Long coffeeId;
    private String coffeeName;
    private Long userId;
    private String userName;

    // Constructors, Getters, and Setters

    public ReviewDTO() {
    }

    public Long getId() {
        return id;
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

    public Double getRating() {
        return rating;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Long getCoffeeId() {
        return coffeeId;
    }

    public String getCoffeeName() {
        return coffeeName;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setCoffeeId(Long coffeeId) {
        this.coffeeId = coffeeId;
    }

    public void setCoffeeName(String coffeeName) {
        this.coffeeName = coffeeName;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}