package com.ams.accountantUser.service;


import com.ams.accountantUser.entity.AccountantUser;
import com.ams.accountantUser.reposiroty.AccountantUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
/**
 * CustomUserDetailsService is an implementation of {@link UserDetailsService}
 * used by Spring Security to load user-specific data during authentication.
 *
 * This implementation retrieves {@link AccountantUser} entities from the database
 * and converts them into Spring Security {@link UserDetails}.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountantUserRepository accountantUserRepository;

    public CustomUserDetailsService(AccountantUserRepository accountantUserRepository) {
        this.accountantUserRepository = accountantUserRepository;
    }
    /**
     * Loads a user by their username for authentication.
     * If the user is not found, throws {@link UsernameNotFoundException}.
     *
     * @param username the username identifying the user
     * @return the Spring Security {@link UserDetails} for authentication
     * @throws UsernameNotFoundException if user is not found in the database
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountantUser user = accountantUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.getPassword())
                .roles(user.getRole())
                .build();

    }

}
