package com.example.coffeetica.user.security;

import com.example.coffeetica.user.models.UserEntity;
import com.example.coffeetica.user.models.RoleEntity;
import com.example.coffeetica.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            // Authenticate the user with the provided username and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            // Generate a JWT token
            String token = jwtTokenProvider.generateToken(authentication);

            // Retrieve user details from the database
            UserEntity user = userRepository.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Return the token and user information
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", Map.of(
                            "id", user.getId(),
                            "roles", user.getRoles().stream().map(RoleEntity::getName).toList()
                    )
            ));
        } catch (AuthenticationException ex) {
            // Log the authentication failure for debugging purposes
            System.err.println("Authentication failed for user: " + authRequest.getUsername());
            ex.printStackTrace();

            // Return an error response with relevant details
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "timestamp", new Date(),
                    "status", HttpStatus.UNAUTHORIZED.value(),
                    "error", "Invalid username or password",
                    "path", "/api/auth/login"
            ));
        }
    }
}