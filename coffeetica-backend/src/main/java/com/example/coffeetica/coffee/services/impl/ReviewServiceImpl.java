package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.models.CoffeeEntity;
import com.example.coffeetica.coffee.models.ReviewDTO;
import com.example.coffeetica.coffee.models.ReviewEntity;
import com.example.coffeetica.coffee.models.ReviewRequestDTO;
import com.example.coffeetica.coffee.repositories.CoffeeRepository;
import com.example.coffeetica.coffee.repositories.ReviewRepository;
import com.example.coffeetica.coffee.services.ReviewService;
import com.example.coffeetica.exceptions.ResourceNotFoundException;
import com.example.coffeetica.user.models.UserEntity;
import com.example.coffeetica.user.repositories.UserRepository;
import com.example.coffeetica.user.security.JwtTokenProvider;
import com.example.coffeetica.user.security.SecurityService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link ReviewService} interface,
 * handling the creation, retrieval, update, and deletion of reviews.
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CoffeeRepository coffeeRepository;
    private final ModelMapper modelMapper;
    private final JwtTokenProvider jwtTokenProvider; // if used
    private final SecurityService securityService;    // if used

    /**
     * Constructs a new ReviewServiceImpl with the required dependencies.
     *
     * @param reviewRepository the review repository
     * @param userRepository the user repository
     * @param coffeeRepository the coffee repository
     * @param modelMapper the model mapper
     * @param jwtTokenProvider the JWT token provider (optional)
     * @param securityService the security service for retrieving current user ID
     */
    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             UserRepository userRepository,
                             CoffeeRepository coffeeRepository,
                             ModelMapper modelMapper,
                             JwtTokenProvider jwtTokenProvider,
                             SecurityService securityService) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.coffeeRepository = coffeeRepository;
        this.modelMapper = modelMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.securityService = securityService;
    }

    @Override
    public Page<ReviewDTO> findAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable)
                .map(this::mapEntityToDTO);
    }

    @Override
    public Optional<ReviewDTO> findReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(this::mapEntityToDTO);
    }

    @Override
    public Page<ReviewDTO> findReviewsByCoffeeId(Long coffeeId, Pageable pageable) {
        return reviewRepository.findByCoffeeId(coffeeId, pageable)
                .map(this::mapEntityToDTO);
    }

    @Override
    public Page<ReviewDTO> findReviewsByUserId(Long userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable)
                .map(this::mapEntityToDTO);
    }

    @Override
    public Optional<ReviewDTO> findReviewByUserAndCoffeeId(String token, Long coffeeId) {
        Long userId = getUserIdFromToken(token);
        return reviewRepository.findByUserIdAndCoffeeId(userId, coffeeId)
                .map(this::mapEntityToDTO);
    }

    @Override
    public ReviewDTO saveReview(ReviewRequestDTO reviewRequestDTO) {
        Long userId = securityService.getCurrentUserId();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        CoffeeEntity coffee = coffeeRepository.findById(reviewRequestDTO.getCoffeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Coffee not found: " + reviewRequestDTO.getCoffeeId()));

        ReviewEntity entity = modelMapper.map(reviewRequestDTO, ReviewEntity.class);
        entity.setUser(user);
        entity.setCoffee(coffee);
        entity.setCreatedAt(LocalDateTime.now());

        ReviewEntity savedEntity = reviewRepository.save(entity);
        return mapEntityToDTO(savedEntity);
    }

    @Override
    public ReviewDTO updateReview(Long id, ReviewRequestDTO reviewRequestDTO) {
        ReviewEntity entity = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + id));

        // Check ownership
        Long currentUserId = securityService.getCurrentUserId();
        if (!entity.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("You are not allowed to edit this review.");
        }

        // Update fields
        entity.setContent(reviewRequestDTO.getContent());
        entity.setBrewingMethod(reviewRequestDTO.getBrewingMethod());
        entity.setBrewingDescription(reviewRequestDTO.getBrewingDescription());
        entity.setRating(reviewRequestDTO.getRating());

        ReviewEntity updatedEntity = reviewRepository.save(entity);
        return mapEntityToDTO(updatedEntity);
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    /**
     * Helper method to map a ReviewEntity to a ReviewDTO,
     * including user and coffee relationships.
     *
     * @param entity the review entity
     * @return the mapped ReviewDTO
     */
    private ReviewDTO mapEntityToDTO(ReviewEntity entity) {
        ReviewDTO dto = modelMapper.map(entity, ReviewDTO.class);

        // Set relationship fields
        dto.setUserId(entity.getUser().getId());
        dto.setUserName(entity.getUser().getUsername());
        dto.setCoffeeId(entity.getCoffee().getId());
        dto.setCoffeeName(entity.getCoffee().getName());
        dto.setCreatedAt(entity.getCreatedAt().toString());

        return dto;
    }

    /**
     * Retrieves a user ID from the given JWT token.
     *
     * @param token the JWT token that may include "Bearer " prefix
     * @return the user ID
     */
    private Long getUserIdFromToken(String token) {
        String processedToken = token.replace("Bearer ", "");
        String username = jwtTokenProvider.getIdentifierFromJWT(processedToken);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for username: " + username));
        return user.getId();
    }
}