package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.models.ReviewDTO;
import com.example.coffeetica.coffee.models.ReviewRequestDTO;
import com.example.coffeetica.coffee.services.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

/**
 * REST controller for managing user reviews for coffees.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Constructs a new {@link ReviewController}.
     *
     * @param reviewService the review service
     */
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Retrieves all reviews with pagination, sorted by creation date.
     *
     * @param pageable pagination configuration
     * @return a page of all reviews
     */
    @GetMapping("/all")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<ReviewDTO>> getAllReviews(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.findAllReviews(pageable));
    }

    /**
     * Retrieves reviews filtered by optional coffee or user ID, with pagination and sorting.
     *
     * @param coffeeId the coffee ID to filter by (optional)
     * @param userId the user ID to filter by (optional)
     * @param page page index
     * @param size page size
     * @param sortBy sorting field
     * @param direction sort direction (asc/desc)
     * @return a page of matching reviews
     */
    @GetMapping
    @PreAuthorize("permitAll()")
    public Page<ReviewDTO> getReviews(
            @RequestParam(required = false) Long coffeeId,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        if (userId != null) {
            return reviewService.findReviewsByUserId(userId, pageable);
        } else if (coffeeId != null) {
            return reviewService.findReviewsByCoffeeId(coffeeId, pageable);
        }

        return Page.empty();
    }

    /**
     * Retrieves a single review by its ID.
     *
     * @param id the review ID
     * @return the matching review DTO, or 404 if not found
     */
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        Optional<ReviewDTO> reviewDTO = reviewService.findReviewById(id);
        return reviewDTO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Retrieves a review for the current user and specific coffee.
     *
     * @param coffeeId the coffee ID
     * @param token the authorization token
     * @return the review if exists, otherwise no content
     */
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> getUserReview(
            @RequestParam Long coffeeId,
            @RequestHeader("Authorization") String token) {

        return reviewService.findReviewByUserAndCoffeeId(token, coffeeId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /**
     * Creates a new review for the current user.
     *
     * @param reviewRequestDTO the review request payload
     * @return the created review DTO with 201 status
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        ReviewDTO savedReviewDTO = reviewService.saveReview(reviewRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReviewDTO);
    }

    /**
     * Updates an existing review by its ID.
     * Allowed for Admins or the owner of the review.
     *
     * @param id the review ID
     * @param reviewRequestDTO the updated review data
     * @return the updated review DTO or 404 if not found
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin') or @securityService.isReviewOwner(#id)")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id,
                                                  @Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        try {
            ReviewDTO updatedReviewDTO = reviewService.updateReview(id, reviewRequestDTO);
            return ResponseEntity.ok(updatedReviewDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Deletes a review by its ID.
     * Allowed for Admins or the owner of the review.
     *
     * @param id the review ID
     * @return 204 No Content on success
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin') or @securityService.isReviewOwner(#id)")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
