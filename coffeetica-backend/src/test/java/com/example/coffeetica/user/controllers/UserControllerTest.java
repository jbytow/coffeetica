package com.example.coffeetica.user.controllers;

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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testRegisterUserReturnsHTTP201() throws Exception {
        UserDTO userDTO = UserTestData.createTestUserDTO();
        when(userService.registerNewUserAccount(any(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userDTO.getId()))
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()));
    }

    @Test
    public void testUpdateUserReturnsHTTP200() throws Exception {
        UserDTO originalUserDTO = UserTestData.createTestUserDTO();
        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setId(originalUserDTO.getId());
        updatedUserDTO.setUsername("updatedUser");
        updatedUserDTO.setRoles(Set.of("USER"));

        when(userService.updateUser(any(UserDTO.class))).thenReturn(updatedUserDTO);

        String userJson = objectMapper.writeValueAsString(updatedUserDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", originalUserDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedUserDTO.getId()))
                .andExpect(jsonPath("$.username").value("updatedUser"));
    }

    @Test
    public void testDeleteUserReturnsHTTP204() throws Exception {
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetUserByUsernameReturnsHTTP200() throws Exception {
        UserDTO userDTO = UserTestData.createTestUserDTO();
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(userDTO.getUsername())
                .password("password")
                .authorities(userDTO.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList()))
                .build();

        when(userService.loadUserByUsername("testUser")).thenReturn(userDetails);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{username}", "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()));
    }

    @Test
    public void testGetUserByUsernameReturnsHTTP404WhenNotFound() throws Exception {
        String username = "nonexistentUser";
        when(userService.loadUserByUsername(username)).thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{username}", username))
                .andExpect(status().isNotFound());
    }
}