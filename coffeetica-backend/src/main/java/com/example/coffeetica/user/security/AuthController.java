package com.example.coffeetica.user.security;

import com.example.coffeetica.user.exceptions.UsernameOrEmailNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            // Authenticate the user with username or email and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getIdentifier(), authRequest.getPassword())
            );

            // Generate a JWT token after successful authentication
            String token = jwtTokenProvider.generateToken(authentication);

            // Return the token in the response
            return ResponseEntity.ok(Map.of("token", token));

        } catch (UsernameOrEmailNotFoundException ex) {
            // Handle case where the username or email does not exist
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "Invalid username/email or password"
            ));
        } catch (AuthenticationException ex) {
            // Handle generic authentication errors (e.g., incorrect password)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "Invalid credentials"
            ));
        }
    }
}