package com.example.coffeetica.user.util;


import com.example.coffeetica.user.models.RoleEntity;
import com.example.coffeetica.user.models.UserDTO;
import com.example.coffeetica.user.models.UserEntity;

import java.util.HashSet;
import java.util.Set;

public final class TestData {

    private TestData() {
    }

    public static UserDTO createTestUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testUser");
        userDTO.setPassword("encryptedPassword");
        userDTO.setRoles(Set.of("USER", "ADMIN"));  // Assume roles are still transmitted as strings in DTOs
        return userDTO;
    }

    public static UserEntity createTestUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testUser");
        userEntity.setPassword("encryptedPassword"); // Normally you would store encrypted passwords
        userEntity.setRoles(createRolesEntitySet());
        return userEntity;
    }

    private static Set<RoleEntity> createRolesEntitySet() {
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(createRoleEntity("USER"));
        roles.add(createRoleEntity("ADMIN"));
        return roles;
    }

    private static RoleEntity createRoleEntity(String roleName) {
        RoleEntity role = new RoleEntity();
        role.setName(roleName);
        return role;
    }

}
