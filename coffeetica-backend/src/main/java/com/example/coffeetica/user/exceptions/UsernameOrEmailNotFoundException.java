package com.example.coffeetica.user.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UsernameOrEmailNotFoundException extends UsernameNotFoundException {

    public UsernameOrEmailNotFoundException(String message) {
        super(message);
    }
}
