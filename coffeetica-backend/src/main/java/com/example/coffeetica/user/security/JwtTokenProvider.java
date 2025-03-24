package com.example.coffeetica.user.security;


import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * A provider class for generating and validating JWT tokens,
 * as well as extracting the identifier (username/email).
 */
@Component
public class JwtTokenProvider {

    private final String jwtSecret;
    private final long jwtExpirationInMs;

    /**
     * Constructs a new {@link JwtTokenProvider} with the secret key and expiration
     * time read from application properties.
     *
     * @param jwtSecret a secret key for signing the JWT
     * @param jwtExpirationInMs expiration time in milliseconds
     */
    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String jwtSecret,
            @Value("${app.jwt.expiration}") long jwtExpirationInMs
    ) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    /**
     * Generates a JWT token for the authenticated user.
     *
     * @param authentication the authentication object containing user principal
     * @return a signed JWT token
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName(); // could be username/email
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Extracts the identifier (username or email) from a JWT token.
     *
     * @param token the JWT token
     * @return the subject (identifier) stored in the token
     */
    public String getIdentifierFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Validates the token for correctness and expiration.
     *
     * @param token the JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException |
                 UnsupportedJwtException | IllegalArgumentException ex) {
            // Could log the exception here
            return false;
        }
    }
}