package com.example.coffeetica.user.controllers;


import com.example.coffeetica.user.models.*;
import com.example.coffeetica.coffee.models.CoffeeDetailsDTO;
import com.example.coffeetica.user.repositories.UserRepository;
import com.example.coffeetica.user.security.JwtTokenProvider;
import com.example.coffeetica.user.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing users. Supports registration,
 * updates, role changes, password resets, and retrieval operations.
 */
@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs a new {@link UserController}.
     *
     * @param userService the user service layer
     * @param userRepository the user repository
     * @param jwtTokenProvider the JWT token provider for auth tokens
     */
    @Autowired
    public UserController(UserService userService,
                          UserRepository userRepository,
                          JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Retrieves a paginated list of users, optionally filtered by a search term.
     *
     * @param search optional search string (username/email)
     * @param page page index
     * @param size page size
     * @param sortBy field to sort by
     * @param direction sort direction (asc/desc)
     * @return a page of user DTOs
     */
    @GetMapping("/api/users")
    @PreAuthorize("permitAll()")
    public Page<UserDTO> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return userService.findAllUsers(search, page, size, sortBy, direction);
    }

    /**
     * Registers a new user account (public endpoint).
     *
     * @param request the registration request
     * @return the created user DTO or an error message
     */
    @PostMapping("/api/users/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO request) {
        logger.debug("Attempting to register user {}", request.getUsername());
        try {
            UserDTO savedUser = userService.registerNewUserAccount(request);
            logger.info("Registered user successfully {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            logger.error("Registration failed for user {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the user ID
     * @return the user DTO if found, otherwise 404
     */
    @GetMapping("/api/users/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        logger.debug("Attempting to load user by ID {}", id);
        try {
            Optional<UserDTO> userDTO = userService.findUserById(id);
            if (userDTO.isPresent()) {
                return ResponseEntity.ok(userDTO.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("User not found with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Retrieves info about the currently authenticated user (username, email, roles).
     *
     * @param token the bearer token
     * @return user info if found, otherwise 404
     */
    @GetMapping("/api/users/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getIdentifierFromJWT(token.replace("Bearer ", ""));
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "roles", user.getRoles().stream().map(RoleEntity::getName).toList()
        ));
    }

    /**
     * Changes the password for the user with the given ID,
     * only if it's the same user making the request.
     *
     * @param id the user ID
     * @param request the old/new password data
     * @return 200 OK if successful, otherwise an error
     */
    @PutMapping("/api/users/{id}/change-password")
    @PreAuthorize("@securityService.getCurrentUserId() == #id")
    public ResponseEntity<?> changeUserPassword(@PathVariable Long id,
                                                @RequestBody ChangePasswordRequestDTO request) {
        logger.debug("User {} is attempting to change their password", id);
        try {
            userService.changeUserPassword(id, request.getCurrentPassword(), request.getNewPassword());
            logger.info("Password changed successfully for user {}", id);
            return ResponseEntity.ok("Password changed successfully.");
        } catch (Exception e) {
            logger.error("Password change failed for user {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Resets another user's password (Admin only).
     *
     * @param id the user ID
     * @param request containing the new password
     * @return 200 OK if successful
     */
    @PutMapping("/api/users/{id}/reset-password")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> resetUserPassword(@PathVariable Long id,
                                               @RequestBody ResetPasswordRequestDTO request) {
        logger.debug("Admin is attempting to reset password for user {}", id);
        try {
            userService.resetUserPassword(id, request.getNewPassword());
            logger.info("Password reset successfully for user {}", id);
            return ResponseEntity.ok("Password reset successfully.");
        } catch (Exception e) {
            if (e instanceof IllegalAccessException) {
                logger.warn("Forbidden password reset for user {}: {}", id, e.getMessage());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            }
            logger.error("Password reset failed for user {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Password reset failed: " + e.getMessage());
        }
    }

    /**
     * Updates the email of the user with the given ID, only if it's the same user making the request.
     *
     * @param id the user ID
     * @param request the new email address
     * @return 200 OK with updated user data, or error
     */
    @PutMapping("/api/users/{id}/update-email")
    @PreAuthorize("@securityService.getCurrentUserId() == #id")
    public ResponseEntity<?> updateUserEmail(@PathVariable Long id, @RequestBody UpdateUserRequestDTO request) {
        logger.debug("User {} is updating their email", id);
        try {
            UserDTO updatedUser = userService.updateUserEmail(id, request);
            logger.info("Updated email successfully for user {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Update email failed for user {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Allows an Admin to update another user's username/email.
     *
     * @param id the user ID
     * @param request data containing username/email to update
     * @return 200 OK if successful
     */
    @PutMapping("/api/users/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> adminUpdateUser(@PathVariable Long id, @RequestBody AdminUpdateUserRequestDTO request) {
        logger.debug("Admin is updating user {}", id);
        try {
            UserDTO updatedUser = userService.adminUpdateUser(id, request);
            logger.info("Admin updated user {} successfully", id);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            if (e instanceof IllegalAccessException) {
                logger.warn("Forbidden update attempt for user {}: {}", id, e.getMessage());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            }
            logger.error("Admin update failed for user {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Deletes a user (Admin only). Blocks deleting Admin/SuperAdmin if you are only Admin.
     *
     * @param id the user ID
     * @return 204 if successful, error otherwise
     */
    @DeleteMapping("/api/users/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.debug("Attempting to delete user {}", id);
        try {
            userService.deleteUser(id);
            logger.info("Deleted user successfully {}", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalAccessException e) {
            logger.warn("Forbidden delete attempt for user {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Delete failed for user {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Delete failed: " + e.getMessage());
        }
    }

    /**
     * Updates a user's roles (SuperAdmin only).
     *
     * @param id the user ID
     * @param request containing the new roles
     * @return 200 OK with updated user info
     */
    @PutMapping("/api/users/{id}/update-roles")
    @PreAuthorize("hasRole('SuperAdmin')")
    public ResponseEntity<?> updateUserRoles(@PathVariable Long id,
                                             @RequestBody UpdateRoleRequestDTO request) {
        logger.debug("SuperAdmin is updating roles for user {}", id);
        try {
            UserDTO updatedUser = userService.updateUserRoles(id, request.getRoles());
            logger.info("SuperAdmin updated roles successfully for user {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Role update failed for user {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Returns the user's "favorite coffee" if they have a review with rating=5.
     *
     * @param id the user ID
     * @return the coffee details or 204 if no such coffee found
     */
    @GetMapping("/api/users/{id}/favorite-coffee")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getFavoriteCoffee(@PathVariable Long id) {
        try {
            Optional<CoffeeDetailsDTO> coffeeOpt = userService.findFavoriteCoffeeOfUser(id);
            if (coffeeOpt.isPresent()) {
                return ResponseEntity.ok(coffeeOpt.get());
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}