package com.example.coffeetica.user.controllers.services.impl;

import com.example.coffeetica.user.models.UserDTO;
import com.example.coffeetica.user.models.UserEntity;
import com.example.coffeetica.user.repositories.UserRepository;
import com.example.coffeetica.user.services.impl.UserServiceImpl;
import com.example.coffeetica.user.util.TestData;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @InjectMocks
    private UserServiceImpl underTest;

    @Test
    public void testRegisterNewUserAccount() throws Exception {
        UserDTO userDTO = TestData.createTestUserDTO();
        UserEntity userEntity = TestData.createTestUserEntity();
        String encryptedPassword = "encryptedPassword";

        when(modelMapper.map(userDTO, UserEntity.class)).thenReturn(userEntity);
        when(passwordEncoder.encode(userEntity.getPassword())).thenReturn(encryptedPassword);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(modelMapper.map(userEntity, UserDTO.class)).thenReturn(userDTO);

        UserDTO result = underTest.registerNewUserAccount(userDTO);

        assertEquals(userDTO, result);
        verify(userRepository).save(userEntity);
        verify(modelMapper).map(userEntity, UserDTO.class);
        verify(passwordEncoder).encode(userEntity.getPassword());
    }


    @Test
    public void testUpdateUser() throws Exception {
        UserDTO userDTO = TestData.createTestUserDTO();
        UserEntity userEntity = TestData.createTestUserEntity();

        // Simulate finding by ID
        when(userRepository.findById(userDTO.getId())).thenReturn(Optional.of(userEntity));

        // Simulate encoding the password
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn(encodedPassword);

        // Update the password of the userEntity
        userEntity.setPassword(encodedPassword);

        // Simulate saving the entity
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        // Return the updated user DTO when mapping back to DTO
        when(modelMapper.map(userEntity, UserDTO.class)).thenReturn(userDTO);

        // Execute the update
        UserDTO result = underTest.updateUser(userDTO);

        // Assertions
        assertEquals(userDTO.getUsername(), result.getUsername());
        assertEquals(userDTO.getId(), result.getId());
        verify(userRepository).save(userEntity);
        verify(passwordEncoder).encode(userDTO.getPassword());
    }


    @Test
    public void testDeleteUser() {
        Long userId = 1L;

        underTest.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    public void testLoadUserByUsername() {
        String username = "testUser";
        UserEntity userEntity = TestData.createTestUserEntity();
        UserDetails expectedUserDetails = org.springframework.security.core.userdetails.User
                .withUsername(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(userEntity.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                        .collect(Collectors.toList()))
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));

        UserDetails actualUserDetails = underTest.loadUserByUsername(username);

        assertEquals(expectedUserDetails.getUsername(), actualUserDetails.getUsername());
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void testLoadUserByUsernameNotFound() {
        String username = "nonexistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            underTest.loadUserByUsername(username);
        });

        assertTrue(exception.getMessage().contains("User not found with username: " + username));
    }
}