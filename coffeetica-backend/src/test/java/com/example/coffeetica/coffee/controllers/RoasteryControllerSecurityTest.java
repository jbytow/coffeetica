package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.models.RoasteryDTO;
import com.example.coffeetica.coffee.services.RoasteryService;
import com.example.coffeetica.coffee.util.CoffeeTestData;

import com.example.coffeetica.user.services.UserService;
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

import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;







/**
 * Integration-style tests for verifying security constraints
 * on roastery endpoints (e.g. admin-only operations).
 */
@SpringBootTest
@AutoConfigureMockMvc  // No addFilters=false => security filters are active
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RoasteryControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Mock the service to avoid dealing with real DB operations;
     * we only need to test that security constraints respond as expected.
     */
    @MockBean
    private RoasteryService roasteryService;

    @MockBean
    private UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Tests that creating a roastery (POST /api/roasteries) with no authentication
     * returns 401 Unauthorized, since the endpoint requires an Admin role.
     *
     * @throws Exception if request fails
     */
    @Test
    public void testCreateRoastery_Unauthenticated_Returns401() throws Exception {
        RoasteryDTO roasteryDTO = CoffeeTestData.createTestRoasteryDTO();
        String roasteryJson = objectMapper.writeValueAsString(roasteryDTO);

        mockMvc.perform(post("/api/roasteries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roasteryJson))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that creating a roastery with an authenticated user who has
     * the "User" role (but not "Admin") returns 403 Forbidden.
     *
     * @throws Exception if request fails
     */
    @Test
    @WithMockUser(roles = "User") // spring-security-test annotation
    public void testCreateRoastery_NonAdminUser_Returns403() throws Exception {
        RoasteryDTO roasteryDTO = CoffeeTestData.createTestRoasteryDTO();
        String roasteryJson = objectMapper.writeValueAsString(roasteryDTO);

        mockMvc.perform(post("/api/roasteries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roasteryJson))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that creating a roastery with a user who has the "Admin" role
     * succeeds with HTTP 201 Created.
     *
     * @throws Exception if request fails
     */
    @Test
    @WithMockUser(roles = "Admin")
    public void testCreateRoastery_AdminUser_Returns201() throws Exception {
        RoasteryDTO roasteryDTO = CoffeeTestData.createTestRoasteryDTO();
        String roasteryJson = objectMapper.writeValueAsString(roasteryDTO);

        // The actual creation is not our test focus, so just mock normal behavior
        when(roasteryService.saveRoastery(any(RoasteryDTO.class))).thenReturn(roasteryDTO);

        mockMvc.perform(post("/api/roasteries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roasteryJson))
                .andExpect(status().isCreated());
    }

    /**
     * Tests that updating a roastery with a non-admin user results in 403 Forbidden.
     *
     * @throws Exception if request fails
     */
    @Test
    @WithMockUser(roles = "User")
    public void testUpdateRoastery_NonAdminUser_Returns403() throws Exception {
        RoasteryDTO roasteryDTO = CoffeeTestData.createTestRoasteryDTO();
        String roasteryJson = objectMapper.writeValueAsString(roasteryDTO);

        mockMvc.perform(put("/api/roasteries/{id}", roasteryDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roasteryJson))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that updating a roastery with an admin user results in 200 OK
     * if the service call is successful.
     *
     * @throws Exception if request fails
     */
    @Test
    @WithMockUser(roles = "Admin")
    public void testUpdateRoastery_AdminUser_Returns200() throws Exception {
        RoasteryDTO roasteryDTO = CoffeeTestData.createTestRoasteryDTO();
        roasteryDTO.setCountry("Africa");
        String roasteryJson = objectMapper.writeValueAsString(roasteryDTO);

        when(roasteryService.updateRoastery(eq(roasteryDTO.getId()), any(RoasteryDTO.class)))
                .thenReturn(roasteryDTO);

        mockMvc.perform(put("/api/roasteries/{id}", roasteryDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roasteryJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("Africa"));
    }

    /**
     * Tests that deleting a roastery with no authentication
     * returns 401 Unauthorized, as only Admin can delete roasteries.
     *
     * @throws Exception if request fails
     */
    @Test
    public void testDeleteRoastery_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(delete("/api/roasteries/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that deleting a roastery with a non-admin user
     * returns 403 Forbidden.
     *
     * @throws Exception if request fails
     */
    @Test
    @WithMockUser(roles = "User")
    public void testDeleteRoastery_NonAdminUser_Returns403() throws Exception {
        mockMvc.perform(delete("/api/roasteries/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that deleting a roastery with an admin user returns 204 No Content
     * if the service call is successful.
     *
     * @throws Exception if request fails
     */
    @Test
    @WithMockUser(roles = "Admin")
    public void testDeleteRoastery_AdminUser_Returns204() throws Exception {
        doNothing().when(roasteryService).deleteRoastery(1L);

        mockMvc.perform(delete("/api/roasteries/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}