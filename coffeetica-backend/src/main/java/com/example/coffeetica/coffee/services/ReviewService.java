package com.example.coffeetica.coffee.services;

import com.example.coffeetica.coffee.models.ReviewDTO;
import com.example.coffeetica.coffee.models.ReviewRequestDTO;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<ReviewDTO> findAllReviews();

    Optional<ReviewDTO> findReviewById(Long id);

    ReviewDTO saveReview(ReviewRequestDTO reviewRequestDTO);

    ReviewDTO updateReview(Long id, ReviewDTO reviewDetails);

    void deleteReview(Long id);
}
