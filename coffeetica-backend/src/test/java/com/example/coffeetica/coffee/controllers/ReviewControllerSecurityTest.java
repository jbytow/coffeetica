package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.models.ReviewDTO;
import com.example.coffeetica.coffee.models.ReviewRequestDTO;
import com.example.coffeetica.coffee.services.ReviewService;
import com.example.coffeetica.coffee.util.CoffeeTestData;
import com.example.coffeetica.user.security.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security tests for {@link ReviewController}, verifying authentication/authorization
 * rules like "hasRole('Admin')" and "@securityService.isReviewOwner(#id)".
 */
@SpringBootTest
@AutoConfigureMockMvc // so real Spring Security filters run
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReviewControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    /**
     * We mock SecurityService because the controller uses
     * "@securityService.isReviewOwner(#id)" in @PreAuthorize.
     * This lets us control how that method behaves (owner or not).
     */
    @MockBean(name = "securityService")
    private SecurityService securityService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Example: GET /api/reviews/all is permitAll, so an unauthenticated user
     * can access => expect 200 OK.
     */
    @Test
    void testGetAllReviews_Unauthenticated_Returns200() throws Exception {
        mockMvc.perform(get("/api/reviews/all"))
                .andExpect(status().isOk());
    }

    /**
     * POST /api/reviews requires "isAuthenticated()",
     * so an unauthenticated request => 401 Unauthorized.
     */
    @Test
    void testCreateReview_Unauthenticated_Returns401() throws Exception {
        ReviewRequestDTO request = CoffeeTestData.createTestReviewRequestDTO();
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    /**
     * If we are authenticated (role=User), we can create a review => 201 Created
     * (assuming no other constraints).
     */
    @Test
    @WithMockUser(username = "someUser", roles = "User") // or @WithMockUser(roles="User")
    void testCreateReview_UserAuthenticated_Returns201() throws Exception {
        ReviewRequestDTO request = CoffeeTestData.createTestReviewRequestDTO();
        String json = objectMapper.writeValueAsString(request);

        // The service will return a new review with ID=10
        ReviewDTO createdDto = CoffeeTestData.createTestReviewDTO();
        createdDto.setId(10L);

        when(reviewService.saveReview(any())).thenReturn(createdDto);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));
    }

    /**
     * PUT /api/reviews/{id} => hasRole('Admin') or isReviewOwner(#id).
     * If we are neither Admin nor the owner, we expect 403.
     */
    @Test
    @WithMockUser(username = "someUser", roles = "User")
    void testUpdateReview_NonOwnerAndNonAdmin_Returns403() throws Exception {
        long reviewId = 123L;
        // We are "someUser", but let's say securityService.isReviewOwner(123) returns false.
        when(securityService.isReviewOwner(reviewId)).thenReturn(false);

        ReviewRequestDTO updateReq = CoffeeTestData.createTestReviewRequestDTO();
        updateReq.setContent("Modified content");
        String json = objectMapper.writeValueAsString(updateReq);

        mockMvc.perform(put("/api/reviews/{id}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    /**
     * If the user is the owner, isReviewOwner(#id) => true => 200 OK is allowed.
     */
    @Test
    @WithMockUser(username = "someUser", roles = "User")
    void testUpdateReview_Owner_Returns200() throws Exception {
        long reviewId = 123L;
        when(securityService.isReviewOwner(reviewId)).thenReturn(true);

        // Suppose the service returns an updated review
        ReviewDTO updatedReview = CoffeeTestData.createTestReviewDTO();
        updatedReview.setId(reviewId);
        updatedReview.setContent("Modified content");
        when(reviewService.updateReview(eq(reviewId), any())).thenReturn(updatedReview);

        ReviewRequestDTO updateReq = CoffeeTestData.createTestReviewRequestDTO();
        updateReq.setContent("Modified content");
        String json = objectMapper.writeValueAsString(updateReq);

        mockMvc.perform(put("/api/reviews/{id}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId))
                .andExpect(jsonPath("$.content").value("Modified content"));
    }

    /**
     * If the user is Admin, we also expect 200 on update,
     * ignoring the isReviewOwner check.
     */
    @Test
    @WithMockUser(username = "adminUser", roles = "Admin")
    void testUpdateReview_Admin_Returns200() throws Exception {
        long reviewId = 999L;
        // Admin => no need to check isReviewOwner
        // We can still mock it if we want, but not strictly necessary:
        when(securityService.isReviewOwner(reviewId)).thenReturn(false);

        ReviewDTO updated = CoffeeTestData.createTestReviewDTO();
        updated.setId(reviewId);
        updated.setContent("Admin changed content");
        when(reviewService.updateReview(eq(reviewId), any())).thenReturn(updated);

        ReviewRequestDTO req = CoffeeTestData.createTestReviewRequestDTO();
        req.setContent("Admin changed content");
        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(put("/api/reviews/{id}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId))
                .andExpect(jsonPath("$.content").value("Admin changed content"));
    }

    /**
     * Similar logic for DELETE /api/reviews/{id}, which also requires
     * "hasRole('Admin') or isReviewOwner(#id)".
     * If not owner or admin => 403, if admin => 204, if owner => 204, etc.
     */
    @Test
    @WithMockUser(roles = "User", username = "someUser")
    void testDeleteReview_NonOwnerNonAdmin_Returns403() throws Exception {
        long reviewId = 456L;
        when(securityService.isReviewOwner(reviewId)).thenReturn(false);

        mockMvc.perform(delete("/api/reviews/{id}", reviewId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "User", username = "ownerUser")
    void testDeleteReview_Owner_Returns204() throws Exception {
        long reviewId = 456L;
        when(securityService.isReviewOwner(reviewId)).thenReturn(true);

        // service call
        doNothing().when(reviewService).deleteReview(reviewId);

        mockMvc.perform(delete("/api/reviews/{id}", reviewId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "Admin", username = "adminUser")
    void testDeleteReview_Admin_Returns204() throws Exception {
        long reviewId = 999L;
        doNothing().when(reviewService).deleteReview(reviewId);

        mockMvc.perform(delete("/api/reviews/{id}", reviewId))
                .andExpect(status().isNoContent());
    }
}
