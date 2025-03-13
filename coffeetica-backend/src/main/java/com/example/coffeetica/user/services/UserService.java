package com.example.coffeetica.user.services;

import com.example.coffeetica.coffee.models.CoffeeDetailsDTO;
import com.example.coffeetica.user.models.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserService {
    Page<UserDTO> findAllUsers(String search, int page, int size, String sortBy, String direction);
    UserDTO registerNewUserAccount(UserDTO userDTO) throws Exception;
    UserDTO updateUser(UserDTO userDTO) throws Exception;
    void changeUserPassword(Long userId, String currentPassword, String newPassword) throws Exception;
    void deleteUser(Long userId);
    UserDetails loadUserByUsernameOrEmail(String identifier);
    UserDetails loadUserByUsername(String username);
    Optional<UserDTO> findUserById(Long id);
    Optional<CoffeeDetailsDTO> findFavoriteCoffeeOfUser(Long userId) throws Exception;

}

