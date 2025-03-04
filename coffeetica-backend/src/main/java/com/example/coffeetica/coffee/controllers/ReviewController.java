package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.models.ReviewDTO;
import com.example.coffeetica.coffee.models.ReviewRequestDTO;
import com.example.coffeetica.coffee.repositories.ReviewRepository;
import com.example.coffeetica.coffee.services.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private ReviewService reviewService;

    @Autowired
    ReviewRepository reviewRepository;

    @GetMapping("/api/reviews/all")
    @PreAuthorize("permitAll()")
    public List<ReviewDTO> getAllReviews() {
        return reviewService.findAllReviews();
    }

    @GetMapping("/api/reviews")
    @PreAuthorize("permitAll()")
    public Page<ReviewDTO> getReviewsByCoffeeId(
            @RequestParam Long coffeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            // Default sorting by creation date
            @RequestParam(defaultValue = "createdAt") String sortBy,
            // Sorting direction – used only for rating
            @RequestParam(required = false) String direction) {

        Sort sort;
        if ("createdAt".equalsIgnoreCase(sortBy)) {
            // Sorting by date always from newest to oldest
            sort = Sort.by("createdAt").descending();
        } else if ("rating".equalsIgnoreCase(sortBy)) {
            // When sorting by rating, the direction depends on the provided parameter
            if ("asc".equalsIgnoreCase(direction)) {
                sort = Sort.by("rating").ascending();
            } else {
                sort = Sort.by("rating").descending();
            }
        } else {
            // If an unknown criterion is provided, apply default sorting by date
            sort = Sort.by("createdAt").descending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return reviewService.findReviewsByCoffeeId(coffeeId, pageable);
    }

    @GetMapping("/api/reviews/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        Optional<ReviewDTO> reviewDTO = reviewService.findReviewById(id);
        return reviewDTO
                .map(review -> new ResponseEntity<>(review, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/api/reviews/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> getUserReview(
            @RequestParam Long coffeeId,
            @RequestHeader("Authorization") String token) {

        return reviewService.findReviewByUserAndCoffeeId(token, coffeeId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/api/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        ReviewDTO savedReviewDTO = reviewService.saveReview(reviewRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReviewDTO);
    }

    @PutMapping("/api/reviews/{id}")
    @PreAuthorize("hasRole('Admin') or @securityService.isReviewOwner(#id)")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        try {
            ReviewDTO updatedReviewDTO = reviewService.updateReview(id, reviewRequestDTO);
            return ResponseEntity.ok(updatedReviewDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/api/reviews/{id}")
    @PreAuthorize("hasRole('Admin') or @securityService.isReviewOwner(#id)")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
