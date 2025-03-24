package com.example.coffeetica.user.services;

import com.example.coffeetica.coffee.models.CoffeeDetailsDTO;
import com.example.coffeetica.user.models.AdminUpdateUserRequestDTO;
import com.example.coffeetica.user.models.RegisterRequestDTO;
import com.example.coffeetica.user.models.UpdateUserRequestDTO;
import com.example.coffeetica.user.models.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

/**
 * The primary service interface for user management, covering registration,
 * updates, role management, and lookups. This interface also extends
 * {@link UserDetailsService} to integrate with Spring Security authentication.
 * <p>
 * Although Spring Security will invoke {@link #loadUserByUsername(String)},
 * this application primarily uses {@link #loadUserByUsernameOrEmail(String)}
 * to authenticate by either username or email.
 */
public interface UserService extends UserDetailsService {

    /**
     * Retrieves a paginated list of users, optionally filtered by a search term that
     * matches username or email. Results can be sorted by a specified field in ascending
     * or descending order.
     *
     * @param search    optional search string to match against username or email
     * @param page      the page index (0-based)
     * @param size      the page size (number of users per page)
     * @param sortBy    the field to sort by (e.g., "username" or "id")
     * @param direction the sort direction: "asc" or "desc"
     * @return a page of {@link UserDTO} objects matching the criteria
     */
    Page<UserDTO> findAllUsers(String search, int page, int size, String sortBy, String direction);

    /**
     * Registers a new user account, encoding the password and assigning
     * a default role of "User" if not otherwise specified.
     *
     * @param request the {@link RegisterRequestDTO} containing user registration data
     * @return a {@link UserDTO} representing the newly registered user
     * @throws Exception if the username or email is already taken, or default role not found
     */
    UserDTO registerNewUserAccount(RegisterRequestDTO request) throws Exception;

    /**
     * Updates the email address of the specified user. Ensures the new email is not
     * already in use by another user.
     *
     * @param userId the ID of the user to update
     * @param request the {@link UpdateUserRequestDTO} containing the new email
     * @return a {@link UserDTO} with the updated user data
     * @throws Exception if the user is not found, or the email is already taken
     */
    UserDTO updateUserEmail(Long userId, UpdateUserRequestDTO request) throws Exception;

    /**
     * Allows an admin to update another user's username and/or email. Prevents editing
     * of Admin/SuperAdmin accounts by an Admin.
     *
     * @param userId the ID of the target user
     * @param request the {@link AdminUpdateUserRequestDTO} containing new username/email
     * @return a {@link UserDTO} representing the updated user
     * @throws Exception if the target user or the updated username/email already exists,
     *                   or if permissions are insufficient
     */
    UserDTO adminUpdateUser(Long userId, AdminUpdateUserRequestDTO request) throws Exception;

    /**
     * Allows a user to change their own password. Verifies the current password
     * and ensures the new password is different. Prevents non-owners from updating.
     *
     * @param userId         the ID of the user changing password
     * @param currentPassword the user's current password
     * @param newPassword     the proposed new password
     * @throws Exception if the user is not found, current password is incorrect,
     *                   or the user lacks permission
     */
    void changeUserPassword(Long userId, String currentPassword, String newPassword) throws Exception;

    /**
     * Allows an admin to reset another user's password, provided the target is not
     * an Admin or SuperAdmin. Encodes the new password before saving.
     *
     * @param userId the ID of the target user
     * @param newPassword the new password to set
     * @throws Exception if the user is not found or the caller lacks permission
     */
    void resetUserPassword(Long userId, String newPassword) throws Exception;

    /**
     * Deletes a user by ID, blocking deletion of Admin/SuperAdmin if the
     * caller is only an Admin.
     *
     * @param userId the ID of the user to delete
     * @throws Exception if the user is not found or permissions are insufficient
     */
    void deleteUser(Long userId) throws Exception;

    /**
     * Finds a user by their unique database ID.
     *
     * @param id the user's ID
     * @return an {@link Optional} containing the user data if found, or empty if not
     */
    Optional<UserDTO> findUserById(Long id);

    /**
     * Locates a "favorite coffee" for the user if they have a 5-star review.
     * Returns the top coffee with rating=5 if one exists.
     *
     * @param userId the ID of the user
     * @return an {@link Optional} of {@link CoffeeDetailsDTO} if found, otherwise empty
     * @throws Exception if the user does not exist
     */
    Optional<CoffeeDetailsDTO> findFavoriteCoffeeOfUser(Long userId) throws Exception;

    /**
     * Updates the roles of a user, replacing their current roles with the provided set.
     *
     * @param userId the user ID
     * @param roles a set of role names (e.g., {"User", "Admin"})
     * @return a {@link UserDTO} representing the updated user
     * @throws Exception if the user is not found, or a specified role does not exist
     */
    UserDTO updateUserRoles(Long userId, Set<String> roles) throws Exception;

    /**
     * Loads a user by username or email, used by the application to handle authentication
     * with either field. This method is not typically called directly, except in advanced
     * authentication flows; Spring Security calls {@link #loadUserByUsername(String)}
     * by default, which delegates to this method.
     *
     * @param identifier a username or an email address
     * @return the {@link org.springframework.security.core.userdetails.UserDetails} object
     * @throws UsernameNotFoundException if no user is found for the given identifier
     */
    UserDetails loadUserByUsernameOrEmail(String identifier) throws UsernameNotFoundException;

    /**
     * For Spring Security integration. Although you may not explicitly call this,
     * Spring Security calls it automatically. Your implementation typically delegates
     * to {@link #loadUserByUsernameOrEmail(String)}.
     *
     * @param username the provided username (or email) from the login attempt
     * @return the {@link UserDetails} if found
     * @throws UsernameNotFoundException if not found
     */
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}