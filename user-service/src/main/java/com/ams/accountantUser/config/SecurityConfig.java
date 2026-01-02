package com.ams.accountantUser.config;


import com.ams.commonsecurity.utils.JwtUtil;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * {@code SecurityConfig} is the central configuration class for setting up
 * Spring Security in the {@code accountant-user-service}.
 * <p>
 * It defines HTTP security rules and the password encoding strategy used for user authentication.
 * Currently, this configuration allows all incoming requests (i.e., no route protection),
 * which is useful for early development stages or when JWT validation is handled elsewhere (e.g., in the gateway).
 * </p>
 *
 * <p><b>Highlights:</b></p>
 * <ul>
 *     <li>Disables CSRF protection for stateless REST API design</li>
 *     <li>Allows all incoming requests using {@code permitAll()}</li>
 *     <li>Registers a {@link PasswordEncoder} bean using BCrypt for secure password hashing</li>
 * </ul>
 *
 * <p><b>Tip:</b> In a production environment, it's recommended to restrict access to endpoints using {@code .authenticated()} or roles, and to integrate a JWT filter.</p>
 *
 * @author Yosef Nago
 * @see JwtUtil for JWT handling
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public  SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Configures the {@link HttpSecurity} object with basic security settings.
     * <p>
     * - Disables CSRF protection (as REST APIs are usually stateless).<br>
     * - Permits all HTTP requests (to be secured later via Gateway/JWT).
     * </p>
     *
     * @param http the {@link HttpSecurity} object to configure
     * @return the constructed {@link SecurityFilterChain}
     * @throws Exception in case of misconfiguration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login","/auth/register").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    /**
     * Defines a {@link PasswordEncoder} bean using the BCrypt algorithm.
     * <p>
     * BCrypt applies a unique salt internally and is computationally expensive by design,
     * making it effective against brute-force and rainbow table attacks.
     * </p>
     *
     * @return a {@link PasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
