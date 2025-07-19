package com.example.hardwarestore.customer;
// Lombok: generates a constructor for all final fields
import lombok.RequiredArgsConstructor;

// Marks this class as a Spring service (business logic component)
import org.springframework.stereotype.Service;
import com.example.hardwarestore.security.UserAccountService;


@Service
// Lombok: auto-generates a constructor for required dependencies
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    // Repository to save and retrieve customers from the database
    private final CustomerRepository customerRepository;

    // Service to create the related UserAccount when a customer is created
    private final UserAccountService userAccountService;

    @Override
    public Customer createCustomer(Customer customer) {
        // Save the customer data to the database
        Customer theCustomer = customerRepository.save(customer);

        // Create a user account for this customer
        userAccountService.createUserAccount(theCustomer);

        // Return the saved customer
        return theCustomer;
    }
}
