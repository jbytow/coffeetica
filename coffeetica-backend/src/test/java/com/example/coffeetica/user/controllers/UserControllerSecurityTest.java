package com.example.coffeetica.user.controllers;


import com.example.coffeetica.user.models.*;
import com.example.coffeetica.user.repositories.RoleRepository;
import com.example.coffeetica.user.repositories.UserRepository;
import com.example.coffeetica.user.security.SecurityService;
import com.example.coffeetica.user.services.UserService;
import com.example.coffeetica.user.util.UserTestData;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security tests for {@link UserController}, verifying role-based or
 * ownership checks with Spring Security active.
 */
@SpringBootTest
@AutoConfigureMockMvc // ensures the real security filters are active
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private SecurityService securityService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Tests that GET /api/users is permitAll => 200 for an unauthenticated user.
     */
    @Test
    void testGetAllUsers_Unauthenticated_Returns200() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    /**
     * Tests that an Admin can delete a user => 204 No Content.
     * The @PreAuthorize("hasRole('Admin')") check passes if the user has "Admin".
     */
    @Test
    @WithMockUser(roles = "Admin")
    void testDeleteUser_Admin_Returns204() throws Exception {
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    /**
     * Tests that a normal user (role "User") is forbidden from deleting => 403.
     * Because @PreAuthorize("hasRole('Admin')") only passes for Admin or higher.
     */
    @Test
    @WithMockUser(roles = "User")
    void testDeleteUser_NonAdmin_Returns403() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that an unauthenticated call to GET /api/users/me => 401 Unauthorized.
     * Because @PreAuthorize("isAuthenticated()") requires some authentication.
     */
    @Test
    void testGetCurrentUser_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that an Admin tries to update roles (PUT /api/users/{id}/update-roles),
     * but only SuperAdmin can do it => 403 for Admin or any user that's not SuperAdmin.
     */
    @Test
    @WithMockUser(roles = "Admin")
    void testUpdateUserRoles_Admin_Returns403() throws Exception {
        mockMvc.perform(put("/api/users/1/update-roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserTestData.createUpdateRoleRequest())))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that a SuperAdmin can update user roles => 200 OK.
     * The security expression (e.g. "hasRole('SuperAdmin')") should pass for a user with role SuperAdmin.
     * Additionally, if the expression compares the current user's ID to the target ID,
     * we stub securityService.getCurrentUserId() to return 1L, matching the target.
     */
    @Test
    @WithMockUser(roles = "SuperAdmin")
    void testUpdateUserRoles_SuperAdmin_Returns200() throws Exception {
        UserDTO updatedUser = UserTestData.createTestUserDTO();
        updatedUser.setRoles(Set.of("User"));

        when(userService.updateUserRoles(eq(1L), anySet())).thenReturn(updatedUser);
        when(securityService.getCurrentUserId()).thenReturn(1L);

        mockMvc.perform(put("/api/users/{id}/update-roles", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserTestData.createUpdateRoleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.roles[0]").value("User"));
    }

    /**
     * Tests that a user is forbidden from updating someone else's email:
     * @PreAuthorize("@securityService.getCurrentUserId() == #id")
     * If getCurrentUserId() != id => 403.
     */
    @Test
    @WithMockUser(roles = "User", username = "randomUser")
    void testUpdateEmail_AnotherUsersId_Returns403() throws Exception {
        when(securityService.getCurrentUserId()).thenReturn(123L);

        mockMvc.perform(put("/api/users/{id}/update-email", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserTestData.createUpdateUserRequest())))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that a user can update their own email => 200.
     * If securityService.getCurrentUserId() == #id => the check passes.
     */
    @Test
    @WithMockUser(roles = "User", username = "ownerUser")
    void testUpdateEmail_OwnId_Returns200() throws Exception {
        long ownUserId = 123L;
        UserDTO updatedUser = UserTestData.createTestUserDTO();
        updatedUser.setId(ownUserId);
        updatedUser.setEmail("mynewemail@example.com");

        when(securityService.getCurrentUserId()).thenReturn(ownUserId);
        when(userService.updateUserEmail(eq(ownUserId), any())).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/{id}/update-email", ownUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserTestData.createUpdateUserRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123L))
                .andExpect(jsonPath("$.email").value("mynewemail@example.com"));
    }

    /**
     * Tests that a normal user is not allowed to reset another user's password => 403,
     * because it's restricted to Admin. (See @PreAuthorize("hasRole('Admin')") in the controller.)
     */
    @Test
    @WithMockUser(roles = "User")
    void testResetUserPassword_NonAdmin_Returns403() throws Exception {
        mockMvc.perform(put("/api/users/{id}/reset-password", 45L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserTestData.createResetPasswordRequest())))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that an Admin can reset another user's password => 200 OK.
     */
    @Test
    @WithMockUser(roles = "Admin")
    void testResetUserPassword_Admin_Returns200() throws Exception {
        doNothing().when(userService).resetUserPassword(eq(45L), eq("newSecurePassword123"));

        mockMvc.perform(put("/api/users/{id}/reset-password", 45L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserTestData.createResetPasswordRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successfully."));
    }
}