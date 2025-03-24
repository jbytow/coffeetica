package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.models.CoffeeEntity;
import com.example.coffeetica.coffee.models.ReviewDTO;
import com.example.coffeetica.coffee.models.ReviewEntity;
import com.example.coffeetica.coffee.models.ReviewRequestDTO;
import com.example.coffeetica.coffee.repositories.CoffeeRepository;
import com.example.coffeetica.coffee.repositories.ReviewRepository;
import com.example.coffeetica.coffee.util.CoffeeTestData;

import com.example.coffeetica.exceptions.ResourceNotFoundException;
import com.example.coffeetica.user.models.UserEntity;
import com.example.coffeetica.user.repositories.UserRepository;
import com.example.coffeetica.user.security.SecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import com.example.coffeetica.user.security.JwtTokenProvider;



import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;



/**
 * Unit tests for {@link ReviewServiceImpl}, verifying CRUD operations
 * and using CoffeeTestData to create sample requests and entities.
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CoffeeRepository coffeeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private ReviewEntity sampleReviewEntity;
    private ReviewDTO sampleReviewDTO;
    private ReviewRequestDTO sampleRequestDTO;
    private UserEntity sampleUser;
    private CoffeeEntity sampleCoffee;

    /**
     * Initializes reusable test data before each test method,
     * using methods from CoffeeTestData.
     */
    @BeforeEach
    void setUp() {
        // Existing test entities/DTO from your CoffeeTestData
        sampleReviewEntity = CoffeeTestData.createTestReviewEntity();
        sampleReviewDTO = CoffeeTestData.createTestReviewDTO();

        // New: a standard request shape from CoffeeTestData
        sampleRequestDTO = CoffeeTestData.createTestReviewRequestDTO();

        sampleUser = sampleReviewEntity.getUser();
        sampleCoffee = sampleReviewEntity.getCoffee();
    }

    /**
     * Tests that saving a review using a ReviewRequestDTO
     * successfully creates and returns a ReviewDTO.
     */
    @Test
    void testSaveReviewSuccess() {
        Long currentUserId = sampleUser.getId();

        // Current user matches the user in sampleUser
        when(securityService.getCurrentUserId()).thenReturn(currentUserId);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(sampleUser));
        when(coffeeRepository.findById(sampleRequestDTO.getCoffeeId()))
                .thenReturn(Optional.of(sampleCoffee));

        // ModelMapper stubs
        when(modelMapper.map(sampleRequestDTO, ReviewEntity.class)).thenReturn(sampleReviewEntity);
        when(reviewRepository.save(sampleReviewEntity)).thenReturn(sampleReviewEntity);
        when(modelMapper.map(sampleReviewEntity, ReviewDTO.class)).thenReturn(sampleReviewDTO);

        // Action
        ReviewDTO result = reviewService.saveReview(sampleRequestDTO);

        // Verification
        assertEquals(sampleReviewDTO, result);
        verify(securityService).getCurrentUserId();
        verify(userRepository).findById(currentUserId);
        verify(coffeeRepository).findById(sampleRequestDTO.getCoffeeId());
        verify(reviewRepository).save(sampleReviewEntity);
    }

    /**
     * Tests that saving a review throws ResourceNotFoundException
     * if the user is missing in the database.
     */
    @Test
    void testSaveReviewThrowsWhenUserNotFound() {
        Long invalidUserId = 99999L;
        when(securityService.getCurrentUserId()).thenReturn(invalidUserId);
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.saveReview(sampleRequestDTO));
        verifyNoInteractions(coffeeRepository, reviewRepository);
    }

    /**
     * Tests that saving a review throws ResourceNotFoundException
     * if the coffee is missing in the database.
     */
    @Test
    void testSaveReviewThrowsWhenCoffeeNotFound() {
        Long currentUserId = sampleUser.getId();
        when(securityService.getCurrentUserId()).thenReturn(currentUserId);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(sampleUser));
        when(coffeeRepository.findById(sampleRequestDTO.getCoffeeId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.saveReview(sampleRequestDTO));
        verifyNoInteractions(reviewRepository);
    }

    /**
     * Tests that findReviewById returns a matching ReviewDTO if present.
     */
    @Test
    void testFindReviewByIdFound() {
        Long reviewId = 1L;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(sampleReviewEntity));
        when(modelMapper.map(sampleReviewEntity, ReviewDTO.class)).thenReturn(sampleReviewDTO);

        Optional<ReviewDTO> result = reviewService.findReviewById(reviewId);
        assertTrue(result.isPresent());
        assertEquals(sampleReviewDTO, result.get());
    }

    /**
     * Tests that findReviewById returns an empty optional if the review doesn't exist.
     */
    @Test
    void testFindReviewByIdNotFound() {
        Long notExistingId = 999L;
        when(reviewRepository.findById(notExistingId)).thenReturn(Optional.empty());

        Optional<ReviewDTO> result = reviewService.findReviewById(notExistingId);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests that listing all reviews returns an empty paginated result when no reviews exist.
     */
    @Test
    void testFindAllReviewsEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ReviewEntity> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(reviewRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<ReviewDTO> reviews = reviewService.findAllReviews(pageable);

        assertTrue(reviews.isEmpty());
    }

    /**
     * Tests that listing all reviews returns a non-empty paginated result when reviews exist,
     * verifying correct mapping to ReviewDTO.
     */
    @Test
    void testFindAllReviewsNonEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ReviewEntity> reviewEntityPage = new PageImpl<>(List.of(sampleReviewEntity), pageable, 1);

        when(reviewRepository.findAll(pageable)).thenReturn(reviewEntityPage);
        when(modelMapper.map(sampleReviewEntity, ReviewDTO.class)).thenReturn(sampleReviewDTO);

        Page<ReviewDTO> reviews = reviewService.findAllReviews(pageable);

        assertEquals(1, reviews.getTotalElements());
        assertEquals(sampleReviewDTO, reviews.getContent().get(0));
    }

    /**
     * Tests that findReviewsByCoffeeId returns a page of reviews for a given coffee.
     */
    @Test
    void testFindReviewsByCoffeeId() {
        Page<ReviewEntity> entityPage = new PageImpl<>(List.of(sampleReviewEntity));
        when(reviewRepository.findByCoffeeId(eq(sampleCoffee.getId()), any(Pageable.class)))
                .thenReturn(entityPage);
        when(modelMapper.map(sampleReviewEntity, ReviewDTO.class)).thenReturn(sampleReviewDTO);

        Page<ReviewDTO> result = reviewService.findReviewsByCoffeeId(sampleCoffee.getId(), Pageable.unpaged());
        assertFalse(result.isEmpty());
        assertEquals(sampleReviewDTO, result.getContent().get(0));
    }

    /**
     * Tests that deleteReview calls repository.deleteById with no checks,
     * meaning no exception is thrown if the ID doesn't exist.
     */
    @Test
    void testDeleteReview() {
        reviewService.deleteReview(1L);
        verify(reviewRepository).deleteById(1L);
    }

    /**
     * Tests that updateReview updates a review's fields if user is the owner.
     */
    @Test
    void testUpdateReviewSuccess() {
        Long reviewId = sampleReviewEntity.getId();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(sampleReviewEntity));

        // Suppose the current user is the same as sampleEntity.getUser()
        when(securityService.getCurrentUserId()).thenReturn(sampleUser.getId());

        // Suppose after saving, we map back to the same sampleReviewDTO
        when(reviewRepository.save(sampleReviewEntity)).thenReturn(sampleReviewEntity);
        when(modelMapper.map(sampleReviewEntity, ReviewDTO.class)).thenReturn(sampleReviewDTO);

        // Create a request from test data but override some fields:
        ReviewRequestDTO updateRequest = CoffeeTestData.createTestReviewRequestDTO();
        updateRequest.setContent("Updated content");
        updateRequest.setBrewingMethod("French Press");
        updateRequest.setBrewingDescription("Updated description");
        updateRequest.setRating(4.5);

        ReviewDTO updated = reviewService.updateReview(reviewId, updateRequest);

        // Check the fields on the entity
        assertEquals("Updated content", sampleReviewEntity.getContent());
        assertEquals("French Press", sampleReviewEntity.getBrewingMethod());
        assertEquals("Updated description", sampleReviewEntity.getBrewingDescription());
        assertEquals(4.5, sampleReviewEntity.getRating());

        // The final returned object is sampleReviewDTO
        assertEquals(sampleReviewDTO, updated);
    }

    /**
     * Tests that updateReview throws ResourceNotFoundException if the review is missing.
     */
    @Test
    void testUpdateReviewNotFound() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());
        ReviewRequestDTO updateRequest = CoffeeTestData.createTestReviewRequestDTO();

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.updateReview(999L, updateRequest));
    }

    /**
     * Tests that updateReview throws a runtime exception if the current user
     * is not the owner of the review.
     */
    @Test
    void testUpdateReviewNotOwner() {
        Long reviewId = sampleReviewEntity.getId();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(sampleReviewEntity));

        // Suppose the current user is different from sampleEntity.getUser().
        when(securityService.getCurrentUserId()).thenReturn(99999L);

        ReviewRequestDTO request = CoffeeTestData.createTestReviewRequestDTO();
        request.setContent("Attempt to update someone else's review");

        assertThrows(RuntimeException.class,
                () -> reviewService.updateReview(reviewId, request));
    }
}