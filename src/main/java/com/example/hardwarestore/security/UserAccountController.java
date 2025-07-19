package com.example.hardwarestore.security;

import com.example.hardwarestore.security.UserAccount;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.hardwarestore.auth.Role;
import com.example.hardwarestore.customer.Customer;
import com.example.hardwarestore.product.Product;
import com.example.hardwarestore.product.ProductService;

/**
 * This controller handles user account related requests, like:
 * - showing the home page (register)
 * - showing the login page
 * - showing the profile page (only for authorized users)
 */
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountRepository userAccountRepository;
    private final ProductService productService;

    /**
     * Show the home page with a registration form.
     * URL: /
     */
    @GetMapping
    public String home(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }

    /**
     * Show the login page.
     * URL: /login
     */
    @GetMapping("/login")
    public String signIn() {
        return "login";
    }

    /**
     * Show the logged-in user's profile page.
     * Only users with permission 'customer:read' can access this.
     * URL: /profile
     */
    @PreAuthorize("hasAuthority('customer:read')")
    @GetMapping("/profile")
    public String userProfile(
            Authentication authentication, // info about the logged-in user
            Model model,
            @Param("keyword") String keyword) {

        // Find the UserAccount by the logged-in username
        UserAccount userAccount = userAccountRepository
                .findUserAccountByUsername(authentication.getName())
                .stream()
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));

        // Add customer info to the model
        model.addAttribute("customer", userAccount.getCustomer());

        // Add the user's role to the model
        Set<Role> roles = userAccount.getRoles();
        Iterator<Role> iterator = roles.iterator();
        if (iterator.hasNext()) {
            Role role = iterator.next();
            model.addAttribute("rol", role.getRoleName());
        }

        // Add product list (with optional keyword search)
        List<Product> productList = productService.getAllProducts(keyword);
        model.addAttribute("listProducts", productList);
        model.addAttribute("keyword", keyword);

        return "profile";
    }

}

