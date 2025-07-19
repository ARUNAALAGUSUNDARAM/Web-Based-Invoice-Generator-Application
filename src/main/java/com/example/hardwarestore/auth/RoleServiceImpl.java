package com.example.hardwarestore.auth; // ✅ Update package to your project name

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * Service implementation for managing Roles.
 * This creates and fetches the USER role with its authorities (permissions).
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    // Automatically inject the RoleRepository to access the database
    private final RoleRepository roleRepository;

    /**
     * Get the USER role.
     * If it does not exist, create it with default authorities and save it.
     *
     * @return Role object for USER
     */
    @Override
    public Role getRoleUSER() {
        // Create a new Role named "USER" with default authorities
        Role role = new Role("USER")
                .addAuthorities(Set.of(
                        new Authority("customer:read"),   // permission to read customer data
                        new Authority("customer:write")   // permission to write customer data
                ));

        // Check if this role already exists in the database
        Optional<Role> theRole = roleRepository.findRoleByRoleName(role.getRoleName());

        // If it does not exist, save the new one
        if (theRole.isEmpty()) {
            return roleRepository.save(role);
        }

        // If it exists, return it
        return theRole.stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }
}
