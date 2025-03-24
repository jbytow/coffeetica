package com.example.coffeetica.coffee.models;

import com.example.coffeetica.coffee.models.enums.FlavorProfile;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.models.enums.RoastLevel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a coffee entity in the system, storing all relevant attributes
 * such as origin, roast level, flavor notes, and an associated roastery.
 */
@Entity
@Table(name = "coffees")
public class CoffeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String countryOfOrigin;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    private RoastLevel roastLevel;

    @Enumerated(EnumType.STRING)
    private FlavorProfile flavorProfile;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "coffee_flavor_notes", joinColumns = @JoinColumn(name = "coffee_id"))
    @Column(name = "flavor_note", nullable = false)
    private Set<String> flavorNotes = new HashSet<>();

    @Column(nullable = false)
    private String processingMethod;

    @Column(nullable = false)
    private Integer productionYear;

    private String imageUrl; // image path (nullable by default)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roastery_id", nullable = false)
    private RoasteryEntity roastery;

    @OneToMany(mappedBy = "coffee", cascade = CascadeType.REMOVE)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<ReviewEntity> reviews = new ArrayList<>();

    // Constructors, getters, and setters

    public CoffeeEntity() {
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

    public RoasteryEntity getRoastery() {
        return roastery;
    }

    public List<ReviewEntity> getReviews() {
        return reviews;
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

    public void setRoastery(RoasteryEntity roastery) {
        this.roastery = roastery;
    }

    public void setReviews(List<ReviewEntity> reviews) {
        this.reviews = reviews;
    }
}
