package com.example.hardwarestore.customer;

import lombok.RequiredArgsConstructor;

// This marks the class as a Spring MVC Controller (handles web requests)
import org.springframework.stereotype.Controller;

// For handling validation errors after submitting a form
import org.springframework.validation.BindingResult;

// For binding form data to an object automatically
import org.springframework.web.bind.annotation.ModelAttribute;

// For handling HTTP POST requests
import org.springframework.web.bind.annotation.PostMapping;

// For mapping the base URL for this controller
import org.springframework.web.bind.annotation.RequestMapping;

// For using Bean Validation annotations (like @Valid)
import jakarta.validation.Valid;

@Controller
// This controller will handle requests starting with this URL path:
@RequestMapping("api/v1/customers")
// Lombok: automatically creates a constructor for any final fields
@RequiredArgsConstructor
public class CustomerController {

    // A reference to the CustomerService, injected automatically
    private final CustomerService customerService;

    // Handles POST requests to "/api/v1/customers/register"
    @PostMapping("/register")
    public String createCustomer(
            // Binds form data to a Customer object and validates it
            @ModelAttribute("customer") @Valid Customer customer,
            // Holds validation results
            BindingResult bindingResult) {

        // If there are validation errors, stay on the registration page
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Otherwise, save the customer using the service
        customerService.createCustomer(customer);

        // After successful registration, redirect to the profile page
        return "redirect:/profile";
    }
}