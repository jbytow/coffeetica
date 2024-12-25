package com.example.coffeetica.coffee.models;

public class RoasteryDTO {

    private Long id;
    private String name;
    private String location;
    private Integer foundingYear;
    private String websiteUrl;

    private String imageUrl; // URL of the uploaded image

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getFoundingYear() {
        return foundingYear;
    }

    public void setFoundingYear(Integer foundingYear) {
        this.foundingYear = foundingYear;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
