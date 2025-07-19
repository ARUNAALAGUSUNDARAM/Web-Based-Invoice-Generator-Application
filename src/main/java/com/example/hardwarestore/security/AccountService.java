package com.example.hardwarestore.security;
import com.example.hardwarestore.customer.Customer;
public interface AccountService {
    UserAccount createUserAccount(Customer customer);
}
