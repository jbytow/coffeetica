package com.example.coffeetica.user.services.impl;

import com.example.coffeetica.coffee.models.CoffeeDetailsDTO;
import com.example.coffeetica.coffee.models.ReviewEntity;
import com.example.coffeetica.coffee.repositories.ReviewRepository;
import com.example.coffeetica.coffee.services.CoffeeService;
import com.example.coffeetica.user.models.*;
import com.example.coffeetica.user.repositories.RoleRepository;
import com.example.coffeetica.user.repositories.UserRepository;
import com.example.coffeetica.user.security.SecurityService;
import com.example.coffeetica.user.services.UserService;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of {@link UserService}, providing registration,
 * user lookups, password changes, and role management.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final CoffeeService coffeeService;
    private final ReviewRepository reviewRepository;
    private final SecurityService securityService;

    /**
     * Constructs a new {@link UserServiceImpl} with required dependencies.
     *
     * @param userRepository repository for user entities
     * @param roleRepository repository for role entities
     * @param passwordEncoder password encoder for user credentials
     * @param modelMapper model mapper for DTO conversions
     * @param coffeeService coffee service for coffee details
     * @param reviewRepository repository for review entities
     * @param securityService security service for current user checks
     */
    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            ModelMapper modelMapper,
            CoffeeService coffeeService,
            ReviewRepository reviewRepository,
            SecurityService securityService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.coffeeService = coffeeService;
        this.reviewRepository = reviewRepository;
        this.securityService = securityService;
    }

    @Override
    public Page<UserDTO> findAllUsers(String search, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserEntity> userEntities = userRepository.findBySearch(search, pageable);

        return userEntities.map(entity -> {
            UserDTO dto = new UserDTO();
            dto.setId(entity.getId());
            dto.setUsername(entity.getUsername());
            dto.setEmail(entity.getEmail());
            dto.setRoles(entity.getRoles().stream()
                    .map(RoleEntity::getName)
                    .collect(Collectors.toSet()));
            // If you want the user's review IDs:
            Set<Long> reviewIds = entity.getReviews().stream()
                    .map(ReviewEntity::getId)
                    .collect(Collectors.toSet());
            dto.setReviewIds(reviewIds);
            return dto;
        });
    }

    @Override
    public UserDTO registerNewUserAccount(RegisterRequestDTO request) throws Exception {
        logger.info("Registering new user {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new Exception("There is an account with that username: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new Exception("There is an account with that email address: " + request.getEmail());
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Assign default "User" role
        RoleEntity userRole = roleRepository.findByName("User")
                .orElseThrow(() -> new RuntimeException("Default role 'User' not found"));
        user.setRoles(Set.of(userRole));

        UserEntity savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public UserDTO updateUserEmail(Long userId, UpdateUserRequestDTO request) throws Exception {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));

        Optional<UserEntity> existingUserWithEmail = userRepository.findByEmail(request.getEmail());
        if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getId().equals(userId)) {
            throw new Exception("This email is already in use by another account.");
        }

        user.setEmail(request.getEmail());
        UserEntity updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    @Override
    public UserDTO adminUpdateUser(Long userId, AdminUpdateUserRequestDTO request) throws Exception {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));

        Long currentUserId = securityService.getCurrentUserId();
        UserEntity currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new Exception("Current user not found"));

        boolean isCurrentUserAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("Admin"));
        boolean isTargetUserProtected = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("Admin") || role.getName().equals("SuperAdmin"));

        if (isCurrentUserAdmin && isTargetUserProtected) {
            throw new IllegalAccessException("You do not have permission to edit an Admin or SuperAdmin.");
        }

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            Optional<UserEntity> existingUserWithUsername = userRepository.findByUsername(request.getUsername());
            if (existingUserWithUsername.isPresent()) {
                throw new Exception("This username is already taken.");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            Optional<UserEntity> existingUserWithEmail = userRepository.findByEmail(request.getEmail());
            if (existingUserWithEmail.isPresent()) {
                throw new Exception("This email is already in use by another account.");
            }
            user.setEmail(request.getEmail());
        }

        UserEntity updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    @Override
    public void changeUserPassword(Long userId, String currentPassword, String newPassword) throws Exception {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));

        Long currentUserId = securityService.getCurrentUserId();
        if (!userId.equals(currentUserId)) {
            throw new IllegalAccessException("You are not allowed to change this password.");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as the old password.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void resetUserPassword(Long userId, String newPassword) throws Exception {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));

        Long currentUserId = securityService.getCurrentUserId();
        UserEntity currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new Exception("Current user not found"));

        boolean isCurrentUserAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("Admin"));
        boolean isTargetUserProtected = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("Admin") || role.getName().equals("SuperAdmin"));

        if (isCurrentUserAdmin && isTargetUserProtected) {
            throw new IllegalAccessException("You do not have permission to reset the password of an Admin or SuperAdmin.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Admin reset password for user {}", userId);
    }

    @Override
    public void deleteUser(Long userId) throws Exception {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));

        Long currentUserId = securityService.getCurrentUserId();
        UserEntity currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new Exception("Current user not found"));

        boolean isCurrentUserAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("Admin"));
        boolean isTargetUserProtected = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("Admin") || role.getName().equals("SuperAdmin"));

        if (isCurrentUserAdmin && isTargetUserProtected) {
            throw new IllegalAccessException("You do not have permission to delete an Admin or SuperAdmin.");
        }

        logger.info("Deleting user {}", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public Optional<UserDTO> findUserById(Long id) {
        return userRepository.findById(id).map(entity -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(entity.getId());
            userDTO.setUsername(entity.getUsername());
            userDTO.setEmail(entity.getEmail());
            userDTO.setRoles(entity.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toSet()));

            // example if you want to map review IDs
            Set<Long> reviewIds = entity.getReviews().stream()
                    .map(review -> review.getId())
                    .collect(Collectors.toSet());
            userDTO.setReviewIds(reviewIds);

            return userDTO;
        });
    }

    @Override
    public Optional<CoffeeDetailsDTO> findFavoriteCoffeeOfUser(Long userId) throws Exception {
        if (!userRepository.existsById(userId)) {
            throw new Exception("User not found with id: " + userId);
        }

        Page<ReviewEntity> topReviews = reviewRepository
                .findReviewsWithRatingFiveByUserId(userId, PageRequest.of(0, 1));

        if (topReviews.isEmpty()) {
            return Optional.empty();
        }

        ReviewEntity review = topReviews.getContent().get(0);
        return coffeeService.findCoffeeDetails(review.getCoffee().getId());
    }

    @Override
    public UserDTO updateUserRoles(Long userId, Set<String> roles) throws Exception {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));

        Set<RoleEntity> roleEntities = new HashSet<>();
        for (String roleName : roles) {
            RoleEntity role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roleEntities.add(role);
        }

        user.setRoles(roleEntities);
        UserEntity updated = userRepository.save(user);
        return modelMapper.map(updated, UserDTO.class);
    }

    /**
     * Loads a user by their username or email (default for Spring Security).
     *
     * @param identifier the username or email
     * @return a fully authenticated UserDetails object
     * @throws UsernameNotFoundException if no user is found
     */
    @Override
    public UserDetails loadUserByUsernameOrEmail(String identifier) {
        logger.info("Loading user by identifier: {}", identifier);
        UserEntity user = userRepository.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + identifier));

        return toSpringSecurityUser(user);
    }

    /**
     * Spring Security calls this method by default.
     * We delegate to loadUserByUsernameOrEmail.
     *
     * @param username the username
     * @return the user details for Spring Security
     * @throws UsernameNotFoundException if not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByUsernameOrEmail(username);
    }

    /**
     * Converts a user entity to Spring Security's User object.
     */
    private UserDetails toSpringSecurityUser(UserEntity user) {
        String[] roles = user.getRoles().stream()
                .map(RoleEntity::getName)
                .map(name -> "ROLE_" + name)
                .toArray(String[]::new);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(roles)
                .build();
    }
}