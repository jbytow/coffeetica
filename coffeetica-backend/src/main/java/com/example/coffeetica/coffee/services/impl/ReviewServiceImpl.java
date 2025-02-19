package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.models.CoffeeEntity;
import com.example.coffeetica.coffee.models.ReviewDTO;
import com.example.coffeetica.coffee.models.ReviewEntity;
import com.example.coffeetica.coffee.models.ReviewRequestDTO;
import com.example.coffeetica.coffee.repositories.CoffeeRepository;
import com.example.coffeetica.coffee.repositories.ReviewRepository;
import com.example.coffeetica.coffee.services.ReviewService;
import com.example.coffeetica.user.models.UserEntity;
import com.example.coffeetica.user.repositories.UserRepository;
import com.example.coffeetica.user.security.JwtTokenProvider;
import com.example.coffeetica.user.security.SecurityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private SecurityService securityService;

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
    public Page<ReviewDTO> findReviewsByCoffeeId(Long coffeeId, Pageable pageable) {
        return reviewRepository.findByCoffeeId(coffeeId, pageable)
                .map(entity -> {
                    ReviewDTO reviewDTO = modelMapper.map(entity, ReviewDTO.class);
                    reviewDTO.setUserId(entity.getUser().getId());
                    reviewDTO.setUserName(entity.getUser().getUsername());
                    reviewDTO.setCoffeeId(entity.getCoffee().getId());
                    return reviewDTO;
                });
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
    public Optional<ReviewDTO> findReviewByUserAndCoffeeId(String token, Long coffeeId) {
        Long userId = getUserIdFromToken(token); // get userId from username

        return reviewRepository.findByUserIdAndCoffeeId(userId, coffeeId)
                .map(entity -> {
                    ReviewDTO reviewDTO = modelMapper.map(entity, ReviewDTO.class);
                    reviewDTO.setUserId(entity.getUser().getId());
                    reviewDTO.setUserName(entity.getUser().getUsername());
                    return reviewDTO;
                });
    }

    @Override
    public ReviewDTO saveReview(ReviewRequestDTO reviewRequestDTO) {

        // 1) Retrieve the ID of the logged-in user from the token (JWT)
        Long userId = securityService.getCurrentUserId();

        // 2) Find the user in the database
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3) Find the coffee in the database based on coffeeId from the request
        CoffeeEntity coffee = coffeeRepository.findById(reviewRequestDTO.getCoffeeId())
                .orElseThrow(() -> new RuntimeException("Coffee not found"));

        // 4) Map ReviewRequestDTO -> ReviewEntity
        ReviewEntity entity = modelMapper.map(reviewRequestDTO, ReviewEntity.class);

        // Manually assign relationships (user, coffee)
        entity.setUser(user);
        entity.setCoffee(coffee);

        // 5) Save to the repository
        ReviewEntity savedEntity = reviewRepository.save(entity);

        // 6) Map ReviewEntity -> ReviewDTO (for response)
        ReviewDTO savedReviewDTO = modelMapper.map(savedEntity, ReviewDTO.class);

        // ModelMapper does not always transfer ID relationships, so they often need to be manually set
        savedReviewDTO.setUserId(savedEntity.getUser().getId());
        savedReviewDTO.setUserName(savedEntity.getUser().getUsername());
        savedReviewDTO.setCoffeeId(savedEntity.getCoffee().getId());
        savedReviewDTO.setCreatedAt(savedEntity.getCreatedAt().toString());

        return savedReviewDTO;
    }

    @Override
    public ReviewDTO updateReview(Long id, ReviewRequestDTO reviewRequestDTO) {
        // Retrieve the existing review
        ReviewEntity entity = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Retrieve the ID of the logged-in user
        Long currentUserId = securityService.getCurrentUserId();

        // Check if the user editing the review is its owner
        if (!entity.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("You are not allowed to edit this review.");
        }

        // Update only the fields that can be modified
        entity.setContent(reviewRequestDTO.getContent());
        entity.setBrewingMethod(reviewRequestDTO.getBrewingMethod());
        entity.setBrewingDescription(reviewRequestDTO.getBrewingDescription());
        entity.setRating(reviewRequestDTO.getRating());

        // Save changes
        ReviewEntity updatedEntity = reviewRepository.save(entity);

        // Map the entity to ReviewDTO for response
        ReviewDTO updatedReviewDTO = modelMapper.map(updatedEntity, ReviewDTO.class);
        updatedReviewDTO.setUserId(updatedEntity.getUser().getId());
        updatedReviewDTO.setUserName(updatedEntity.getUser().getUsername());
        updatedReviewDTO.setCoffeeId(updatedEntity.getCoffee().getId());
        updatedReviewDTO.setCreatedAt(updatedEntity.getCreatedAt().toString());

        return updatedReviewDTO;
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    private Long getUserIdFromToken(String token) {
        String username = jwtTokenProvider.getIdentifierFromJWT(token.replace("Bearer ", ""));
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}
