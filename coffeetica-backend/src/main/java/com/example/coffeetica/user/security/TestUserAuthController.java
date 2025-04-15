package com.example.coffeetica.user.security;

import com.example.coffeetica.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * REST controller that provides a simplified authentication endpoint for a predefined
 * test admin user, intended strictly for demo or portfolio purposes.
 *
 * This endpoint bypasses password validation and does not assign any roles or authorities.
 * It simply generates a JWT token for a fixed username configured via application properties.
 *
 * Configuration example (in application.properties):
 * testadmin.username=testuser
 *
 * Example request:
 * POST /api/auth/auto-login
 * Example response:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
 * }
 */
@RestController
@RequestMapping("/api/auth")
public class TestUserAuthController {

    @Value("${testadmin.username}")
    private String testAdminUsername;

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    /**
     * Constructs the {@link TestUserAuthController} with required dependencies.
     *
     * @param jwtTokenProvider the JWT token provider used to generate authentication tokens
     * @param userRepository   the repository for accessing user data (not used in this implementation but injected for consistency/future use)
     */

    public TestUserAuthController(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    /**
     * Authenticates as the predefined test admin user and generates a JWT token.
     * No credentials or roles are validated. The username is injected via configuration.
     * @return HTTP 200 with a JSON object containing the JWT token, or HTTP 500 on error
     */
    @PostMapping("/auto-login")
    public ResponseEntity<?> autoLogin() {

        try {
            // Create authentication with just the username (no password, no authorities)
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    testAdminUsername,
                    null
            );

            // Generate the JWT token
            String token = jwtTokenProvider.generateToken(authentication);

            return ResponseEntity.ok(Map.of("token", token));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Test user authentication failed"));
        }
    }
}