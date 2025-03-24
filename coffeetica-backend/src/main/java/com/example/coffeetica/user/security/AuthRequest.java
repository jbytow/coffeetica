package com.example.coffeetica.user.security;

import jakarta.validation.constraints.NotBlank;

/**
 * A request DTO for user authentication,
 * supporting username or email as the identifier.
 */
public class AuthRequest {

    @NotBlank(message = "Identifier (username or email) cannot be blank.")
    private String identifier;

    @NotBlank(message = "Password cannot be blank.")
    private String password;

    public AuthRequest() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}