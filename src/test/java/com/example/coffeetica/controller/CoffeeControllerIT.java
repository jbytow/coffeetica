package com.example.coffeetica.controller;

import com.example.coffeetica.model.CoffeeDTO;
import com.example.coffeetica.services.CoffeeService;
import com.example.coffeetica.util.TestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CoffeeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CoffeeService coffeeService;

    @Test
    public void testThatCoffeeIsCreatedReturnsHTTP201() throws Exception {
        final CoffeeDTO coffeeDTO = TestData.createTestCoffeeDTO();
        final ObjectMapper objectMapper = new ObjectMapper();
        final String coffeeJson = objectMapper.writeValueAsString(coffeeDTO);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/coffees/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(coffeeJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(coffeeDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.countryOfOrigin").value(coffeeDTO.getCountryOfOrigin()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.region").value(coffeeDTO.getRegion()));
    }

    @Test
    public void testThatCoffeeIsUpdatedReturnsHTTP200() throws Exception {
        final CoffeeDTO coffeeDTO = TestData.createTestCoffeeDTO();
        coffeeService.saveCoffee(coffeeDTO);

        coffeeDTO.setRoastery("New Roastery");

        final ObjectMapper objectMapper = new ObjectMapper();
        final String coffeeJson = objectMapper.writeValueAsString(coffeeDTO);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/coffees/{id}", coffeeDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(coffeeJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(coffeeDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roastery").value(coffeeDTO.getRoastery()));
    }

    @Test
    public void testThatRetrieveCoffeeReturns404WhenCoffeeNotFound() throws Exception {
        mockMvc.perform(get("/api/coffees/123123123"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatRetrieveCoffeeReturnsHttp200AndCoffeeWhenExists() throws Exception {
        final CoffeeDTO coffeeDTO = TestData.createTestCoffeeDTO();
        coffeeService.saveCoffee(coffeeDTO);

        mockMvc.perform(get("/api/coffees/{id}", coffeeDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(coffeeDTO.getId()))
                .andExpect(jsonPath("$.name").value(coffeeDTO.getName()))
                .andExpect(jsonPath("$.countryOfOrigin").value(coffeeDTO.getCountryOfOrigin()))
                .andExpect(jsonPath("$.region").value(coffeeDTO.getRegion()));
    }

    @Test
    public void testThatListCoffeeReturnsHttp200EmptyListWhenNoCoffeesExist() throws Exception {
        mockMvc.perform(get("/api/coffees/"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void testThatListCoffeesReturnsHttp200AndCoffeesWhenCoffeesExist() throws Exception {
        final CoffeeDTO coffeeDTO = TestData.createTestCoffeeDTO();
        coffeeService.saveCoffee(coffeeDTO);

        mockMvc.perform(get("/api/coffees/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(coffeeDTO.getId()))
                .andExpect(jsonPath("$.[0].name").value(coffeeDTO.getName()))
                .andExpect(jsonPath("$.[0].countryOfOrigin").value(coffeeDTO.getCountryOfOrigin()))
                .andExpect(jsonPath("$.[0].region").value(coffeeDTO.getRegion()));
    }

    @Test
    public void testThatHttp204IsReturnedWhenCoffeeDoesntExist() throws Exception {
        final long nonExistentCoffeeId = 213213213L;

        mockMvc.perform(delete("/api/coffees/{id}", nonExistentCoffeeId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testThatHttp204IsReturnedWhenExistingCoffeeIsDeleted() throws Exception {
        final CoffeeDTO coffeeDTO = TestData.createTestCoffeeDTO();

        mockMvc.perform(delete("/api/coffees/{id}", coffeeDTO.getId()))
                .andExpect(status().isNoContent());
    }
}
