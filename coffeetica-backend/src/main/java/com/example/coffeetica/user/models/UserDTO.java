package com.example.coffeetica.user.models;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * A Data Transfer Object (DTO) for user data in read operations.
 */
public class UserDTO {

    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Email
    private String email;

    /**
     * A set of string-based roles (e.g., "User", "Admin").
     */
    private Set<String> roles;

    /**
     * A set of IDs referencing the user's reviews.
     */
    private Set<Long> reviewIds;

    public UserDTO() {
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Set<Long> getReviewIds() {
        return reviewIds;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void setReviewIds(Set<Long> reviewIds) {
        this.reviewIds = reviewIds;
    }
}