package com.example.coffeetica.user.controllers;


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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @GetMapping("/api/users/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromJWT(token.replace("Bearer ", ""));
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "roles", user.getRoles().stream().map(RoleEntity::getName).toList()
        ));
    }

    @PutMapping("/api/users/{id}")
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

    @GetMapping("/api/users/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        logger.debug("Attempting to load user by username {}", username);
        try {
            UserDetails user = userService.loadUserByUsername(username);
            logger.info("Loaded user successfully by username {}", username);
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException e) {
            logger.error("User not found with username {}: {}", username, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
