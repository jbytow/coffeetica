package com.example.coffeetica.user.security;


import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationInMs;

    // Generate a JWT token using the user's authentication details
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username) // Set the username as the subject of the token
                .setIssuedAt(now) // Set the token's issued time
                .setExpiration(expiryDate) // Set the token's expiration time
                .signWith(SignatureAlgorithm.HS512, jwtSecret) // Sign the token with the secret key
                .compact(); // Build the token
    }

    // Extract the username (subject) from the token
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret) // Use the secret key to parse the token
                .parseClaimsJws(token) // Parse the token
                .getBody();
        return claims.getSubject(); // Return the username
    }

    // Validate the token's integrity and expiration
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token); // Parse and validate the token
            return true;
        } catch (MalformedJwtException | ExpiredJwtException |
                 UnsupportedJwtException | IllegalArgumentException ex) {
            return false; // Return false if the token is invalid
        }
    }
}