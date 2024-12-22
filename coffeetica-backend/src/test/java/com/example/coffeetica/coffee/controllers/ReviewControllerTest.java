package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.models.ReviewDTO;
import com.example.coffeetica.coffee.services.ReviewService;
import com.example.coffeetica.coffee.util.CoffeeTestData;
import com.example.coffeetica.user.models.UserDTO;
import com.example.coffeetica.user.services.UserService;
import com.example.coffeetica.user.util.UserTestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testThatReviewIsCreatedReturnsHTTP201() throws Exception {
        final ReviewDTO reviewDTO = CoffeeTestData.createTestReviewDTO();
        final UserDTO userDTO = UserTestData.createTestUserDTO();
        reviewDTO.setUserId(userDTO.getId());
        final String reviewJson = objectMapper.writeValueAsString(reviewDTO);

        when(userService.findUserById(userDTO.getId())).thenReturn(Optional.of(userDTO));
        when(reviewService.saveReview(any(ReviewDTO.class))).thenReturn(reviewDTO);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reviewJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(reviewDTO.getContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brewingMethod").value(reviewDTO.getBrewingMethod()));
    }

    @Test
    public void testThatReviewIsUpdatedReturnsHTTP200() throws Exception {
        final ReviewDTO reviewDTO = CoffeeTestData.createTestReviewDTO();
        final UserDTO userDTO = UserTestData.createTestUserDTO();
        reviewDTO.setUserId(userDTO.getId());
        final String reviewJson = objectMapper.writeValueAsString(reviewDTO);

        when(userService.findUserById(userDTO.getId())).thenReturn(Optional.of(userDTO));
        when(reviewService.updateReview(anyLong(), any(ReviewDTO.class))).thenReturn(reviewDTO);

        reviewDTO.setBrewingMethod("French Press");

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/reviews/{id}", reviewDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reviewJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(reviewDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brewingMethod").value(reviewDTO.getBrewingMethod()));
    }

    @Test
    public void testThatRetrieveReviewReturns404WhenReviewNotFound() throws Exception {
        when(reviewService.findReviewById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/123123123"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatRetrieveReviewReturnsHttp200AndReviewWhenExists() throws Exception {
        final ReviewDTO reviewDTO = CoffeeTestData.createTestReviewDTO();
        final UserDTO userDTO = UserTestData.createTestUserDTO();
        reviewDTO.setUserId(userDTO.getId());

        // Mockowanie odpowiedzi UserService
        when(userService.findUserById(userDTO.getId())).thenReturn(Optional.of(userDTO));
        when(reviewService.findReviewById(reviewDTO.getId())).thenReturn(Optional.of(reviewDTO));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/{id}", reviewDTO.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(reviewDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(reviewDTO.getContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brewingMethod").value(reviewDTO.getBrewingMethod()));
    }

    @Test
    public void testThatListReviewsReturnsHttp200EmptyListWhenNoReviewsExist() throws Exception {
        when(reviewService.findAllReviews()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    public void testThatListReviewsReturnsHttp200AndReviewsWhenReviewsExist() throws Exception {
        ReviewDTO reviewDTO = CoffeeTestData.createTestReviewDTO();
        final UserDTO userDTO = UserTestData.createTestUserDTO();
        reviewDTO.setUserId(userDTO.getId());

        // Mockowanie odpowiedzi UserService
        when(userService.findUserById(userDTO.getId())).thenReturn(Optional.of(userDTO));
        when(reviewService.findAllReviews()).thenReturn(Arrays.asList(reviewDTO));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(reviewDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].content").value(reviewDTO.getContent()));
    }

    @Test
    public void testThatHttp204IsReturnedWhenReviewDoesntExist() throws Exception {
        final long nonExistentReviewId = 213213213L;

        doNothing().when(reviewService).deleteReview(nonExistentReviewId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reviews/{id}", nonExistentReviewId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testThatHttp204IsReturnedWhenExistingReviewIsDeleted() throws Exception {
        final ReviewDTO reviewDTO = CoffeeTestData.createTestReviewDTO();
        final UserDTO userDTO = UserTestData.createTestUserDTO();
        reviewDTO.setUserId(userDTO.getId());

        when(userService.findUserById(userDTO.getId())).thenReturn(Optional.of(userDTO));
        doNothing().when(reviewService).deleteReview(reviewDTO.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reviews/{id}", reviewDTO.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}