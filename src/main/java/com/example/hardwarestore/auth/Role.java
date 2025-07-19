package com.example.hardwarestore.auth;

import com.example.hardwarestore.security.UserAccount;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name")
    private String roleName;

    // One role can have many authorities (permissions)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "role", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH
    })
    private Set<Authority> authorities = new HashSet<>();

    // Each role belongs to one user account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    @JsonIgnore
    private UserAccount userAccount;

    public Role(String roleName) {
        this.roleName = roleName;
    }

    // Add a set of permissions to this role
    public Role addAuthorities(Set<Authority> newAuthorities) {
        this.authorities.addAll(newAuthorities);
        for (Authority authority : newAuthorities) {
            if (authority != null) {
                authority.setRole(this);
            }
        }
        return this;
    }

    // Return authorities in Spring Security format
    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> grantedAuthorities = this.authorities
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toSet());

        // Also include "ROLE_ADMIN" or "ROLE_USER" etc.
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.roleName));

        return grantedAuthorities;
    }
}

