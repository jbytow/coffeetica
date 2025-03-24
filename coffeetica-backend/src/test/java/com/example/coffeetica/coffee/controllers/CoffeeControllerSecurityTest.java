package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.models.CoffeeDTO;
import com.example.coffeetica.coffee.models.CoffeeDetailsDTO;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.services.CoffeeService;
import com.example.coffeetica.coffee.util.CoffeeTestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration-style tests for verifying security constraints on coffee endpoints.
 * We enable the Spring Security filter chain to confirm only Admin can create,
 * update, or delete coffees, while GET endpoints are public (permitAll).
 */
@SpringBootTest
@AutoConfigureMockMvc // omit addFilters=false so that Security filters are active
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CoffeeControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CoffeeService coffeeService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Tests that creating a coffee (POST /api/coffees) with no authentication
     * returns 401 Unauthorized, since Admin role is required.
     *
     * @throws Exception if the request fails
     */
    @Test
    void testCreateCoffee_Unauthenticated_Returns401() throws Exception {
        CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        String coffeeJson = objectMapper.writeValueAsString(coffeeDTO);

        mockMvc.perform(post("/api/coffees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(coffeeJson))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that creating a coffee with an authenticated user who is not an Admin
     * returns 403 Forbidden.
     *
     * @throws Exception if the request fails
     */
    @Test
    @WithMockUser(roles = "User")
    void testCreateCoffee_NonAdminUser_Returns403() throws Exception {
        CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        String coffeeJson = objectMapper.writeValueAsString(coffeeDTO);

        mockMvc.perform(post("/api/coffees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(coffeeJson))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that creating a coffee with an Admin user returns 201 Created.
     *
     * @throws Exception if the request fails
     */
    @Test
    @WithMockUser(roles = "Admin")
    void testCreateCoffee_AdminUser_Returns201() throws Exception {
        CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        String coffeeJson = objectMapper.writeValueAsString(coffeeDTO);

        when(coffeeService.saveCoffee(any(CoffeeDTO.class))).thenReturn(coffeeDTO);

        mockMvc.perform(post("/api/coffees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(coffeeJson))
                .andExpect(status().isCreated());
    }

    /**
     * Tests that updating a coffee with no authentication
     * returns 401 Unauthorized.
     *
     * @throws Exception if the request fails
     */
    @Test
    void testUpdateCoffee_Unauthenticated_Returns401() throws Exception {
        CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        String coffeeJson = objectMapper.writeValueAsString(coffeeDTO);

        mockMvc.perform(put("/api/coffees/{id}", coffeeDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(coffeeJson))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that updating a coffee with a non-admin user
     * returns 403 Forbidden.
     *
     * @throws Exception if the request fails
     */
    @Test
    @WithMockUser(roles = "User")
    void testUpdateCoffee_NonAdminUser_Returns403() throws Exception {
        CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        String coffeeJson = objectMapper.writeValueAsString(coffeeDTO);

        mockMvc.perform(put("/api/coffees/{id}", coffeeDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(coffeeJson))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that updating a coffee with an admin user returns 200 OK
     * if the service call is successful.
     *
     * @throws Exception if the request fails
     */
    @Test
    @WithMockUser(roles = "Admin")
    void testUpdateCoffee_AdminUser_Returns200() throws Exception {
        CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        coffeeDTO.setRegion(Region.AFRICA);  // new data
        String coffeeJson = objectMapper.writeValueAsString(coffeeDTO);

        when(coffeeService.updateCoffee(eq(coffeeDTO.getId()), any(CoffeeDTO.class))).thenReturn(coffeeDTO);

        mockMvc.perform(put("/api/coffees/{id}", coffeeDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(coffeeJson))
                .andExpect(status().isOk());
    }

    /**
     * Tests that deleting a coffee (DELETE /api/coffees/{id}) with no authentication
     * returns 401 Unauthorized.
     *
     * @throws Exception if the request fails
     */
    @Test
    void testDeleteCoffee_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(delete("/api/coffees/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that deleting a coffee with a non-admin user
     * returns 403 Forbidden.
     *
     * @throws Exception if the request fails
     */
    @Test
    @WithMockUser(roles = "User")
    void testDeleteCoffee_NonAdminUser_Returns403() throws Exception {
        mockMvc.perform(delete("/api/coffees/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that deleting a coffee with an admin user returns 204 No Content
     * on successful deletion.
     *
     * @throws Exception if the request fails
     */
    @Test
    @WithMockUser(roles = "Admin")
    void testDeleteCoffee_AdminUser_Returns204() throws Exception {
        doNothing().when(coffeeService).deleteCoffee(1L);

        mockMvc.perform(delete("/api/coffees/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    /**
     * Test that GET requests are permitAll(). This means
     * even an unauthenticated request should return 200 (if the coffee exists).
     *
     * @throws Exception if the request fails
     */
    @Test
    void testGetCoffee_Unauthenticated_Returns200_WhenCoffeeExists() throws Exception {
        CoffeeDetailsDTO coffeeDetails = CoffeeTestData.createTestCoffeeDetailsDTO();
        Long coffeeId = 1L;
        coffeeDetails.setId(coffeeId);

        when(coffeeService.findCoffeeDetails(coffeeId))
                .thenReturn(Optional.of(coffeeDetails));

        mockMvc.perform(get("/api/coffees/{id}", coffeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(coffeeId));
    }
}