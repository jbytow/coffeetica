package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.models.ReviewDTO;
import com.example.coffeetica.coffee.models.ReviewRequestDTO;
import com.example.coffeetica.coffee.services.ReviewService;
import com.example.coffeetica.coffee.util.CoffeeTestData;
import com.example.coffeetica.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;



/**
 * Integration-style tests for ReviewController, using CoffeeTestData
 * to create typical request/response objects.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WithMockUser(username = "defaultUser", roles = "Admin")
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Tests creation of a review (POST /api/reviews) returning 201 Created.
     *
     * @throws Exception if request or JSON fails
     */
    @Test
    void testThatReviewIsCreatedReturnsHTTP201() throws Exception {
        // Use your new test-data builder for the request
        ReviewRequestDTO request = CoffeeTestData.createTestReviewRequestDTO();

        // The service returns a ReviewDTO
        ReviewDTO returnedDTO = CoffeeTestData.createTestReviewDTO();
        returnedDTO.setId(10L);

        String requestJson = objectMapper.writeValueAsString(request);

        when(reviewService.saveReview(any(ReviewRequestDTO.class))).thenReturn(returnedDTO);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.brewingMethod").value("Espresso"))
                .andExpect(jsonPath("$.content").value("Great coffee with a nutty flavor."));
    }

    /**
     * Tests updating a review (PUT /api/reviews/{id}) returns 200 OK
     * if found and updated successfully.
     */
    @Test
    void testThatReviewIsUpdatedReturnsHTTP200() throws Exception {
        long reviewId = 1L;
        // Create a standard request
        ReviewRequestDTO updateRequest = CoffeeTestData.createTestReviewRequestDTO();
        // Modify some fields for the "update"
        updateRequest.setRating(4.2);
        updateRequest.setBrewingMethod("French Press");
        updateRequest.setContent("Changed content");

        // The updated result
        ReviewDTO updatedReview = CoffeeTestData.createTestReviewDTO();
        updatedReview.setId(reviewId);
        updatedReview.setRating(4.2);
        updatedReview.setBrewingMethod("French Press");
        updatedReview.setContent("Changed content");

        String json = objectMapper.writeValueAsString(updateRequest);

        when(reviewService.updateReview(eq(reviewId), any(ReviewRequestDTO.class))).thenReturn(updatedReview);

        mockMvc.perform(put("/api/reviews/{id}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(4.2))
                .andExpect(jsonPath("$.brewingMethod").value("French Press"))
                .andExpect(jsonPath("$.content").value("Changed content"));
    }

    /**
     * Tests updating a review returns 404 if the service throws a ResourceNotFoundException.
     */
    @Test
    void testThatUpdateReviewReturns404WhenNotFound() throws Exception {
        long reviewId = 999L;
        ReviewRequestDTO request = CoffeeTestData.createTestReviewRequestDTO();

        doThrow(new ResourceNotFoundException("Review not found: " + reviewId))
                .when(reviewService).updateReview(eq(reviewId), any(ReviewRequestDTO.class));

        mockMvc.perform(put("/api/reviews/{id}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests retrieving a review returns 404 if it doesn't exist.
     */
    @Test
    void testThatRetrieveReviewReturns404WhenReviewNotFound() throws Exception {
        when(reviewService.findReviewById(eq(999L))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reviews/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests retrieving an existing review returns 200 OK and the correct JSON.
     */
    @Test
    void testThatRetrieveReviewReturnsHttp200AndReviewWhenExists() throws Exception {
        ReviewDTO existingReview = CoffeeTestData.createTestReviewDTO();
        existingReview.setId(2L);

        when(reviewService.findReviewById(2L)).thenReturn(Optional.of(existingReview));

        mockMvc.perform(get("/api/reviews/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.content").value("Great coffee with a nutty flavor."));
    }

    /**
     * Tests that retrieving all reviews with pagination returns 200 OK and an empty content list
     * when no reviews exist in the system.
     *
     * @throws Exception if the request fails
     */
    @Test
    void testThatListReviewsReturnsHttp200EmptyListWhenNoReviewsExist() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<ReviewDTO> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(reviewService.findAllReviews(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/reviews/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.number").value(0));
    }

    /**
     * Tests that retrieving all reviews with pagination returns 200 OK and a paginated
     * response containing review data when at least one review exists.
     *
     * @throws Exception if the request fails
     */
    @Test
    void testThatListReviewsReturnsHttp200AndReviewsWhenReviewsExist() throws Exception {
        ReviewDTO sampleReview = CoffeeTestData.createTestReviewDTO();
        sampleReview.setId(5L);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<ReviewDTO> reviewPage = new PageImpl<>(List.of(sampleReview), pageable, 1);

        when(reviewService.findAllReviews(any(Pageable.class))).thenReturn(reviewPage);

        mockMvc.perform(get("/api/reviews/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(5L))
                .andExpect(jsonPath("$.content[0].content").value("Great coffee with a nutty flavor."))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.number").value(0));
    }

    /**
     * Tests that deleting a review returns 204 No Content,
     * even if it doesn't exist (assuming the service doesn't throw an error).
     */
    @Test
    void testThatHttp204IsReturnedWhenReviewDoesntExist() throws Exception {
        long nonExistentReviewId = 123456L;
        doNothing().when(reviewService).deleteReview(nonExistentReviewId);

        mockMvc.perform(delete("/api/reviews/{id}", nonExistentReviewId))
                .andExpect(status().isNoContent());
    }

    /**
     * Tests that deleting an existing review returns 204 No Content.
     */
    @Test
    void testThatHttp204IsReturnedWhenExistingReviewIsDeleted() throws Exception {
        long existingId = 1L;
        doNothing().when(reviewService).deleteReview(existingId);

        mockMvc.perform(delete("/api/reviews/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    /**
     * Tests creating a review fails with 400 Bad Request if mandatory fields
     * in the ReviewRequestDTO are invalid or missing.
     */
    @Test
    void testCreateReviewFailsValidation() throws Exception {
        // For example, we build a valid request then break a field
        ReviewRequestDTO invalidRequest = CoffeeTestData.createTestReviewRequestDTO();
        invalidRequest.setRating(null); // fails @NotNull
        invalidRequest.setContent("");  // fails @NotBlank

        String requestJson = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}
