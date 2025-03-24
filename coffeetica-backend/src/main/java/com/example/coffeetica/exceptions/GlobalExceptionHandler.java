package com.example.coffeetica.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * A global exception handler that catches application-specific exceptions
 * and transforms them into relevant HTTP responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException by returning a 404 Not Found status
     * along with the exception's message as the response body.
     *
     * @param ex the ResourceNotFoundException instance
     * @return a ResponseEntity with status 404 and the exception message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles validation errors thrown by @Valid annotations in DTOs or request bodies.
     *
     * @param ex the MethodArgumentNotValidException instance
     * @return a ResponseEntity with status 400 and a simple error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationError(MethodArgumentNotValidException ex) {
        // You can customize this to return more detailed info about each validation error
        return ResponseEntity.badRequest().body("Validation error: " + ex.getMessage());
    }

    /**
     * Handles AccessDeniedException by returning a 403 Forbidden response.
     *
     * This method intercepts AccessDeniedException thrown when a user is authenticated
     * but does not have the necessary permissions to access a resource.
     * It ensures that the exception is not handled by the generic exception handler,
     * and instead returns a proper HTTP 403 status.
     *
     * @param ex the AccessDeniedException that was thrown
     * @return a ResponseEntity with HTTP status 403 and a message indicating that access is denied
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
    }

    /**
     * Handles any other unhandled exceptions, returning a generic 500 Internal Server Error.
     *
     * @param ex any unchecked exception not already handled by another method
     * @return a generic ResponseEntity with status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        // Log the exception if needed (for debugging or auditing)
        // logger.error("Unhandled exception occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }
}