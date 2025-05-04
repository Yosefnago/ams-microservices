package com.ams.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * {@code SecurityConfig} is the primary security configuration class for the application.
 *
 * <p>
 * It defines the {@link SecurityFilterChain} bean that customizes the HTTP security settings
 * such as disabling CSRF protection and allowing unrestricted access to all endpoints.
 * </p>
 *
 * <p>
 * Additionally, it provides a {@link PasswordEncoder} bean using the {@link BCryptPasswordEncoder}
 * algorithm, which is commonly used for encoding and verifying user passwords.
 * </p>
 *
 * <p>
 * This configuration is intended for use in development or gateway-service scenarios
 * where the application handles JWT or external authentication.
 * </p>
 *
 * @author Yosef Nago
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain.
     *
     * <p>
     * This implementation disables CSRF, permits all requests without authentication,
     * and disables HTTP Basic authentication.
     * </p>
     *
     * @param http the {@link HttpSecurity} object used to customize web security
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs while configuring the security settings
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()

                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .build();
    }
    /**
     * Provides a {@link PasswordEncoder} bean that uses the BCrypt hashing algorithm.
     *
     * @return a new instance of {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
