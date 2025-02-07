package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.models.ReviewDTO;
import com.example.coffeetica.coffee.models.ReviewEntity;
import com.example.coffeetica.coffee.repositories.CoffeeRepository;
import com.example.coffeetica.coffee.repositories.ReviewRepository;
import com.example.coffeetica.coffee.services.ReviewService;
import com.example.coffeetica.user.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ReviewDTO> findAllReviews() {
        return reviewRepository.findAll().stream()
                .map(entity -> {
                    ReviewDTO reviewDTO = modelMapper.map(entity, ReviewDTO.class);
                    reviewDTO.setUserId(entity.getUser().getId());
                    reviewDTO.setUserName(entity.getUser().getUsername());
                    reviewDTO.setCoffeeId(entity.getCoffee().getId());
                    return reviewDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReviewDTO> findReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(entity -> {
                    ReviewDTO reviewDTO = modelMapper.map(entity, ReviewDTO.class);
                    reviewDTO.setUserId(entity.getUser().getId());
                    reviewDTO.setUserName(entity.getUser().getUsername());
                    reviewDTO.setCoffeeId(entity.getCoffee().getId());
                    return reviewDTO;
                });
    }

    @Override
    public ReviewDTO saveReview(ReviewDTO reviewDTO) {
        ReviewEntity entity = modelMapper.map(reviewDTO, ReviewEntity.class);
        entity.setUser(userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        entity.setCoffee(coffeeRepository.findById(reviewDTO.getCoffeeId())
                .orElseThrow(() -> new RuntimeException("Coffee not found")));
        ReviewEntity savedEntity = reviewRepository.save(entity);
        return modelMapper.map(savedEntity, ReviewDTO.class);
    }

    @Override
    public ReviewDTO updateReview(Long id, ReviewDTO reviewDetails) {
        ReviewEntity entity = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        modelMapper.map(reviewDetails, entity);
        entity.setUser(userRepository.findById(reviewDetails.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        entity.setCoffee(coffeeRepository.findById(reviewDetails.getCoffeeId())
                .orElseThrow(() -> new RuntimeException("Coffee not found")));
        ReviewEntity updatedEntity = reviewRepository.save(entity);
        return modelMapper.map(updatedEntity, ReviewDTO.class);
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
