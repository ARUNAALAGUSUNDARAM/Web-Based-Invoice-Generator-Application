package com.example.hardwarestore.auth; // ✅ Update to match your new package name

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for accessing Role data in the database.
 * Provides CRUD operations and custom queries for Role entities.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a Role by its name.
     * Example: findRoleByRoleName("ADMIN") returns the Role with name ADMIN.
     */
    Optional<Role> findRoleByRoleName(String roleName);

}

