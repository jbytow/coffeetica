package com.example.coffeetica.config;


import com.example.coffeetica.user.models.RoleEntity;
import com.example.coffeetica.user.models.UserEntity;
import com.example.coffeetica.user.repositories.RoleRepository;
import com.example.coffeetica.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${superadmin.username:superadmin}")
    private String superAdminUsername;

    @Value("${superadmin.username:superadmin@email.com}")
    private String superAdminEmail;

    @Value("${superadmin.password:superadminpassword}")
    private String superAdminPassword;

    @PostConstruct
    public void init() {
        createRoleIfNotFound("User");
        createRoleIfNotFound("Admin");
        createRoleIfNotFound("SuperAdmin");

        if (!userRepository.existsByUsername(superAdminUsername)) {
            RoleEntity superAdminRole = roleRepository.findByName("SuperAdmin")
                    .orElseThrow(() -> new RuntimeException("Role not found: SuperAdmin"));

            RoleEntity adminRole = roleRepository.findByName("Admin")
                    .orElseThrow(() -> new RuntimeException("Role not found: Admin"));

            Set<RoleEntity> roles = new HashSet<>();
            roles.add(superAdminRole);
            roles.add(adminRole);


            UserEntity superAdmin = new UserEntity();
            superAdmin.setUsername(superAdminUsername);
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setPassword(passwordEncoder.encode(superAdminPassword));
            superAdmin.setRoles(roles);

            userRepository.save(superAdmin);
        }
    }

    private void createRoleIfNotFound(String name) {
        if (!roleRepository.existsByName(name)) {
            RoleEntity role = new RoleEntity();
            role.setName(name);
            roleRepository.save(role);
        }
    }
}
