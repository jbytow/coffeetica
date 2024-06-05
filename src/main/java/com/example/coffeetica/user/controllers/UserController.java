package com.example.coffeetica.user.controllers;


import com.example.coffeetica.user.models.UserDTO;
import com.example.coffeetica.user.models.UserEntity;
import com.example.coffeetica.user.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/api/users/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        logger.debug("Attempting to register user {}", userDTO.getUsername());
        try {
            UserDTO savedUser = userService.registerNewUserAccount(userDTO);
            logger.info("Registered user successfully {}", userDTO.getUsername());
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            logger.error("Registration failed for user {}: {}", userDTO.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
            return ResponseEntity.ok().build();
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
