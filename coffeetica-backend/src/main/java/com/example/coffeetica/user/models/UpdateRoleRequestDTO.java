package com.example.coffeetica.user.models;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

/**
 * A DTO used by a SuperAdmin to update a user's roles.
 */
public class UpdateRoleRequestDTO {

    @NotEmpty(message = "Roles set cannot be empty.")
    private Set<String> roles;

    public UpdateRoleRequestDTO() {
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}