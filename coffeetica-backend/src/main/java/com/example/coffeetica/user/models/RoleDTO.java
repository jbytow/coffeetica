package com.example.coffeetica.user.models;

/**
 * A Data Transfer Object for role data
 */
public class RoleDTO {

    private Long id;
    private String name;

    public RoleDTO() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}