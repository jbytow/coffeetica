package com.example.coffeetica.user.security;

import com.example.coffeetica.user.exceptions.UsernameOrEmailNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * A REST controller handling authentication requests, such as user login,
 * returning a JWT token upon successful authentication.
 */
@RestController
@RequestMapping
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs a new {@link AuthController} with the required dependencies.
     *
     * @param authenticationManager the Spring Security authentication manager
     * @param jwtTokenProvider      the JWT token provider for generating tokens
     */
    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Authenticates the user using either username or email as an identifier.
     * On success, returns a JWT token in JSON format.
     *
     * @param authRequest the authentication request containing identifier/password
     * @return 200 OK with token on success, or 401 Unauthorized on failure
     */
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        logger.info("Attempting login for identifier: {}", authRequest.getIdentifier());
        try {
            // Attempt authentication with username/email (identifier) and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getIdentifier(),
                            authRequest.getPassword()
                    )
            );

            // Generate the JWT token upon successful authentication
            String token = jwtTokenProvider.generateToken(authentication);

            // Return the token in the response
            return ResponseEntity.ok(Map.of("token", token));

        } catch (UsernameOrEmailNotFoundException ex) {
            // Specific case: user not found in database
            logger.warn("User not found: {}", authRequest.getIdentifier());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username/email or password"));

        } catch (AuthenticationException ex) {
            // Generic authentication failure (e.g., bad credentials)
            logger.error("Authentication failed for identifier: {}", authRequest.getIdentifier());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }
}