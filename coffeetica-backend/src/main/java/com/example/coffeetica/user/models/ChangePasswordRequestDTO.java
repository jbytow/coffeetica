package com.example.coffeetica.user.models;


import jakarta.validation.constraints.NotBlank;

/**
 * A DTO for changing a user's password.
 */
public class ChangePasswordRequestDTO {

    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword;

    public ChangePasswordRequestDTO() {
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}