package com.example.coffeetica.user.services;

import com.example.coffeetica.user.models.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    UserDTO registerNewUserAccount(UserDTO userDTO) throws Exception;
    UserDTO updateUser(UserDTO userDTO) throws Exception;
    void deleteUser(Long userId);
    UserDetails loadUserByUsername(String username);
}

