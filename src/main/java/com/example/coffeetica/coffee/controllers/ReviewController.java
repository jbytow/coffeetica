package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.model.ReviewDTO;
import com.example.coffeetica.coffee.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/api/reviews/")
    public List<ReviewDTO> getAllReviews() {
        return reviewService.findAllReviews();
    }

    @GetMapping("/api/reviews/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        Optional<ReviewDTO> reviewDTO = reviewService.findReviewById(id);
        return reviewDTO
                .map(review -> new ResponseEntity<>(review, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/api/reviews/")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO) {
        ReviewDTO savedReviewDTO = reviewService.saveReview(reviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReviewDTO);
    }

    @PutMapping("/api/reviews/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id, @RequestBody ReviewDTO reviewDetails) {
        try {
            ReviewDTO updatedReviewDTO = reviewService.updateReview(id, reviewDetails);
            return ResponseEntity.ok(updatedReviewDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/api/reviews/{id}")
    public ResponseEntity deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
