package com.example.hardwarestore.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.hardwarestore.auth.Role;
import com.example.hardwarestore.customer.Customer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user account in the system.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserAccount {

    @Id
    @GeneratedValue
    private Long userAccountId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    /**
     * A user can have multiple roles.
     * The roles are eagerly loaded and automatically managed.
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "userAccount", cascade = {
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.PERSIST
    })
    private Set<Role> roles = new HashSet<>();

    /**
     * Each user account is linked to exactly one customer.
     * This association is eagerly loaded and changes cascade.
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = {
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.PERSIST
    })
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    /**
     * Flags to indicate the status of the account.
     */
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;

    /**
     * Constructor to quickly create a user with default status flags.
     */
    public UserAccount(String username, String password) {
        this.username = username;
        this.password = password;
        this.isAccountNonLocked = true;
        this.isAccountNonExpired = true;
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;
    }

    /**
     * Add roles to this account and link them back to this user.
     */
    public UserAccount addRoles(Set<Role> newRoles) {
        this.roles.addAll(newRoles);
        for (Role role : newRoles) {
            if (role != null) {
                role.setUserAccount(this);
            }
        }
        return this;
    }

    /**
     * Link a customer to this account and set the back-reference.
     */
    public UserAccount addCustomer(Customer customer) {
        if (customer != null) {
            this.customer = customer;
            customer.setUserAccount(this);
        }
        return this;
    }
}

