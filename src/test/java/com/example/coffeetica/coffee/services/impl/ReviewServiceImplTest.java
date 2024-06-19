package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.models.CoffeeEntity;
import com.example.coffeetica.coffee.models.ReviewDTO;
import com.example.coffeetica.coffee.models.ReviewEntity;
import com.example.coffeetica.coffee.repositories.CoffeeRepository;
import com.example.coffeetica.coffee.repositories.ReviewRepository;
import com.example.coffeetica.coffee.util.CoffeeTestData;

import com.example.coffeetica.user.models.UserEntity;
import com.example.coffeetica.user.repositories.UserRepository;
import com.example.coffeetica.user.util.UserTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository; // Mock UserRepository

    @Mock
    private CoffeeRepository coffeeRepository; // Mock CoffeeRepository

    @InjectMocks
    private ReviewServiceImpl underTest;

    @Test
    public void testThatReviewIsSaved() {
        ReviewDTO reviewDTO = CoffeeTestData.createTestReviewDTO();
        ReviewEntity reviewEntity = CoffeeTestData.createTestReviewEntity();
        UserEntity userEntity = UserTestData.createTestUserEntity(); // Create test user data
        CoffeeEntity coffeeEntity = CoffeeTestData.createTestCoffeeEntity(); // Create test coffee data

        // Mocking the behavior of ModelMapper
        when(modelMapper.map(reviewDTO, ReviewEntity.class)).thenReturn(reviewEntity);
        when(reviewRepository.save(reviewEntity)).thenReturn(reviewEntity);
        when(modelMapper.map(reviewEntity, ReviewDTO.class)).thenReturn(reviewDTO);

        // Mocking the behavior of UserRepository
        when(userRepository.findById(reviewDTO.getUserId())).thenReturn(Optional.of(userEntity)); // Mock UserRepository

        // Mocking the behavior of CoffeeRepository
        when(coffeeRepository.findById(reviewDTO.getCoffeeId())).thenReturn(Optional.of(coffeeEntity)); // Mock CoffeeRepository

        // Action
        ReviewDTO result = underTest.saveReview(reviewDTO);

        // Assertions
        assertEquals(reviewDTO, result);
        verify(modelMapper).map(reviewDTO, ReviewEntity.class);
        verify(reviewRepository).save(reviewEntity);
        verify(modelMapper).map(reviewEntity, ReviewDTO.class);
        verify(userRepository).findById(reviewDTO.getUserId()); // Verify UserRepository call
        verify(coffeeRepository).findById(reviewDTO.getCoffeeId()); // Verify CoffeeRepository call
    }

    @Test
    public void testThatFindByIdReturnsReviewWhenExists() {
        Long id = 1L;
        ReviewDTO reviewDTO = CoffeeTestData.createTestReviewDTO();
        ReviewEntity reviewEntity = CoffeeTestData.createTestReviewEntity();

        // Mocking the behavior of ReviewRepository and ModelMapper
        when(reviewRepository.findById(id)).thenReturn(Optional.of(reviewEntity));
        when(modelMapper.map(reviewEntity, ReviewDTO.class)).thenReturn(reviewDTO);

        // Action
        Optional<ReviewDTO> result = underTest.findReviewById(id);

        // Assertions
        assertTrue(result.isPresent());
        assertEquals(reviewDTO, result.get());
        verify(reviewRepository).findById(id);
        verify(modelMapper).map(reviewEntity, ReviewDTO.class);
    }

    @Test
    public void testThatFindByIdReturnsEmptyWhenNoReview() {
        Long id = 1L;

        // Mocking the behavior of ReviewRepository
        when(reviewRepository.findById(id)).thenReturn(Optional.empty());

        // Action
        Optional<ReviewDTO> result = underTest.findReviewById(id);

        // Assertions
        assertEquals(Optional.empty(), result);
        verify(reviewRepository).findById(id);
    }

    @Test
    public void testListReviewsReturnsEmptyListWhenNoReviewsExist() {
        // Mocking the behavior of ReviewRepository
        when(reviewRepository.findAll()).thenReturn(new ArrayList<>());

        // Action
        List<ReviewDTO> result = underTest.findAllReviews();

        // Assertions
        assertTrue(result.isEmpty());
        verify(reviewRepository).findAll();
    }

    @Test
    public void testListReviewsReturnsReviewsWhenExist() {
        ReviewDTO reviewDTO = CoffeeTestData.createTestReviewDTO();
        ReviewEntity reviewEntity = CoffeeTestData.createTestReviewEntity();

        // Mocking the behavior of ReviewRepository and ModelMapper
        when(reviewRepository.findAll()).thenReturn(List.of(reviewEntity));
        when(modelMapper.map(reviewEntity, ReviewDTO.class)).thenReturn(reviewDTO);

        // Action
        List<ReviewDTO> result = underTest.findAllReviews();

        // Assertions
        assertEquals(1, result.size());
        assertEquals(reviewDTO, result.get(0));
        verify(reviewRepository).findAll();
        verify(modelMapper).map(reviewEntity, ReviewDTO.class);
    }

    @Test
    public void testDeleteReviewDeletesReview() {
        Long id = 1L;

        // Action
        underTest.deleteReview(id);

        // Assertions
        verify(reviewRepository, times(1)).deleteById(id);
    }
}