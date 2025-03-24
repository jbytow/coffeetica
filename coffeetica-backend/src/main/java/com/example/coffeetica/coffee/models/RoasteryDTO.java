package com.example.coffeetica.coffee.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * A Data Transfer Object (DTO) for transferring roastery information across layers.
 * Includes basic validation annotations to enforce constraints.
 */
public class RoasteryDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name can have at most 100 characters")
    private String name;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country can have at most 100 characters")
    private String country;

    @NotNull(message = "Founding year is required")
    @Min(value = 1500, message = "Founding year must be greater than or equal to 1500")
    private Integer foundingYear;

    @NotNull(message = "Website URL is required")
    @Size(max = 200, message = "Website URL can have at most 200 characters")
    private String websiteUrl;

    private String imageUrl; // URL of the uploaded image

    public RoasteryDTO() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public Integer getFoundingYear() {
        return foundingYear;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setFoundingYear(Integer foundingYear) {
        this.foundingYear = foundingYear;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}