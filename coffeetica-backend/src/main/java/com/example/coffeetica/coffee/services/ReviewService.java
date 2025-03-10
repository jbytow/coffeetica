package com.example.coffeetica.coffee.services;

import com.example.coffeetica.coffee.models.ReviewDTO;
import com.example.coffeetica.coffee.models.ReviewRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<ReviewDTO> findAllReviews();

    Page<ReviewDTO> findReviewsByCoffeeId(Long coffeeId, Pageable pageable);

    Page<ReviewDTO> findReviewsByUserId(Long userId, Pageable pageable);

    Optional<ReviewDTO> findReviewById(Long id);

    Optional<ReviewDTO> findReviewByUserAndCoffeeId(String token, Long coffeeId);

    ReviewDTO saveReview(ReviewRequestDTO reviewRequestDTO);

    ReviewDTO updateReview(Long id, ReviewRequestDTO reviewRequestDTO);

    void deleteReview(Long id);
}
