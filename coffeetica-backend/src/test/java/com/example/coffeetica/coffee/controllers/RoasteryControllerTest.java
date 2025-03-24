package com.example.coffeetica.coffee.controllers;


import com.example.coffeetica.coffee.models.RoasteryDTO;
import com.example.coffeetica.coffee.services.RoasteryService;
import com.example.coffeetica.coffee.util.CoffeeTestData;

import com.example.coffeetica.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration-style test class for the Roastery controller layer,
 * using MockMvc to simulate HTTP requests.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WithMockUser(username = "defaultUser", roles = "Admin")
public class RoasteryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoasteryService roasteryService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Tests creation of a new Roastery via HTTP POST, expecting a 201 Created status
     * and verifying JSON response structure.
     *
     * @throws Exception if the request or JSON parsing fails
     */
    @Test
    public void testThatRoasteryIsCreatedReturnsHTTP201() throws Exception {
        RoasteryDTO roasteryDTO = CoffeeTestData.createTestRoasteryDTO();
        String roasteryJson = objectMapper.writeValueAsString(roasteryDTO);

        when(roasteryService.saveRoastery(any(RoasteryDTO.class))).thenReturn(roasteryDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/roasteries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roasteryJson))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(roasteryDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.country").value(roasteryDTO.getCountry()));
    }

    /**
     * Tests updating an existing Roastery via HTTP PUT, expecting a 200 OK status
     * and verifying the updated JSON response matches the changed field.
     *
     * @throws Exception if the request or JSON parsing fails
     */
    @Test
    public void testThatRoasteryIsUpdatedReturnsHTTP200() throws Exception {
        RoasteryDTO roasteryDTO = CoffeeTestData.createTestRoasteryDTO();
        roasteryDTO.setCountry("Africa"); // new data
        String roasteryJson = objectMapper.writeValueAsString(roasteryDTO);

        when(roasteryService.updateRoastery(anyLong(), any(RoasteryDTO.class))).thenReturn(roasteryDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/roasteries/{id}", roasteryDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roasteryJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(roasteryDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.country").value("Africa"));
    }

    /**
     * Tests the scenario where updating a Roastery returns 404 Not Found
     * if the roastery does not exist.
     *
     * @throws Exception if the request or JSON parsing fails
     */
    @Test
    public void testThatUpdateRoasteryReturns404WhenNotFound() throws Exception {
        RoasteryDTO roasteryDTO = CoffeeTestData.createTestRoasteryDTO();
        roasteryDTO.setId(9999L); // non-existent ID
        String roasteryJson = objectMapper.writeValueAsString(roasteryDTO);

        when(roasteryService.updateRoastery(eq(9999L), any(RoasteryDTO.class)))
                .thenThrow(new ResourceNotFoundException("Roastery not found with ID: 9999"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/roasteries/{id}", roasteryDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roasteryJson))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests retrieval of a Roastery by ID and verifies a 404 response
     * when the roastery is not found in the service layer.
     *
     * @throws Exception if the request fails
     */
    @Test
    public void testThatRetrieveRoasteryReturns404WhenRoasteryNotFound() throws Exception {
        when(roasteryService.findRoasteryById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roasteries/123123123"))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests retrieval of an existing Roastery by ID, expecting 200 OK
     * and verifying the Roastery data in JSON response.
     *
     * @throws Exception if the request fails
     */
    @Test
    public void testThatRetrieveRoasteryReturnsHttp200AndRoasteryWhenExists() throws Exception {
        RoasteryDTO roasteryDTO = CoffeeTestData.createTestRoasteryDTO();
        when(roasteryService.findRoasteryById(roasteryDTO.getId())).thenReturn(Optional.of(roasteryDTO));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roasteries/{id}", roasteryDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(roasteryDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(roasteryDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.country").value(roasteryDTO.getCountry()));
    }

    /**
     * Tests listing of all roasteries returns 200 OK with an empty JSON array
     * if no roasteries exist in the system.
     *
     * @throws Exception if the request fails
     */
    @Test
    public void testThatListRoasteriesReturnsHttp200EmptyListWhenNoRoasteriesExist() throws Exception {
        when(roasteryService.findAllRoasteries()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roasteries"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    /**
     * Tests listing of all roasteries returns 200 OK with a non-empty JSON array
     * when roasteries exist in the system.
     *
     * @throws Exception if the request fails
     */
    @Test
    public void testThatListRoasteriesReturnsHttp200AndRoasteriesWhenRoasteriesExist() throws Exception {
        RoasteryDTO roasteryDTO = CoffeeTestData.createTestRoasteryDTO();
        roasteryDTO.setId(1L);
        roasteryDTO.setName("Test Roastery");
        roasteryDTO.setCountry("Colombia");

        when(roasteryService.findAllRoasteries()).thenReturn(Arrays.asList(roasteryDTO));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roasteries"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(roasteryDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(roasteryDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].country").value(roasteryDTO.getCountry()));
    }

    /**
     * Tests that deleting a roastery returns 204 No Content even if the roastery
     * does not exist, assuming the service layer does not throw an exception in that scenario.
     *
     * @throws Exception if the request fails
     */
    @Test
    public void testThatHttp204IsReturnedWhenRoasteryDoesntExist() throws Exception {
        final long nonExistentRoasteryId = 213213213L;
        doNothing().when(roasteryService).deleteRoastery(nonExistentRoasteryId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/roasteries/{id}", nonExistentRoasteryId))
                .andExpect(status().isNoContent());
    }

    /**
     * Tests that deleting an existing roastery returns 204 No Content,
     * verifying no exceptions are thrown by the service layer.
     *
     * @throws Exception if the request fails
     */
    @Test
    public void testThatHttp204IsReturnedWhenExistingRoasteryIsDeleted() throws Exception {
        RoasteryDTO roasteryDTO = CoffeeTestData.createTestRoasteryDTO();
        doNothing().when(roasteryService).deleteRoastery(roasteryDTO.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/roasteries/{id}", roasteryDTO.getId()))
                .andExpect(status().isNoContent());
    }


    /**
     * Tests that creating a roastery with a blank name fails validation.
     * The endpoint should respond with a 400 Bad Request when the name is invalid or missing.
     *
     * @throws Exception if the mock request or JSON parsing fails
     */
    @Test
    void testCreateRoasteryFailsValidationWhenNameIsBlank() throws Exception {
        RoasteryDTO invalidDto = new RoasteryDTO();
        invalidDto.setName("");
        invalidDto.setCountry("Brazil");
        invalidDto.setFoundingYear(2010);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/roasteries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }


    /**
     * Tests filtering roasteries by name.
     * <p>
     * Given a roastery with the name "My Filtered Roastery", when filtering
     * by "My Filtered", the result should contain the expected roastery.
     *
     * @throws Exception if the request execution fails
     */
    @Test
    void testGetFilteredRoasteriesByName() throws Exception {
        // given
        RoasteryDTO roasteryDTO = new RoasteryDTO();
        roasteryDTO.setId(1L);
        roasteryDTO.setName("My Filtered Roastery");
        roasteryDTO.setCountry("Testland");
        roasteryDTO.setFoundingYear(2020);

        Page<RoasteryDTO> pageResult = new PageImpl<>(List.of(roasteryDTO));
        when(roasteryService.findFilteredRoasteries(
                eq("My Filtered"),
                nullable(String.class),
                nullable(Integer.class),
                nullable(Integer.class),
                any(Pageable.class)
        )).thenReturn(pageResult);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/roasteries/filter")
                        .param("name", "My Filtered"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("My Filtered Roastery"));
    }

    /**
     * Tests uploading an image for a roastery.
     * <p>
     * Given a roastery with ID 1, when a multipart file is uploaded, the response
     * should indicate success.
     *
     * @throws Exception if the request execution fails
     */
    @Test
    void testUploadRoasteryImageReturnsOk() throws Exception {
        // Mock roastery exists
        doNothing().when(roasteryService).updateRoasteryImageUrl(eq(1L), anyString());

        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "TestImage".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/roasteries/1/upload-image").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("File uploaded successfully")));
    }
}