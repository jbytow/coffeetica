package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.models.CoffeeDTO;
import com.example.coffeetica.coffee.models.CoffeeDetailsDTO;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.services.CoffeeService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Integration-style test class for the Coffee controller layer,
 * using MockMvc to simulate HTTP requests, just like RoasteryControllerTest.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // ignoring security filters for direct functional tests
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WithMockUser(username = "defaultUser", roles = "Admin")
public class CoffeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CoffeeService coffeeService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Tests creation of a new Coffee via HTTP POST, expecting a 201 Created status
     * and verifying JSON response structure.
     *
     * @throws Exception if the request or JSON parsing fails
     */
    @Test
    public void testThatCoffeeIsCreatedReturnsHTTP201() throws Exception {
        CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        String coffeeJson = objectMapper.writeValueAsString(coffeeDTO);

        when(coffeeService.saveCoffee(any(CoffeeDTO.class))).thenReturn(coffeeDTO);

        mockMvc.perform(post("/api/coffees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(coffeeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(coffeeDTO.getName()))
                .andExpect(jsonPath("$.countryOfOrigin").value(coffeeDTO.getCountryOfOrigin()));
    }

    /**
     * Tests updating an existing Coffee via HTTP PUT, expecting a 200 OK status
     * and verifying the updated JSON response matches the changed field.
     *
     * @throws Exception if the request or JSON parsing fails
     */
    @Test
    public void testThatCoffeeIsUpdatedReturnsHTTP200() throws Exception {
        CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        coffeeDTO.setRegion(Region.AFRICA); // new data
        String coffeeJson = objectMapper.writeValueAsString(coffeeDTO);

        when(coffeeService.updateCoffee(anyLong(), any(CoffeeDTO.class))).thenReturn(coffeeDTO);

        mockMvc.perform(put("/api/coffees/{id}", coffeeDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(coffeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(coffeeDTO.getId()))
                .andExpect(jsonPath("$.region").value(coffeeDTO.getRegion().getDisplayName()));
    }

    /**
     * Tests the scenario where updating a Coffee returns 404 Not Found
     * if the coffee does not exist.
     *
     * @throws Exception if the request or JSON parsing fails
     */
    @Test
    public void testThatUpdateCoffeeReturns404WhenNotFound() throws Exception {
        CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        coffeeDTO.setId(9999L); // non-existent ID
        String coffeeJson = objectMapper.writeValueAsString(coffeeDTO);

        when(coffeeService.updateCoffee(eq(9999L), any(CoffeeDTO.class)))
                .thenThrow(new ResourceNotFoundException("Coffee not found with ID: 9999"));

        mockMvc.perform(put("/api/coffees/{id}", coffeeDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(coffeeJson))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests retrieval of a Coffee by ID and verifies a 404 response
     * when the coffee is not found in the service layer.
     *
     * @throws Exception if the request fails
     */
    @Test
    public void testThatRetrieveCoffeeReturns404WhenCoffeeNotFound() throws Exception {
        when(coffeeService.findCoffeeById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/coffees/99999999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests retrieval of an existing Coffee by ID, expecting 200 OK
     * and verifying the Coffee data in JSON response.
     *
     * @throws Exception if the request fails
     */
    @Test
    public void testThatRetrieveCoffeeReturnsHttp200AndCoffeeWhenExists() throws Exception {
        CoffeeDetailsDTO coffeeDetailsDTO  = CoffeeTestData.createTestCoffeeDetailsDTO();
        when(coffeeService.findCoffeeDetails(coffeeDetailsDTO.getId())).thenReturn(Optional.of(coffeeDetailsDTO));

        mockMvc.perform(get("/api/coffees/{id}", coffeeDetailsDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(coffeeDetailsDTO.getId()))
                .andExpect(jsonPath("$.name").value(coffeeDetailsDTO.getName()))
                .andExpect(jsonPath("$.countryOfOrigin").value(coffeeDetailsDTO.getCountryOfOrigin()));
    }

    /**
     * Tests listing of all coffees returns 200 OK with an empty JSON array
     * if no coffees exist in the system.
     *
     * @throws Exception if the request fails
     */
    @Test
    public void testThatListCoffeeReturnsHttp200EmptyListWhenNoCoffeesExist() throws Exception {
        Page<CoffeeDTO> emptyPage = new PageImpl<>(Collections.emptyList());
        when(coffeeService.findCoffees(
                eq(null), eq(null), eq(null), eq(null),
                eq(null), eq(null), eq(null), eq(null),
                eq(null), eq(null), any(Pageable.class)
        )).thenReturn(emptyPage);

        mockMvc.perform(get("/api/coffees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    /**
     * Tests listing of all coffees returns 200 OK with a non-empty JSON array
     * when coffees exist in the system.
     *
     * @throws Exception if the request fails
     */
    @Test
    public void testThatListCoffeesReturnsHttp200AndCoffeesWhenCoffeesExist() throws Exception {
        CoffeeDTO coffeeDTO = new CoffeeDTO();
        coffeeDTO.setId(1L);
        coffeeDTO.setName("Test Coffee");
        Page<CoffeeDTO> coffeePage = new PageImpl<>(List.of(coffeeDTO));

        when(coffeeService.findCoffees(
                eq(null), eq(null), eq(null), eq(null),
                eq(null), eq(null), eq(null), eq(null),
                eq(null), eq(null), any(Pageable.class)
        )).thenReturn(coffeePage);

        mockMvc.perform(get("/api/coffees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(coffeeDTO.getId()))
                .andExpect(jsonPath("$.content[0].name").value("Test Coffee"));
    }

    /**
     * Tests that deleting a coffee returns 204 No Content even if the coffee
     * does not exist, assuming the service layer does not throw an exception.
     *
     * @throws Exception if the request fails
     */
    @Test
    public void testThatHttp204IsReturnedWhenCoffeeDoesntExist() throws Exception {
        long nonExistentCoffeeId = 213213213L;
        doNothing().when(coffeeService).deleteCoffee(nonExistentCoffeeId);

        mockMvc.perform(delete("/api/coffees/{id}", nonExistentCoffeeId))
                .andExpect(status().isNoContent());
    }

    /**
     * Tests that deleting an existing coffee returns 204 No Content,
     * verifying no exceptions are thrown by the service layer.
     *
     * @throws Exception if the request fails
     */
    @Test
    public void testThatHttp204IsReturnedWhenExistingCoffeeIsDeleted() throws Exception {
        CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        doNothing().when(coffeeService).deleteCoffee(coffeeDTO.getId());

        mockMvc.perform(delete("/api/coffees/{id}", coffeeDTO.getId()))
                .andExpect(status().isNoContent());
    }

    /**
     * Tests that creating a coffee with a blank name fails validation,
     * returning 400 Bad Request.
     *
     * @throws Exception if the request or JSON parsing fails
     */
    @Test
    public void testCreateCoffeeFailsValidationWhenNameIsBlank() throws Exception {
        CoffeeDTO invalidDto = new CoffeeDTO();
        invalidDto.setName(""); // blank
        invalidDto.setCountryOfOrigin("Brazil");
        invalidDto.setProcessingMethod("Washed");
        invalidDto.setProductionYear(2024);
        // roastery required
        invalidDto.setRoastery(CoffeeTestData.createTestRoasteryDTO());

        mockMvc.perform(post("/api/coffees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests that filtering coffees by name returns a paginated JSON result
     * containing the matching coffees.
     *
     * @throws Exception if the request or JSON parsing fails
     */
    @Test
    void testGetFilteredCoffeesByName() throws Exception {
        // given
        CoffeeDTO coffeeDTO = new CoffeeDTO();
        coffeeDTO.setId(1L);
        coffeeDTO.setName("My Filtered Coffee");
        coffeeDTO.setCountryOfOrigin("TestLand");
        coffeeDTO.setRegion(Region.AFRICA);
        coffeeDTO.setProductionYear(2022);

        Page<CoffeeDTO> pageResult = new PageImpl<>(List.of(coffeeDTO));

        // We expect the service to be called with name="My Filtered",
        // and all other filter params = null.
        when(coffeeService.findCoffees(
                eq("My Filtered"),    // name
                eq(null),             // countryOfOrigin
                eq(null),             // region
                eq(null),             // roastLevel
                eq(null),             // flavorProfile
                eq(null),             // flavorNotes
                eq(null),             // processingMethod
                eq(null),             // minProductionYear
                eq(null),             // maxProductionYear
                eq(null),             // roasteryName
                any(Pageable.class))
        ).thenReturn(pageResult);

        // when & then
        mockMvc.perform(get("/api/coffees")
                        // Provide the filter param in the request
                        .param("name", "My Filtered")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("My Filtered Coffee"))
                .andExpect(jsonPath("$.content[0].countryOfOrigin").value("TestLand"))
                .andExpect(jsonPath("$.content[0].region").value("Africa"));
    }

    /**
     * Tests uploading an image for a coffee, expecting 200 OK on success.
     *
     * @throws Exception if the request fails
     */
    @Test
    public void testUploadCoffeeImageReturnsOk() throws Exception {
        // Mock the service
        doNothing().when(coffeeService).updateCoffeeImageUrl(eq(1L), anyString());

        MockMultipartFile file = new MockMultipartFile(
                "file", "coffee.jpg", "image/jpeg", "TestImage".getBytes()
        );
        mockMvc.perform(multipart("/api/coffees/1/upload-image").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("File uploaded successfully")));
    }
}