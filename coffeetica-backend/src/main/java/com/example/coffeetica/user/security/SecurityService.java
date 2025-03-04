package com.example.coffeetica.user.security;

import com.example.coffeetica.coffee.models.ReviewEntity;
import com.example.coffeetica.coffee.repositories.ReviewRepository;
import com.example.coffeetica.user.models.UserEntity;
import com.example.coffeetica.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SecurityService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public SecurityService(UserRepository userRepository, ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * Retrieves the ID of the currently authenticated user.
     *
     * @return the ID of the authenticated user
     * @throws RuntimeException if no user is authenticated
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User)) {
            throw new RuntimeException("No authenticated user found");
        }

        // Retrieve the authenticated user from Spring Security context
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        // Extract the username from the principal
        String username = principal.getUsername();

        // Fetch the user entity from the database to retrieve the user ID
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getId();
    }

    /**
     * Checks whether the currently authenticated user is the owner of a given review.
     *
     * @param reviewId the ID of the review
     * @return true if the authenticated user is the owner of the review, false otherwise
     */
    public boolean isReviewOwner(Long reviewId) {
        Optional<ReviewEntity> reviewOpt = reviewRepository.findById(reviewId);

        if (reviewOpt.isEmpty()) {
            return false; // The review does not exist
        }

        // Retrieve the ID of the currently authenticated user
        Long currentUserId = getCurrentUserId();

        // Compare the review owner's ID with the authenticated user's ID
        return reviewOpt.get().getUser().getId().equals(currentUserId);
    }
}