package com.example.coffeetica.user.services.impl;


import com.example.coffeetica.coffee.models.CoffeeDetailsDTO;
import com.example.coffeetica.coffee.models.CoffeeEntity;
import com.example.coffeetica.coffee.models.ReviewEntity;
import com.example.coffeetica.coffee.repositories.ReviewRepository;
import com.example.coffeetica.coffee.services.CoffeeService;
import com.example.coffeetica.user.models.RoleEntity;
import com.example.coffeetica.user.models.UserDTO;
import com.example.coffeetica.user.models.UserEntity;
import com.example.coffeetica.user.repositories.RoleRepository;
import com.example.coffeetica.user.repositories.UserRepository;
import com.example.coffeetica.user.services.UserService;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CoffeeService coffeeService;

    @Autowired
    private ReviewRepository reviewRepository;

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
            return dto;
        });
    }

    @Override
    public UserDTO registerNewUserAccount(UserDTO userDTO) throws Exception {
        logger.info("Registering new user {}", userDTO.getUsername());

        // Check if the username or email already exists
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new Exception("There is an account with that username: " + userDTO.getUsername());
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new Exception("There is an account with that email address: " + userDTO.getEmail());
        }

        // Map UserDTO to UserEntity
        UserEntity user = modelMapper.map(userDTO, UserEntity.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Always assign the "User" role
        RoleEntity userRole = roleRepository.findByName("User")
                .orElseThrow(() -> new RuntimeException("Default role 'User' not found"));

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(userRole);

        user.setRoles(roles);

        // Save the user and return as DTO
        UserEntity savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) throws Exception {
        logger.info("Updating user {}", userDTO.getId());
        UserEntity user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new Exception("User not found with id: " + userDTO.getId()));

        // Zanim zmapujesz:
        // Zapisz stare hasło
        String oldPassword = user.getPassword();

        // Zmapuj pola (username, email, itp.)
        modelMapper.map(userDTO, user);

        // Nie zmieniaj hasła w normalnym update:
        user.setPassword(oldPassword);

        // Roles (jeśli chcesz pozwolić userowi aktualizować role, co nie jest zwyczajne,
        // ale w kodzie to robisz). Zachowaj lub nadaj te role
        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            Set<RoleEntity> roles = userDTO.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        // Zapisz w bazie
        UserEntity updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    @Override
    public void changeUserPassword(Long userId, String currentPassword, String newPassword) throws Exception {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));

        // Sprawdź, czy aktualne hasło jest prawidłowe (jeżeli użytkownik nie jest Adminem,
        // bo Admin może zmieniać hasło bez znajomości starego)
        // W typowych projektach i tak sprawdzasz, ale up to you:
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new Exception("Current password is incorrect.");
        }

        // Ustaw nowe hasło
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        logger.info("Deleting user {}", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public UserDetails loadUserByUsernameOrEmail(String identifier) {
        logger.info("Loading user by identifier: {}", identifier);
        UserEntity user = userRepository.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + identifier));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> "ROLE_" + role.getName())
                        .toArray(String[]::new))
                .build();
    }

    // Spring Security will call this method by default
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByUsernameOrEmail(username);  // Delegate to the loadUserByUsernameOrEmail method
    }

    public Optional<UserDTO> findUserById(Long id) {
        return userRepository.findById(id).map(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setPassword(user.getPassword());
            userDTO.setEmail(user.getEmail());
            userDTO.setRoles(user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toSet()));
            return userDTO;
        });
    }

    public Optional<CoffeeDetailsDTO> findFavoriteCoffeeOfUser(Long userId) throws Exception {
        // Upewniamy się, że user istnieje – w przeciwnym razie -> Exception/404
        if (!userRepository.existsById(userId)) {
            throw new Exception("User not found with id: " + userId);
        }

        // pobieramy recenzję(je) rating=5.0, sort=desc, limit=1
        Page<ReviewEntity> topReviews = reviewRepository
                .findReviewsWithRatingFiveByUserId(userId, PageRequest.of(0, 1));

        if (topReviews.isEmpty()) {
            return Optional.empty(); // brak recenzji z oceną 5
        }

        ReviewEntity review = topReviews.getContent().get(0);
        // Mamy recenzję -> pobieramy kawę:
        CoffeeEntity coffeeEntity = review.getCoffee();
        // ... i mapujemy do CoffeeDetailsDTO:
        return coffeeService.findCoffeeDetails(coffeeEntity.getId());
    }


}