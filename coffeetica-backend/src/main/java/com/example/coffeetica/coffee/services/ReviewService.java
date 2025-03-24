package com.example.coffeetica.coffee.services;

import com.example.coffeetica.coffee.models.ReviewDTO;
import com.example.coffeetica.coffee.models.ReviewRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface defining operations for managing reviews.
 */
public interface ReviewService {

    /**
     * Retrieves all reviews (unfiltered).
     *
     * @return a a page of reviews
     */
    Page<ReviewDTO> findAllReviews(Pageable pageable);

    /**
     * Retrieves a page of reviews for a specific coffee ID.
     *
     * @param coffeeId the coffee ID
     * @param pageable pagination and sorting info
     * @return a page of reviews
     */
    Page<ReviewDTO> findReviewsByCoffeeId(Long coffeeId, Pageable pageable);

    /**
     * Retrieves a page of reviews for a specific user ID.
     *
     * @param userId the user ID
     * @param pageable pagination and sorting info
     * @return a page of reviews
     */
    Page<ReviewDTO> findReviewsByUserId(Long userId, Pageable pageable);

    /**
     * Finds a single review by its ID.
     *
     * @param id the review ID
     * @return an optional containing the review if found
     */
    Optional<ReviewDTO> findReviewById(Long id);

    /**
     * Finds a review for the currently authenticated user and a specific coffee.
     *
     * @param token the JWT token of the user
     * @param coffeeId the coffee ID
     * @return an optional containing the review if found
     */
    Optional<ReviewDTO> findReviewByUserAndCoffeeId(String token, Long coffeeId);

    /**
     * Creates a new review.
     *
     * @param reviewRequestDTO the review data
     * @return the saved review DTO
     */
    ReviewDTO saveReview(ReviewRequestDTO reviewRequestDTO);

    /**
     * Updates an existing review by its ID.
     *
     * @param id the review ID
     * @param reviewRequestDTO the new review data
     * @return the updated review DTO
     */
    ReviewDTO updateReview(Long id, ReviewRequestDTO reviewRequestDTO);

    /**
     * Deletes a review by its ID.
     *
     * @param id the review ID
     */
    void deleteReview(Long id);
}