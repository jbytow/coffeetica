package com.example.coffeetica.user.services.impl;

import com.example.coffeetica.coffee.repositories.ReviewRepository;
import com.example.coffeetica.user.models.*;
import com.example.coffeetica.user.repositories.RoleRepository;
import com.example.coffeetica.user.repositories.UserRepository;
import com.example.coffeetica.user.services.impl.UserServiceImpl;
import com.example.coffeetica.user.util.UserTestData;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.coffeetica.coffee.services.CoffeeService;

import com.example.coffeetica.user.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.security.core.userdetails.*;




/**
 * Unit tests for {@link UserServiceImpl} focusing on user registration,
 * updates, role management, etc. Accepts a {@link RegisterRequestDTO}
 * for new user accounts, returning a {@link UserDTO}.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CoffeeService coffeeService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private UserServiceImpl underTest;

    private RegisterRequestDTO sampleRegisterRequest;
    private UserDTO sampleUserDTO;
    private UserEntity sampleUserEntity;
    private RoleEntity userRoleEntity;
    private RoleEntity adminRoleEntity;

    @BeforeEach
    void setUp() {
        // Input object for registering
        sampleRegisterRequest = UserTestData.createTestRegisterRequest(); // has raw password

        // Typical result after registration
        sampleUserDTO = UserTestData.createTestUserDTO(); // no password field in final DTO

        // Typical user entity in DB
        sampleUserEntity = UserTestData.createTestUserEntity(); // "encryptedPassword"

        userRoleEntity = new RoleEntity();
        userRoleEntity.setName("User");

        adminRoleEntity = new RoleEntity();
        adminRoleEntity.setName("Admin");
    }

    /**
     * Tests registering a new user with a RegisterRequestDTO,
     * verifying uniqueness checks, password encryption, default role assignment,
     * and final mapping to UserDTO.
     */
    @Test
    void testRegisterNewUserAccount() throws Exception {
        // Suppose the input request has 'rawPassword'
        when(userRepository.existsByUsername(sampleRegisterRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(sampleRegisterRequest.getEmail())).thenReturn(false);

        when(roleRepository.findByName("User")).thenReturn(Optional.of(userRoleEntity));

        // Encryption
        when(passwordEncoder.encode("rawPassword")).thenReturn("encryptedPassword");

        // Saving user
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity u = inv.getArgument(0);
            // check if the password is set
            assertEquals("encryptedPassword", u.getPassword());
            return u;
        });

        // After saving, we map entity -> userDTO
        when(modelMapper.map(any(UserEntity.class), eq(UserDTO.class))).thenReturn(sampleUserDTO);

        // Action
        UserDTO result = underTest.registerNewUserAccount(sampleRegisterRequest);

        // Verification
        verify(userRepository).existsByUsername("testUser");
        verify(userRepository).existsByEmail("testuser@example.com");
        verify(roleRepository).findByName("User");
        verify(passwordEncoder).encode("rawPassword");
        verify(userRepository).save(any(UserEntity.class));
        verify(modelMapper).map(any(UserEntity.class), eq(UserDTO.class));

        assertEquals(sampleUserDTO, result);
        // sampleUserDTO has no .getPassword() by design
    }

    /**
     * Tests loadUserByUsername(...) returns correct UserDetails if the user is found.
     */
    @Test
    void testLoadUserByUsername() {
        String username = "testUser";
        sampleUserEntity.setUsername(username);

        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.of(sampleUserEntity));

        UserDetails userDetails = underTest.loadUserByUsername(username);

        assertEquals(username, userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_Admin")));
    }

    /**
     * Tests loadUserByUsername(...) throws UsernameNotFoundException if user not found.
     */
    @Test
    void testLoadUserByUsername_NotFound() {
        String username = "nonexistentUser";
        when(userRepository.findByUsernameOrEmail("nonexistentUser")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> underTest.loadUserByUsername(username));
        assertTrue(ex.getMessage().contains("User not found with username or email: " + username));
    }

    /**
     * Tests deleteUser calls userRepository.deleteById if the user is found
     * and current user is allowed to delete.
     */
    @Test
    void testDeleteUser() throws Exception {
        Long userId = 1L;
        UserEntity userEntity = UserTestData.createTestUserEntity();
        userEntity.setRoles(Set.of(userRoleEntity));
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        // Suppose current user is an admin with ID=2
        when(securityService.getCurrentUserId()).thenReturn(2L);

        UserEntity adminEntity = new UserEntity();
        adminEntity.setId(2L);
        adminEntity.setRoles(Set.of(adminRoleEntity)); // e.g. "Admin"
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminEntity));

        underTest.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    /**
     * Tests deleteUser throws an exception if the user is not found.
     */
    @Test
    void testDeleteUser_UserNotFound() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> underTest.deleteUser(userId));
        assertTrue(ex.getMessage().contains("User not found with id: 99"));
    }

    /**
     * Tests adminUpdateUser success scenario: found user, current user is Admin,
     * updated username/email, etc.
     */
    @Test
    void testAdminUpdateUser_Success() throws Exception {
        Long userId = 1L;
        AdminUpdateUserRequestDTO request = new AdminUpdateUserRequestDTO();
        request.setUsername("updatedUser");
        request.setEmail("updateduser@example.com");

        UserEntity targetUser = UserTestData.createTestUserEntity(); // existing user
        targetUser.setRoles(Set.of(userRoleEntity));
        when(userRepository.findById(userId)).thenReturn(Optional.of(targetUser));

        // Current user is admin
        when(securityService.getCurrentUserId()).thenReturn(2L);
        UserEntity currentAdmin = UserTestData.createAnotherUserEntity();
        currentAdmin.setRoles(Set.of(adminRoleEntity)); // "Admin"
        when(userRepository.findById(2L)).thenReturn(Optional.of(currentAdmin));

        when(userRepository.findByUsername("updatedUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("updateduser@example.com")).thenReturn(Optional.empty());

        // Save
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // final mapping
        UserDTO finalDTO = UserTestData.createTestUserDTO();
        finalDTO.setUsername("updatedUser");
        finalDTO.setEmail("updateduser@example.com");
        when(modelMapper.map(any(UserEntity.class), eq(UserDTO.class))).thenReturn(finalDTO);

        UserDTO result = underTest.adminUpdateUser(userId, request);

        verify(userRepository).save(any(UserEntity.class));
        assertEquals("updatedUser", result.getUsername());
        assertEquals("updateduser@example.com", result.getEmail());
    }

    /**
     * Tests updateUserEmail success scenario: user found, new email not in use,
     * we update and save, returning a mapped UserDTO.
     */
    @Test
    void testUpdateUserEmail_Success() throws Exception {
        Long userId = 1L;
        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setEmail("newemail@example.com");

        UserEntity existingUser = UserTestData.createTestUserEntity();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("newemail@example.com")).thenReturn(Optional.empty());

        // Save
        UserEntity updatedUser = existingUser;
        updatedUser.setEmail("newemail@example.com");
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedUser);

        // final map
        UserDTO finalDTO = UserTestData.createTestUserDTO();
        finalDTO.setEmail("newemail@example.com");
        when(modelMapper.map(updatedUser, UserDTO.class)).thenReturn(finalDTO);

        UserDTO result = underTest.updateUserEmail(userId, request);
        verify(userRepository).save(any(UserEntity.class));
        assertEquals("newemail@example.com", result.getEmail());
    }

    /**
     * Tests resetUserPassword success scenario: user found,
     * current user is Admin, target user is not Admin/SuperAdmin,
     * new password is encoded, etc.
     */
    @Test
    void testResetUserPassword_Success() throws Exception {
        Long targetUserId = 99L;
        UserEntity targetUser = new UserEntity();
        targetUser.setId(99L);
        targetUser.setRoles(Set.of(userRoleEntity)); // "User"
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        // Current user is admin with ID=2
        when(securityService.getCurrentUserId()).thenReturn(2L);
        UserEntity adminUser = new UserEntity();
        adminUser.setId(2L);
        adminUser.setRoles(Set.of(adminRoleEntity));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        when(passwordEncoder.encode("newSecret")).thenReturn("encodedSecret");

        doAnswer(inv -> {
            UserEntity saved = inv.getArgument(0);
            assertEquals("encodedSecret", saved.getPassword());
            return saved;
        }).when(userRepository).save(any(UserEntity.class));

        underTest.resetUserPassword(targetUserId, "newSecret");
        verify(userRepository).save(any(UserEntity.class));
    }

}