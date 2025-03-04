package com.example.coffeetica.config;

import com.example.coffeetica.user.security.JwtAuthenticationFilter;
import com.example.coffeetica.user.security.JwtTokenProvider;
import com.example.coffeetica.user.services.UserService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Creates and returns the JWT authentication filter.
     *
     * @param tokenProvider the JWT token provider
     * @param userService the user service to retrieve user details
     * @return an instance of {@link JwtAuthenticationFilter}
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserService userService) {
        return new JwtAuthenticationFilter(tokenProvider, userService);
    }

    /**
     * Configures the security filter chain, specifying access control rules for different endpoints.
     *
     * @param http the {@link HttpSecurity} instance
     * @param jwtAuthenticationFilter the JWT authentication filter
     * @return a configured {@link SecurityFilterChain} bean
     * @throws Exception if any security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http.csrf(csrf -> csrf.disable())
                // CSRF protection is disabled as we use stateless JWT authentication

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // The application does not use session-based authentication (stateless API)

                .authorizeHttpRequests(auth -> auth

                        // Publicly accessible endpoints (no authentication required)
                        .requestMatchers(HttpMethod.GET, "/api/coffees/**").permitAll() // Coffee catalog
                        .requestMatchers(HttpMethod.GET, "/api/roasteries/**").permitAll() // Roasteries catalog
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll() // Static image uploads

                        // Authentication and registration endpoints (public)
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()

                        // User-specific endpoints (authentication required)
                        .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()

                        // User management
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}").authenticated() // Users can update their own profile
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("Admin") // Admins can update any user
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("Admin") // Only Admins can delete users

                        // Review management (authenticated users with roles)
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**").hasAnyRole("User", "Admin") // Only authenticated users can create reviews

                        // Coffee entity management (restricted to Admins)
                        .requestMatchers(HttpMethod.POST, "/api/coffees/**").hasRole("Admin") // Admins can create coffees
                        .requestMatchers(HttpMethod.PUT, "/api/coffees/**").hasRole("Admin") // Admins can update coffees
                        .requestMatchers(HttpMethod.DELETE, "/api/coffees/**").hasRole("Admin") // Admins can delete coffees

                        // Roastery entity management (restricted to Admins)
                        .requestMatchers(HttpMethod.POST, "/api/roasteries/**").hasRole("Admin") // Admins can create roasteries
                        .requestMatchers(HttpMethod.PUT, "/api/roasteries/**").hasRole("Admin") // Admins can update roasteries
                        .requestMatchers(HttpMethod.DELETE, "/api/roasteries/**").hasRole("Admin") // Admins can delete roasteries

                        // Any other request requires authentication
                        .anyRequest().authenticated()
                )
                // Adds the JWT authentication filter before the username/password authentication filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Defines a password encoder bean using BCrypt hashing.
     *
     * @return an instance of {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides an authentication manager for handling authentication requests.
     *
     * @param authConfig the authentication configuration
     * @return the authentication manager
     * @throws Exception if an error occurs while retrieving the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}