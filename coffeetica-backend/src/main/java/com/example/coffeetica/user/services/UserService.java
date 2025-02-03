package com.example.coffeetica.user.services;

import com.example.coffeetica.user.models.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserService {
    UserDTO registerNewUserAccount(UserDTO userDTO) throws Exception;
    UserDTO updateUser(UserDTO userDTO) throws Exception;
    void deleteUser(Long userId);
    UserDetails loadUserByUsernameOrEmail(String identifier);
    UserDetails loadUserByUsername(String username);
    Optional<UserDTO> findUserById(Long id);
}

