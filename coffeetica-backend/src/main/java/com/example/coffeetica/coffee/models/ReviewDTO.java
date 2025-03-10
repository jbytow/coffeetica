package com.example.coffeetica.coffee.models;

public class ReviewDTO {

    private Long id;
    private String content;
    private String brewingMethod;
    private String brewingDescription;
    private Double rating;
    private String createdAt;
    private Long coffeeId;
    private String coffeeName;
    private Long userId;
    private String userName;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getCreatedAt() { return createdAt; }

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Long getCoffeeId() {
        return coffeeId;
    }

    public void setCoffeeId(Long coffeeId) {
        this.coffeeId = coffeeId;
    }

    public String getCoffeeName() { return coffeeName; }

    public void setCoffeeName(String coffeeName) { this.coffeeName = coffeeName; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }
}
