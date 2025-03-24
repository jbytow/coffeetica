package com.example.coffeetica.user.util;


import com.example.coffeetica.user.models.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class providing test data for user-related entities and DTOs.
 */
public final class UserTestData {

    private UserTestData() {
        // Prevent instantiation
    }

    /**
     * Creates a sample {@link RegisterRequestDTO} with username='testUser',
     * email='testuser@example.com', and password='rawPassword'.
     *
     * @return a fully populated {@link RegisterRequestDTO} for user registration
     */
    public static RegisterRequestDTO createTestRegisterRequest() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("testUser");
        request.setEmail("testuser@example.com");
        request.setPassword("rawPassword"); // raw password
        return request;
    }

    /**
     * Creates a sample {@link UserDTO} – typically the output
     * after registration, with ID=1, roles={User, Admin}, etc.
     *
     * @return a {@link UserDTO}
     */
    public static UserDTO createTestUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testUser");
        // userDTO does NOT store password in your design
        userDTO.setEmail("testuser@example.com");
        userDTO.setRoles(Set.of("User", "Admin"));
        return userDTO;
    }

    /**
     * Creates a sample {@link UserEntity} with ID=1, roles={User, Admin},
     * matching the typical state after registration.
     *
     * @return a {@link UserEntity}
     */
    public static UserEntity createTestUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testUser");
        userEntity.setPassword("encryptedPassword");
        userEntity.setEmail("testuser@example.com");
        userEntity.setRoles(createRolesEntitySet());
        return userEntity;
    }

    /**
     * Creates a set of {@link RoleEntity} containing "User" and "Admin".
     *
     * @return a set of roles
     */
    public static Set<RoleEntity> createRolesEntitySet() {
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(createRoleEntity("User"));
        roles.add(createRoleEntity("Admin"));
        return roles;
    }

    /**
     * Creates a single {@link RoleEntity} for the given role name.
     *
     * @param roleName the name of the role (e.g. "User", "Admin")
     * @return a new {@link RoleEntity}
     */
    public static RoleEntity createRoleEntity(String roleName) {
        RoleEntity role = new RoleEntity();
        role.setName(roleName);
        return role;
    }

    /**
     * Creates a second distinct user for scenarios where we need another user
     * with ID=2, 'anotherUser' username, only "User" role, etc.
     *
     * @return a {@link UserEntity} representing a different user
     */
    public static UserEntity createAnotherUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(2L);
        userEntity.setUsername("anotherUser");
        userEntity.setPassword("anotherEncryptedPass");
        userEntity.setEmail("anotherUser@example.com");
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(createRoleEntity("User"));
        userEntity.setRoles(roles);
        return userEntity;
    }

    /**
     * Creates a sample {@link UpdateUserRequestDTO} with email='updated@example.com'
     */
    public static UpdateUserRequestDTO createUpdateUserRequest() {
        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setEmail("updated@example.com");
        return request;
    }

    /**
     * Creates a sample {@link ResetPasswordRequestDTO} with newPassword='newSecurePassword123'
     */
    public static ResetPasswordRequestDTO createResetPasswordRequest() {
        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO();
        request.setNewPassword("newSecurePassword123");
        return request;
    }

    /**
     * Creates a sample {@link ChangePasswordRequestDTO}
     * with currentPassword='oldPass' and newPassword='newPass'
     */
    public static ChangePasswordRequestDTO createChangePasswordRequest() {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setCurrentPassword("oldPass");
        request.setNewPassword("newPass");
        return request;
    }

    /**
     * Creates a sample {@link UpdateRoleRequestDTO} with roles=["User"]
     */
    public static UpdateRoleRequestDTO createUpdateRoleRequest() {
        UpdateRoleRequestDTO request = new UpdateRoleRequestDTO();
        request.setRoles(Set.of("User"));
        return request;
    }

}