package com.example.coffeetica.config;


import com.example.coffeetica.user.models.RoleEntity;
import com.example.coffeetica.user.models.UserEntity;
import com.example.coffeetica.user.repositories.RoleRepository;
import com.example.coffeetica.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * Initializes default roles and users at application startup.
 */
@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${superadmin.username}")
    private String superAdminUsername;

    @Value("${superadmin.email}")
    private String superAdminEmail;

    @Value("${superadmin.password}")
    private String superAdminPassword;

    @Value("${testadmin.username}")
    private String testAdminUsername;

    @Value("${testadmin.email}")
    private String testAdminEmail;

    @Value("${testadmin.password}")
    private String testAdminPassword;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Executes initialization logic after Spring context is loaded.
     */
    @PostConstruct
    public void init() {
        createRoleIfNotFound("User");
        createRoleIfNotFound("Admin");
        createRoleIfNotFound("SuperAdmin");

        // Create default SuperAdmin
        if (!userRepository.existsByUsername(superAdminUsername)) {
            Set<RoleEntity> allRoles = getRoles("User", "Admin", "SuperAdmin");
            UserEntity superAdmin = new UserEntity();
            superAdmin.setUsername(superAdminUsername);
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setPassword(passwordEncoder.encode(superAdminPassword));
            superAdmin.setRoles(allRoles);
            userRepository.save(superAdmin);
        }

        // Create test Admin user
        if (!userRepository.existsByUsername(testAdminUsername)) {
            Set<RoleEntity> adminRoles = getRoles("User", "Admin");
            UserEntity testAdmin = new UserEntity();
            testAdmin.setUsername(testAdminUsername);
            testAdmin.setEmail(testAdminEmail);
            testAdmin.setPassword(passwordEncoder.encode(testAdminPassword));
            testAdmin.setRoles(adminRoles);
            userRepository.save(testAdmin);
        }
    }

    /**
     * Creates a role if it does not exist in the database.
     *
     * @param name the name of the role
     */
    private void createRoleIfNotFound(String name) {
        if (!roleRepository.existsByName(name)) {
            RoleEntity role = new RoleEntity();
            role.setName(name);
            roleRepository.save(role);
        }
    }

    /**
     * Retrieves a set of roles based on provided role names.
     *
     * @param roleNames the names of the roles to retrieve
     * @return a set of {@link RoleEntity}
     * @throws RuntimeException if any role is not found
     */
    private Set<RoleEntity> getRoles(String... roleNames) {
        Set<RoleEntity> roles = new HashSet<>();
        for (String roleName : roleNames) {
            RoleEntity role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roles.add(role);
        }
        return roles;
    }
}