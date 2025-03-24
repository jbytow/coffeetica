package com.example.coffeetica.coffee.models;

import jakarta.persistence.*;
import java.util.Set;

/**
 * Represents a roastery entity in the system, storing essential information about the roastery
 * (e.g., name, country, founding year) along with associated coffees.
 */
@Entity
@Table(name = "roasteries")
public class RoasteryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private Integer foundingYear;

    @Column(nullable = false)
    private String websiteUrl;

    @Column(nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "roastery", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CoffeeEntity> coffees;

    // Constructors, getters, and setters

    public RoasteryEntity() {
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

    public Set<CoffeeEntity> getCoffees() {
        return coffees;
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

    public void setCoffees(Set<CoffeeEntity> coffees) {
        this.coffees = coffees;
    }
}