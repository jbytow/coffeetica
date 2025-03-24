package com.example.coffeetica.coffee.models;

import com.example.coffeetica.coffee.models.enums.FlavorProfile;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.models.enums.RoastLevel;
import jakarta.validation.constraints.*;


import java.util.Set;

/**
 * A Data Transfer Object (DTO) for transferring coffee data across layers.
 * Includes basic validation annotations.
 */
public class CoffeeDTO {

    private Long id;

    @NotBlank(message = "Coffee name is required")
    @Size(max = 100, message = "Coffee name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Country of origin is required")
    @Size(max = 100, message = "Country of origin must not exceed 100 characters")
    private String countryOfOrigin;

    @NotNull(message = "Region is required")
    private Region region;

    @NotNull(message = "Roast level is required")
    private RoastLevel roastLevel;

    @NotNull(message = "Flavor profile is required")
    private FlavorProfile flavorProfile;

    @NotEmpty(message = "At least one flavor note is required")
    private Set<String> flavorNotes;

    @NotBlank(message = "Processing method is required")
    @Size(max = 100, message = "Processing method must not exceed 100 characters")
    private String processingMethod;

    @NotNull(message = "Production year is required")
    @Min(value = 1500, message = "Production year must be greater or equal to 1500")
    private Integer productionYear;

    private String imageUrl; // URL of the uploaded image

    @NotNull(message = "Roastery information is required")
    private RoasteryDTO roastery;

    public CoffeeDTO() {
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
}