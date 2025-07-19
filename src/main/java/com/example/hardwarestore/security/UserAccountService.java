package com.example.hardwarestore.security;

import lombok.RequiredArgsConstructor;

import java.util.Set;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.hardwarestore.customer.Customer;
import com.example.hardwarestore.auth.*;
import java.util.Collections;
import java.util.Optional;

/**
 * This service handles:
 * - Creating a new UserAccount for a Customer
 * - Connecting your UserAccount with Spring Security by implementing UserDetailsService
 */
@Service
@RequiredArgsConstructor
public class UserAccountService implements UserDetailsService, AccountService {


    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    /**
     * Creates a new UserAccount for a given Customer.
     * It encodes the password, assigns default roles and permissions,
     * and saves the account if it doesn't exist yet.
     */
    @Override
    public UserAccount createUserAccount(Customer customer) {
        // Create a new UserAccount with the customer's email and encoded password
        UserAccount userAccount = new UserAccount(
                customer.getEmail(),
                passwordEncoder.encode(customer.getPassword()))
                .addRoles(Collections.singleton(createNewUserRole()))
                .addCustomer(customer);

        // Check if an account with the same username already exists
        Optional<UserAccount> userAccountByUsername = userAccountRepository
                .findUserAccountByUsername(userAccount.getUsername());

        // If not found, save the new account
        if (userAccountByUsername.isEmpty()) {
            return userAccountRepository.save(userAccount);
        }

        // If found, return the existing account
        return userAccountByUsername
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User account not found"));
    }

    /**
     * Creates a default "USER" role with basic permissions for new users.
     * For example: "customer:read" and "customer:write".
     */
    private Role createNewUserRole() {
        Role role = new Role("USER")
                .addAuthorities(Set.of(
                        new Authority("customer:read"),
                        new Authority("customer:write")
                ));

        // Save the role to the database and return it
        return roleRepository.save(role);
    }

    /**
     * Spring Security calls this method to load user details by username during login.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Look up the user account by username (email)
        UserAccount userAccount = userAccountRepository
                .findUserAccountByUsername(username)
                .stream()
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        // Wrap the UserAccount in a UserDetails object
        return new UserAccountDetails(userAccount);
    }
}
