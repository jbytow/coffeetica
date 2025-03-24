package com.example.coffeetica.coffee.models;

import com.example.coffeetica.user.models.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;


import java.time.LocalDateTime;


/**
 * Represents a review entity containing user feedback (content, rating, etc.)
 * for a specific coffee, along with the user who created the review.
 */
@Entity
@Table(name = "reviews")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 10000)
    @Column(nullable = false)
    private String content;

    @Size(max = 50)
    @Column(nullable = false)
    private String brewingMethod;

    @Size(max = 200)
    private String brewingDescription;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coffee_id", nullable = false)
    private CoffeeEntity coffee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public ReviewEntity() {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public CoffeeEntity getCoffee() {
        return coffee;
    }

    public UserEntity getUser() {
        return user;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCoffee(CoffeeEntity coffee) {
        this.coffee = coffee;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}