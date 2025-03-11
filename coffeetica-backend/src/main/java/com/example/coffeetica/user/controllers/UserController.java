package com.example.coffeetica.user.controllers;


import com.example.coffeetica.coffee.models.ChangePasswordRequestDTO;
import com.example.coffeetica.coffee.models.CoffeeDetailsDTO;
import com.example.coffeetica.user.models.UserDTO;
import com.example.coffeetica.user.models.UserEntity;
import com.example.coffeetica.user.models.RoleEntity;
import com.example.coffeetica.user.repositories.UserRepository;
import com.example.coffeetica.user.security.JwtTokenProvider;
import com.example.coffeetica.user.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/api/users/register")
    @PreAuthorize("permitAll()") // Public endpoint
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        logger.debug("Attempting to register user {}", userDTO.getUsername());
        try {
            UserDTO savedUser = userService.registerNewUserAccount(userDTO);
            logger.info("Registered user successfully {}", userDTO.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            logger.error("Registration failed for user {}: {}", userDTO.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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

    @PutMapping("/api/users/{id}/change-password")
    @PreAuthorize("hasRole('Admin') or @securityService.getCurrentUserId() == #id")
    public ResponseEntity<?> changeUserPassword(@PathVariable Long id,
                                                @RequestBody ChangePasswordRequestDTO request) {
        logger.debug("Attempting to change password for user {}", id);
        try {
            userService.changeUserPassword(id, request.getCurrentPassword(), request.getNewPassword());
            logger.info("Password changed successfully for user {}", id);
            return ResponseEntity.ok("Password changed successfully.");
        } catch (Exception e) {
            logger.error("Change password failed for user {}: {}", id, e.getMessage());
            // Można też zwrócić status 400 lub 401, zależnie od przyczyny
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/api/users/{id}")
    @PreAuthorize("hasRole('Admin') or @securityService.getCurrentUserId() == #id")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        logger.debug("Attempting to update user {}", id);
        try {
            userDTO.setId(id);
            UserDTO updatedUser = userService.updateUser(userDTO);
            logger.info("Updated user successfully {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Update failed for user {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/api/users/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.debug("Attempting to delete user {}", id);
        try {
            userService.deleteUser(id);
            logger.info("Deleted user successfully {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Delete failed for user {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @GetMapping("/api/users/{username}")
//    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
//        logger.debug("Attempting to load user by username {}", username);
//        try {
//            UserDetails user = userService.loadUserByUsername(username);
//            logger.info("Loaded user successfully by username {}", username);
//            return ResponseEntity.ok(user);
//        } catch (UsernameNotFoundException e) {
//            logger.error("User not found with username {}: {}", username, e.getMessage());
//            return ResponseEntity.notFound().build();
//        }
//    }

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
