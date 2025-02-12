package com.example.coffeetica.user.security;

import com.example.coffeetica.user.models.UserEntity;
import com.example.coffeetica.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final UserRepository userRepository;

    @Autowired
    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User)) {
            throw new RuntimeException("No authenticated user found");
        }

        // Rzutowanie principal na standardowego Usera z Spring Security
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        // Odczytujemy username
        String username = principal.getUsername();

        // Wyszukujemy w bazie usera, by poznać jego ID
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getId();
    }
}