package com.example.coffeetica.coffee.models;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "roasteries")
public class RoasteryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    private Integer foundingYear;
    private String websiteUrl;

    @OneToMany(mappedBy = "roastery", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CoffeeEntity> coffees;

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

    public Set<CoffeeEntity> getCoffees() {
        return coffees;
    }

    public void setCoffees(Set<CoffeeEntity> coffees) {
        this.coffees = coffees;
    }
}
