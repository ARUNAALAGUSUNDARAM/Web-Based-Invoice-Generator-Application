package com.example.hardwarestore.config; // ✅ Update to your package name

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.hardwarestore.security.UserAccountService;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SpringSecurityConfig {

    // Inject UserAccountService (your custom UserDetailsService)
    private final UserAccountService userAccountService;

    // Inject your password encoder (e.g., BCryptPasswordEncoder)
    private final PasswordEncoder passwordEncoder;

    /**
     * Configure the security filter chain.
     * - Defines which URLs are public.
     * - Defines login and logout behavior.
     *
     * @param http HttpSecurity object to configure.
     * @return SecurityFilterChain
     * @throws Exception if something goes wrong.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String loginUrl = "/login";

        // Disable CSRF protection for simplicity (not recommended for production APIs)
        http.csrf(AbstractHttpConfigurer::disable);

        // Define which requests are allowed without authentication
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",                  // Home page
                                "/register",          // Registration page
                                "/profile**",         // Profile pages
                                "/aggregateProduct**", // Add product page
                                "/newProduct**",      // New product page
                                "/editProduct**"      // Edit product page
                        ).permitAll() // These are public
                        .requestMatchers(HttpMethod.POST, "/api/v1/customers/*").permitAll() // Allow POST to create customers
                        .requestMatchers(HttpMethod.POST, "/products/**").permitAll() // Allow POST for products
                        .anyRequest().authenticated() // Any other request requires login
                )

                // Configure login form
                .formLogin(form -> form
                        .loginPage(loginUrl)             // Custom login page URL
                        .loginProcessingUrl(loginUrl)    // URL to submit login credentials
                        .defaultSuccessUrl("/profile")   // Redirect after successful login
                        .permitAll()                     // Allow everyone to see the login page
                )

                // Configure logout
                .logout(logout -> logout
                        .logoutUrl("/logout")            // URL to perform logout
                        .logoutSuccessUrl(loginUrl)      // Redirect to login page after logout
                        .permitAll()                     // Allow everyone to log out
                );

        return http.build();
    }

    /**
     * Define the authentication provider.
     * Uses the custom UserAccountService and PasswordEncoder.
     *
     * @return DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder); // How to check passwords
        provider.setUserDetailsService(userAccountService); // How to load users
        return provider;
    }

    /**
     * Configure the AuthenticationManager with the custom provider.
     *
     * @param http HttpSecurity to get AuthenticationManagerBuilder.
     * @return AuthenticationManager
     * @throws Exception if something goes wrong.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.authenticationProvider(daoAuthenticationProvider());
        return auth.build();
    }
}

