package com.example.coffeetica.coffee.models;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "coffees")
public class CoffeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Name of the coffee
    @Column(nullable = false)
    private String countryOfOrigin; // Country of origin
    @Column(nullable = false)
    private String region; // Region
    @Column(nullable = false)
    private String roastLevel; // Roast level
    @Column(nullable = false)
    private String flavorProfile; // Flavor profile
    @Column(nullable = false)
    private String notes; // Notes
    @Column(nullable = false)
    private String processingMethod; // Processing method
    @Column(nullable = false)
    private Integer productionYear; // Year

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roastery_id", nullable = false)
    private RoasteryEntity roastery;

    @OneToMany(mappedBy = "coffee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewEntity> reviews;

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

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRoastLevel() {
        return roastLevel;
    }

    public void setRoastLevel(String roastLevel) {
        this.roastLevel = roastLevel;
    }

    public String getFlavorProfile() {
        return flavorProfile;
    }

    public void setFlavorProfile(String flavorProfile) {
        this.flavorProfile = flavorProfile;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getProcessingMethod() {
        return processingMethod;
    }

    public void setProcessingMethod(String processingMethod) {
        this.processingMethod = processingMethod;
    }

    public Integer getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(Integer productionYear) {
        this.productionYear = productionYear;
    }

    public RoasteryEntity getRoastery() {
        return roastery;
    }

    public void setRoastery(RoasteryEntity roastery) {
        this.roastery = roastery;
    }

    public Set<ReviewEntity> getReviews() {
        return reviews;
    }

    public void setReviews(Set<ReviewEntity> reviews) {
        this.reviews = reviews;
    }
}
