package com.example.coffeetica.user.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * A DTO for a regular user to update their own email address.
 */
public class UpdateUserRequestDTO {

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Please provide a valid email address.")
    private String email;

    public UpdateUserRequestDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}