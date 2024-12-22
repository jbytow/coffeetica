package com.example.coffeetica.user.controllers.services.impl;

import com.example.coffeetica.user.models.RoleEntity;
import com.example.coffeetica.user.models.UserDTO;
import com.example.coffeetica.user.models.UserEntity;
import com.example.coffeetica.user.repositories.RoleRepository;
import com.example.coffeetica.user.repositories.UserRepository;
import com.example.coffeetica.user.services.impl.UserServiceImpl;
import com.example.coffeetica.user.util.UserTestData;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl underTest;

    @Test
    public void testRegisterNewUserAccount() throws Exception {
        UserDTO userDTO = UserTestData.createTestUserDTO();
        UserEntity userEntity = UserTestData.createTestUserEntity();
        String encryptedPassword = "encryptedPassword";
        RoleEntity userRoleEntity = UserTestData.createRoleEntity("USER");
        RoleEntity adminRoleEntity = UserTestData.createRoleEntity("ADMIN");

        // Mocking the behavior of ModelMapper and other components
        doReturn(userEntity).when(modelMapper).map(ArgumentMatchers.any(UserDTO.class), ArgumentMatchers.eq(UserEntity.class));
        doReturn(encryptedPassword).when(passwordEncoder).encode(userEntity.getPassword());
        doReturn(userEntity).when(userRepository).save(userEntity);
        doReturn(userDTO).when(modelMapper).map(ArgumentMatchers.any(UserEntity.class), ArgumentMatchers.eq(UserDTO.class));
        doReturn(Optional.of(userRoleEntity)).when(roleRepository).findByName("USER"); // Mock RoleRepository for USER role
        doReturn(Optional.of(adminRoleEntity)).when(roleRepository).findByName("ADMIN"); // Mock RoleRepository for ADMIN role

        // Action
        UserDTO result = underTest.registerNewUserAccount(userDTO);

        // Assertions
        assertEquals(userDTO, result);
        verify(userRepository).save(userEntity);
        verify(modelMapper).map(userEntity, UserDTO.class);
        verify(passwordEncoder).encode(userEntity.getPassword());
        verify(roleRepository).findByName("USER"); // Verify RoleRepository call for USER role
        verify(roleRepository).findByName("ADMIN"); // Verify RoleRepository call for ADMIN role
    }

    @Test
    public void testDeleteUser() {
        Long userId = 1L;

        // Action
        underTest.deleteUser(userId);

        // Assertions
        verify(userRepository).deleteById(userId);
    }

    @Test
    public void testLoadUserByUsername() {
        String username = "testUser";
        UserEntity userEntity = UserTestData.createTestUserEntity();
        UserDetails expectedUserDetails = org.springframework.security.core.userdetails.User
                .withUsername(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(userEntity.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                        .collect(Collectors.toList()))
                .build();

        // Mocking the behavior of UserRepository
        doReturn(Optional.of(userEntity)).when(userRepository).findByUsername(username);

        // Action
        UserDetails actualUserDetails = underTest.loadUserByUsername(username);

        // Assertions
        assertEquals(expectedUserDetails.getUsername(), actualUserDetails.getUsername());
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void testLoadUserByUsernameNotFound() {
        String username = "nonexistentUser";

        // Mocking the behavior of UserRepository
        doReturn(Optional.empty()).when(userRepository).findByUsername(username);

        // Action
        Exception exception = assertThrows(Exception.class, () -> {
            underTest.loadUserByUsername(username);
        });

        // Assertions
        assertTrue(exception.getMessage().contains("User not found with username: " + username));
    }
}