package com.example.coffeetica.user.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;


public class UserDTO {
    private Long id;
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
    private Set<String> roles;
    private Set<Long> reviewIds;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<Long> getReviewIds() {
        return reviewIds;
    }

    public void setReviewIds(Set<Long> reviewIds) {
        this.reviewIds = reviewIds;
    }
}
