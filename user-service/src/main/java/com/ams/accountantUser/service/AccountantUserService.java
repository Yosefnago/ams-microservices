package com.ams.accountantUser.service;


import com.ams.dtos.registerDto.RegisterRequest;
import com.ams.accountantUser.entity.AccountantUser;
import com.ams.accountantUser.reposiroty.AccountantUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * {@code AccountantUserService} provides business logic for managing {@link AccountantUser} entities.
 * <p>
 * It handles operations such as registration, retrieval, deletion, and existence checks.
 * The service uses {@link PasswordEncoder} to securely store passwords, and communicates with the database
 * through {@link AccountantUserRepository}.
 * </p>
 *
 * @author Yosef Nago
 * @see AccountantUser
 * @see AccountantUserRepository
 */
@Service
public class AccountantUserService  {

    private final PasswordEncoder passwordEncoder;

    private final AccountantUserRepository accountantUserRepository;

    /**
     * Constructs a new {@code AccountantUserService} with required dependencies.
     *
     * @param accountantUserRepository the repository used for data access
     * @param passwordEncoder the encoder used to hash user passwords
     */
    public AccountantUserService(@Autowired AccountantUserRepository accountantUserRepository, PasswordEncoder passwordEncoder) {
        this.accountantUserRepository = accountantUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new accountant user based on the given {@link RegisterRequest}.
     *
     * @param request the registration data from the client
     */
    public void register(RegisterRequest request) {

        AccountantUser accountantUser = new AccountantUser();

        accountantUser.setFirstName(request.firstName());
        accountantUser.setLastName(request.lastName());
        accountantUser.setUsername(request.username());
        accountantUser.setPassword(passwordEncoder.encode(request.password()));
        accountantUser.setPhone(request.phoneNumber());
        accountantUser.setEmail(request.email());

        accountantUserRepository.save(accountantUser);
    }

    /**
     * Retrieves an accountant user by their unique ID.
     *
     * @param id the ID to search for
     * @return an {@link Optional} containing the user if found, or empty otherwise
     */
    public Optional<AccountantUser> findId(Long id) {
        return accountantUserRepository.findById(id);
    }

    /**
     * Retrieves an accountant user by their username.
     *
     * @param username the username to search for
     * @return an {@link Optional} containing the user if found, or empty otherwise
     */
    public  Optional<AccountantUser> findByUsername(String username) {
        return accountantUserRepository.findByUsername(username);
    }

    /**
     * Deletes an accountant user by their username.
     *
     * @param username the username of the user to delete
     */
    public  void deleteAccountantUser(String username) {
        accountantUserRepository.deleteByUsername(username);
    }

    /**
     * Checks whether an accountant user exists with the given username.
     *
     * @param username the username to check
     * @return {@code true} if a user with that username exists, {@code false} otherwise
     */
    public boolean existsByUsername(String username) {
        return accountantUserRepository.findByUsername(username) != null;
    }
}
