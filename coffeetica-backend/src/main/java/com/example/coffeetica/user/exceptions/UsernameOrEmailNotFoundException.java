package com.example.coffeetica.user.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Thrown when a user cannot be found by their username or email
 * during the authentication process.
 *
 * Extends {@link UsernameNotFoundException} to support Spring Security workflows.
 */
public class UsernameOrEmailNotFoundException extends UsernameNotFoundException {

    /**
     * Constructs a new UsernameOrEmailNotFoundException with the specified message.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public UsernameOrEmailNotFoundException(String message) {
        super(message);
    }
}
