package com.example.coffeetica.user.security;


import com.example.coffeetica.user.services.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;

/**
 * A filter that reads the JWT token from the Authorization header,
 * validates it, and sets the authentication in the SecurityContext
 * if valid.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    /**
     * Constructs a {@link JwtAuthenticationFilter} with required dependencies.
     *
     * @param tokenProvider the JWT token provider
     * @param userService   the user service for loading user details
     */
    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserService userService) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    /**
     * Filters each request, extracting a JWT token from the Authorization header,
     * validating it, and setting the security context if it is valid.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if filtering fails
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if (token != null && tokenProvider.validateToken(token)) {
            // Extract the username/email from the token
            String identifier = tokenProvider.getIdentifierFromJWT(token);

            // Load user details via username/email
            UserDetails userDetails = userService.loadUserByUsernameOrEmail(identifier);

            // Set up authentication
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Store in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Attempts to retrieve the JWT token from the Authorization header,
     * returning null if not present or improperly formed.
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}