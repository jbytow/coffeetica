package com.example.coffeetica.user.models;

import com.example.coffeetica.coffee.models.ReviewEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user entity storing credentials and relationships.
 */
@Entity
@Table(name = "users") // you can add unique constraints here if needed
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username for the user, typically unique.
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Bcrypt-hashed password.
     */
    @Column(nullable = false, length = 100)
    private String password;

    /**
     * Email for the user, typically unique.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Roles assigned to the user.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roleEntities = new HashSet<>();

    /**
     * Reviews created by this user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewEntity> reviews = new HashSet<>();

    public UserEntity() {
    }


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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<RoleEntity> getRoles() {
        return roleEntities;
    }

    public void setRoles(Set<RoleEntity> roleEntities) {
        this.roleEntities = roleEntities;
    }

    public Set<ReviewEntity> getReviews() {
        return reviews;
    }

    public void setReviews(Set<ReviewEntity> reviews) {
        this.reviews = reviews;
    }
}