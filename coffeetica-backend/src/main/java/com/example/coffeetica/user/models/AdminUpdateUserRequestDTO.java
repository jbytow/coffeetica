package com.example.coffeetica.user.models;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * A DTO for admin operations to update a user's username or email.
 */
public class AdminUpdateUserRequestDTO {


    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
    private String username;

    @Email(message = "Please provide a valid email address.")
    private String email;

    public AdminUpdateUserRequestDTO() {
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
