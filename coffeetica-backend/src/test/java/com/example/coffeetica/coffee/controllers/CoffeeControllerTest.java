package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.models.CoffeeDTO;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.services.CoffeeService;
import com.example.coffeetica.coffee.util.CoffeeTestData;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
        final CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        final String coffeeJson = objectMapper.writeValueAsString(coffeeDTO);

        when(coffeeService.saveCoffee(any(CoffeeDTO.class))).thenReturn(coffeeDTO);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/coffees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(coffeeJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(coffeeDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.countryOfOrigin").value(coffeeDTO.getCountryOfOrigin()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.region").value(coffeeDTO.getRegion().getDisplayName()));
    }

    @Test
    public void testThatCoffeeIsUpdatedReturnsHTTP200() throws Exception {
        final CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        final String coffeeJson = objectMapper.writeValueAsString(coffeeDTO);

        when(coffeeService.updateCoffee(anyLong(), any(CoffeeDTO.class))).thenReturn(coffeeDTO);

        coffeeDTO.setRegion(Region.AFRICA);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/coffees/{id}", coffeeDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(coffeeJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(coffeeDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.region").value(coffeeDTO.getRegion().getDisplayName()));
    }

    @Test
    public void testThatRetrieveCoffeeReturns404WhenCoffeeNotFound() throws Exception {
        when(coffeeService.findCoffeeById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/coffees/123123123"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatRetrieveCoffeeReturnsHttp200AndCoffeeWhenExists() throws Exception {
        final CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        when(coffeeService.findCoffeeById(coffeeDTO.getId())).thenReturn(Optional.of(coffeeDTO));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/coffees/{id}", coffeeDTO.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(coffeeDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(coffeeDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.countryOfOrigin").value(coffeeDTO.getCountryOfOrigin()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.region").value(coffeeDTO.getRegion().getDisplayName()));
    }

    @Test
    public void testThatListCoffeeReturnsHttp200EmptyListWhenNoCoffeesExist() throws Exception {
        Page<CoffeeDTO> emptyPage = new PageImpl<>(Collections.emptyList());

        when(coffeeService.findAllCoffees(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/coffees?page=0&size=5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
    }

    @Test
    public void testThatListCoffeesReturnsHttp200AndCoffeesWhenCoffeesExist() throws Exception {
        CoffeeDTO coffeeDTO = new CoffeeDTO();
        coffeeDTO.setId(1L);
        coffeeDTO.setName("Test Coffee");

        List<CoffeeDTO> coffeeList = Arrays.asList(coffeeDTO);
        Page<CoffeeDTO> coffeePage = new PageImpl<>(coffeeList);

        when(coffeeService.findAllCoffees(any(Pageable.class))).thenReturn(coffeePage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/coffees?page=0&size=5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(coffeeDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Test Coffee"));
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
        final CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        doNothing().when(coffeeService).deleteCoffee(coffeeDTO.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/coffees/{id}", coffeeDTO.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
