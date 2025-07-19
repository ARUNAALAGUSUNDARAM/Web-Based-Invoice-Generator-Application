package com.example.hardwarestore.customer;
import com.example.hardwarestore.security.UserAccount;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a customer in the system.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customer")

public class Customer {
    @Id
    @GeneratedValue
    private Long customerId;

    @Column(name = "first_name")
    @NotEmpty(message = "First name cannot be empty")
    @Size(min = 2, message = "First name must have at least 2 characters")
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty(message = "Last name cannot be empty")
    @Size(min = 2, message = "Last name must have at least 2 characters")
    private String lastName;

    @Column(name = "email")
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    private String email;

    @Transient // Not stored in the database
    @NotEmpty(message = "Password cannot be empty")
    private String password;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserAccount userAccount;
}

