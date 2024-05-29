package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.model.ReviewDTO;
import com.example.coffeetica.coffee.model.ReviewEntity;
import com.example.coffeetica.coffee.repositories.ReviewRepository;
import com.example.coffeetica.coffee.services.impl.ReviewServiceImpl;
import com.example.coffeetica.coffee.util.TestData;

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

    @InjectMocks
    private ReviewServiceImpl underTest;

    @Test
    public void testThatReviewIsSaved() {
        ReviewDTO reviewDTO = TestData.createTestReviewDTO();
        ReviewEntity reviewEntity = TestData.createTestReviewEntity();

        // Mocking the behavior of ModelMapper
        when(modelMapper.map(reviewDTO, ReviewEntity.class)).thenReturn(reviewEntity);
        when(reviewRepository.save(reviewEntity)).thenReturn(reviewEntity);
        when(modelMapper.map(reviewEntity, ReviewDTO.class)).thenReturn(reviewDTO);

        // Action
        ReviewDTO result = underTest.saveReview(reviewDTO);

        // Assertions
        assertEquals(reviewDTO, result);
        verify(modelMapper).map(reviewDTO, ReviewEntity.class);
        verify(reviewRepository).save(reviewEntity);
        verify(modelMapper).map(reviewEntity, ReviewDTO.class);
    }

    @Test
    public void testThatFindByIdReturnsReviewWhenExists() {
        Long id = 1L;
        ReviewDTO reviewDTO = TestData.createTestReviewDTO();
        ReviewEntity reviewEntity = TestData.createTestReviewEntity();

        when(reviewRepository.findById(id)).thenReturn(Optional.of(reviewEntity));
        when(modelMapper.map(reviewEntity, ReviewDTO.class)).thenReturn(reviewDTO);

        Optional<ReviewDTO> result = underTest.findReviewById(id);

        assertTrue(result.isPresent());
        assertEquals(reviewDTO, result.get());
        verify(reviewRepository).findById(id);
        verify(modelMapper).map(reviewEntity, ReviewDTO.class);
    }

    @Test
    public void testThatFindByIdReturnEmptyWhenNoReview() {
        Long id = 1L;
        when(reviewRepository.findById(id)).thenReturn(Optional.empty());

        Optional<ReviewDTO> result = underTest.findReviewById(id);

        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testListReviewsReturnsEmptyListWhenNoReviewsExist() {
        when(reviewRepository.findAll()).thenReturn(new ArrayList<>());

        List<ReviewDTO> result = underTest.findAllReviews();

        assertTrue(result.isEmpty());
    }

    @Test
    public void testListReviewsReturnsReviewsWhenExist() {
        ReviewDTO reviewDTO = TestData.createTestReviewDTO();
        ReviewEntity reviewEntity = TestData.createTestReviewEntity();

        when(reviewRepository.findAll()).thenReturn(List.of(reviewEntity));
        when(modelMapper.map(reviewEntity, ReviewDTO.class)).thenReturn(reviewDTO);

        List<ReviewDTO> result = underTest.findAllReviews();

        assertEquals(1, result.size());
        assertEquals(reviewDTO, result.get(0));
        verify(modelMapper).map(reviewEntity, ReviewDTO.class);
    }

    @Test
    public void testDeleteReviewDeletesReview() {
        Long id = 1L;

        underTest.deleteReview(id);

        verify(reviewRepository, times(1)).deleteById(id);
    }
}