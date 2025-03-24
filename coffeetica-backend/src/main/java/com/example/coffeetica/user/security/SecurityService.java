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

/**
 * A service providing security-related checks,
 * such as retrieving the current user ID or verifying ownership of a review.
 */
@Service
public class SecurityService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    /**
     * Constructs a new {@link SecurityService}.
     *
     * @param userRepository the user repository
     * @param reviewRepository the review repository
     */
    public SecurityService(UserRepository userRepository,
                           ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * Retrieves the ID of the currently authenticated user from the SecurityContext.
     *
     * @return the ID of the authenticated user
     * @throws RuntimeException if no user is authenticated
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User)) {
            throw new RuntimeException("No authenticated user found");
        }

        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        String username = principal.getUsername();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    /**
     * Checks if the currently authenticated user owns a given review.
     *
     * @param reviewId the ID of the review
     * @return true if the authenticated user is the owner, false otherwise
     */
    public boolean isReviewOwner(Long reviewId) {
        Optional<ReviewEntity> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isEmpty()) {
            return false;
        }
        Long currentUserId = getCurrentUserId();
        return reviewOpt.get().getUser().getId().equals(currentUserId);
    }
}