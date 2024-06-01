package com.example.coffeetica.coffee.controllers;


import com.example.coffeetica.coffee.models.RoasteryDTO;
import com.example.coffeetica.coffee.services.RoasteryService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RoasteryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoasteryService roasteryService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testThatRoasteryIsCreatedReturnsHTTP201() throws Exception {
        final RoasteryDTO roasteryDTO = TestData.createTestRoasteryDTO();
        final String roasteryJson = objectMapper.writeValueAsString(roasteryDTO);

        when(roasteryService.saveRoastery(any(RoasteryDTO.class))).thenReturn(roasteryDTO);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/roasteries/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(roasteryJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(roasteryDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.location").value(roasteryDTO.getLocation()));
    }

    @Test
    public void testThatRoasteryIsUpdatedReturnsHTTP200() throws Exception {
        final RoasteryDTO roasteryDTO = TestData.createTestRoasteryDTO();
        final String roasteryJson = objectMapper.writeValueAsString(roasteryDTO);

        when(roasteryService.updateRoastery(anyLong(), any(RoasteryDTO.class))).thenReturn(roasteryDTO);

        roasteryDTO.setLocation("Africa");

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/roasteries/{id}", roasteryDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(roasteryJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(roasteryDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.location").value(roasteryDTO.getLocation()));
    }

    @Test
    public void testThatRetrieveRoasteryReturns404WhenRoasteryNotFound() throws Exception {
        when(roasteryService.findRoasteryById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roasteries/123123123"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatRetrieveRoasteryReturnsHttp200AndRoasteryWhenExists() throws Exception {
        final RoasteryDTO roasteryDTO = TestData.createTestRoasteryDTO();
        when(roasteryService.findRoasteryById(roasteryDTO.getId())).thenReturn(Optional.of(roasteryDTO));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roasteries/{id}", roasteryDTO.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(roasteryDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(roasteryDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.location").value(roasteryDTO.getLocation()));
    }

    @Test
    public void testThatListRoasteriesReturnsHttp200EmptyListWhenNoRoasteriesExist() throws Exception {
        when(roasteryService.findAllRoasteries()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roasteries/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    public void testThatListRoasteriesReturnsHttp200AndRoasteriesWhenRoasteriesExist() throws Exception {
        RoasteryDTO roasteryDTO = new RoasteryDTO();
        roasteryDTO.setId(1L);
        roasteryDTO.setName("Test Roastery");

        when(roasteryService.findAllRoasteries()).thenReturn(Arrays.asList(roasteryDTO));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roasteries/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(roasteryDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Test Roastery"));
    }

    @Test
    public void testThatHttp204IsReturnedWhenRoasteryDoesntExist() throws Exception {
        final long nonExistentRoasteryId = 213213213L;

        doNothing().when(roasteryService).deleteRoastery(nonExistentRoasteryId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/roasteries/{id}", nonExistentRoasteryId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testThatHttp204IsReturnedWhenExistingRoasteryIsDeleted() throws Exception {
        final RoasteryDTO roasteryDTO = TestData.createTestRoasteryDTO();
        doNothing().when(roasteryService).deleteRoastery(roasteryDTO.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/roasteries/{id}", roasteryDTO.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
