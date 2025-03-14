package com.example.coffeetica.user.services;

import com.example.coffeetica.coffee.models.CoffeeDetailsDTO;
import com.example.coffeetica.user.models.AdminUpdateUserRequestDTO;
import com.example.coffeetica.user.models.RegisterRequestDTO;
import com.example.coffeetica.user.models.UpdateUserRequestDTO;
import com.example.coffeetica.user.models.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.Set;

public interface UserService {
    Page<UserDTO> findAllUsers(String search, int page, int size, String sortBy, String direction);
    UserDTO registerNewUserAccount(RegisterRequestDTO request) throws Exception;
    UserDTO updateUserEmail(Long userId, UpdateUserRequestDTO request) throws Exception;
    UserDTO adminUpdateUser(Long userId, AdminUpdateUserRequestDTO request) throws Exception;
    void changeUserPassword(Long userId, String currentPassword, String newPassword) throws Exception;
    void resetUserPassword(Long userId, String newPassword) throws Exception;
    void deleteUser(Long userId) throws Exception;
    UserDetails loadUserByUsernameOrEmail(String identifier);
    UserDetails loadUserByUsername(String username);
    Optional<UserDTO> findUserById(Long id);
    Optional<CoffeeDetailsDTO> findFavoriteCoffeeOfUser(Long userId) throws Exception;

    UserDTO updateUserRoles(Long userId, Set<String> roles) throws Exception;

}

