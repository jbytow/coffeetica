package com.example.coffeetica.coffee.models;

import com.example.coffeetica.user.models.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String content;

    @Column(nullable = false)
    private String brewingMethod;

    @Column(nullable = true)
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

    public CoffeeEntity getCoffee() {
        return coffee;
    }

    public void setCoffee(CoffeeEntity coffee) {
        this.coffee = coffee;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}