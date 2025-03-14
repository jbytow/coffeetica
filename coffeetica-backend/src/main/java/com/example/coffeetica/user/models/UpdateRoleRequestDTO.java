package com.example.coffeetica.user.models;


import javax.validation.constraints.NotEmpty;
import java.util.Set;

public class UpdateRoleRequestDTO {
    @NotEmpty
    private Set<String> roles;

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}