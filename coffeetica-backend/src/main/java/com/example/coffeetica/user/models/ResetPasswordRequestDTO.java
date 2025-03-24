package com.example.coffeetica.user.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * A request DTO for resetting a user's password (admin-initiated).
 */
public class ResetPasswordRequestDTO {

    @NotBlank(message = "New password cannot be blank.")
    @Size(min = 6, max = 100, message = "New password must be between 6 and 100 characters.")
    private String newPassword;

    public ResetPasswordRequestDTO() {
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}