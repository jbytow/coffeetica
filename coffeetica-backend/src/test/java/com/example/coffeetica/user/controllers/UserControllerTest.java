package com.example.coffeetica.user.controllers;

import com.example.coffeetica.user.models.RegisterRequestDTO;
import com.example.coffeetica.user.models.UpdateUserRequestDTO;
import com.example.coffeetica.user.models.UserDTO;
import com.example.coffeetica.user.security.SecurityService;
import com.example.coffeetica.user.services.UserService;
import com.example.coffeetica.user.util.UserTestData;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


/**
 * Functional tests for {@link UserController} endpoints,
 * ignoring security filters. Verifies basic request/response flows.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // skip security filters
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean(name = "securityService")
    private SecurityService securityService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Tests registering a user with POST /api/users/register
     * using {@link RegisterRequestDTO}, expecting 201 on success.
     *
     * @throws Exception if the request or JSON serialization fails
     */
    @Test
    void testRegisterUserReturnsHTTP201() throws Exception {
        // The request object for registration (username, email, password)
        RegisterRequestDTO registerRequest = UserTestData.createTestRegisterRequest();

        // The service returns a final UserDTO with assigned ID, roles, etc.
        UserDTO returnedUserDTO = UserTestData.createTestUserDTO();
        when(userService.registerNewUserAccount(any(RegisterRequestDTO.class))).thenReturn(returnedUserDTO);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(returnedUserDTO.getId()))
                .andExpect(jsonPath("$.username").value(returnedUserDTO.getUsername()));
    }

    /**
     * Tests retrieving a paginated list of users with GET /api/users,
     * expecting 200 OK and an empty list if no users exist.
     *
     * @throws Exception if the request fails
     */
    @Test
    void testGetAllUsersReturns200EmptyWhenNoUsersExist() throws Exception {
        Page<UserDTO> emptyPage = new PageImpl<>(Collections.emptyList());
        when(userService.findAllUsers(isNull(), eq(0), eq(10), eq("id"), eq("asc")))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/users?page=0&size=10&sortBy=id&direction=asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    /**
     * Tests retrieving a specific user by ID with GET /api/users/{id}.
     * Expects 200 OK if found.
     *
     * @throws Exception if the request fails
     */
    @Test
    void testGetUserByIdReturnsHttp200WhenExists() throws Exception {
        Long userId = 1L;
        UserDTO userDTO = UserTestData.createTestUserDTO();
        when(userService.findUserById(userId)).thenReturn(Optional.of(userDTO));

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDTO.getId()))
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()));
    }

    /**
     * Tests retrieving a specific user by ID returns 404 if not found.
     */
    @Test
    void testGetUserByIdReturns404WhenNotFound() throws Exception {
        Long userId = 999L;
        when(userService.findUserById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests updating a user's email with PUT /api/users/{id}/update-email
     * returning 200 OK if successful.
     */
    @Test
    @WithMockUser(username = "testUser", roles = "User")
    void testUpdateUserEmailReturns200() throws Exception {
        Long userId = 1L;

        when(securityService.getCurrentUserId()).thenReturn(1L);

        UpdateUserRequestDTO updateEmailReq = new UpdateUserRequestDTO();
        updateEmailReq.setEmail("newemail@example.com");

        UserDTO updatedUserDTO = UserTestData.createTestUserDTO();
        updatedUserDTO.setEmail("newemail@example.com");

        when(userService.updateUserEmail(eq(userId), any(UpdateUserRequestDTO.class)))
                .thenReturn(updatedUserDTO);

        mockMvc.perform(put("/api/users/{id}/update-email", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateEmailReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newemail@example.com"));
    }

    /**
     * Tests deleting a user with DELETE /api/users/{id}, expecting 204 No Content on success.
     */
    @WithMockUser(username = "defaultUser", roles = "Admin")
    @Test
    void testDeleteUserReturnsHTTP204() throws Exception {
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }
}