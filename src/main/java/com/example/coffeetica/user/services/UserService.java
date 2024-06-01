package com.example.coffeetica.user.services;

import com.example.coffeetica.user.models.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    UserEntity registerNewUserAccount(UserEntity user) throws Exception;
    UserEntity updateUser(UserEntity user) throws Exception;
    void deleteUser(Long userId);
    UserDetails loadUserByUsername(String username);
}

