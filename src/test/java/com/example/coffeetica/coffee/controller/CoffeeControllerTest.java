package com.example.coffeetica.coffee.controller;

import com.example.coffeetica.coffee.model.CoffeeDTO;
import com.example.coffeetica.coffee.services.CoffeeService;
import com.example.coffeetica.coffee.util.TestData;

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
public class CoffeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CoffeeService coffeeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testThatCoffeeIsCreatedReturnsHTTP201() throws Exception {
        final CoffeeDTO coffeeDTO = TestData.createTestCoffeeDTO();
        final String coffeeJson = objectMapper.writeValueAsString(coffeeDTO);

        when(coffeeService.saveCoffee(any(CoffeeDTO.class))).thenReturn(coffeeDTO);

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
        final String coffeeJson = objectMapper.writeValueAsString(coffeeDTO);

        when(coffeeService.updateCoffee(anyLong(), any(CoffeeDTO.class))).thenReturn(coffeeDTO);

        coffeeDTO.setRegion("Africa");

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/coffees/{id}", coffeeDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(coffeeJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(coffeeDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.region").value(coffeeDTO.getRegion()));
    }

    @Test
    public void testThatRetrieveCoffeeReturns404WhenCoffeeNotFound() throws Exception {
        when(coffeeService.findCoffeeById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/coffees/123123123"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatRetrieveCoffeeReturnsHttp200AndCoffeeWhenExists() throws Exception {
        final CoffeeDTO coffeeDTO = TestData.createTestCoffeeDTO();
        when(coffeeService.findCoffeeById(coffeeDTO.getId())).thenReturn(Optional.of(coffeeDTO));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/coffees/{id}", coffeeDTO.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(coffeeDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(coffeeDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.countryOfOrigin").value(coffeeDTO.getCountryOfOrigin()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.region").value(coffeeDTO.getRegion()));
    }

    @Test
    public void testThatListCoffeeReturnsHttp200EmptyListWhenNoCoffeesExist() throws Exception {
        when(coffeeService.findAllCoffees()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/coffees/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    public void testThatListCoffeesReturnsHttp200AndCoffeesWhenCoffeesExist() throws Exception {
        CoffeeDTO coffeeDTO = new CoffeeDTO();
        coffeeDTO.setId(1L);
        coffeeDTO.setName("Test Coffee");

        when(coffeeService.findAllCoffees()).thenReturn(Arrays.asList(coffeeDTO));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/coffees/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(coffeeDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Test Coffee"));
    }

    @Test
    public void testThatHttp204IsReturnedWhenCoffeeDoesntExist() throws Exception {
        final long nonExistentCoffeeId = 213213213L;

        doNothing().when(coffeeService).deleteCoffee(nonExistentCoffeeId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/coffees/{id}", nonExistentCoffeeId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testThatHttp204IsReturnedWhenExistingCoffeeIsDeleted() throws Exception {
        final CoffeeDTO coffeeDTO = TestData.createTestCoffeeDTO();
        doNothing().when(coffeeService).deleteCoffee(coffeeDTO.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/coffees/{id}", coffeeDTO.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
