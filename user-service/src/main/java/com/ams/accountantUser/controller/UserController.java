package com.ams.accountantUser.controller;

import com.ams.accountantUser.entity.AccountantUser;
import com.ams.accountantUser.reposiroty.AccountantUserRepository;
import com.ams.accountantUser.service.AccountantUserService;
import com.ams.commonsecurity.utils.JwtUtil;
import com.ams.dtos.accountantDto.AccountantDetailsResponse;
import com.ams.dtos.clientDto.LoadClientDetailsCaseResponse;
import com.ams.dtos.loginDto.LoginRequest;
import com.ams.dtos.loginDto.LoginResponse;
import com.ams.dtos.registerDto.RegisterRequest;
import com.ams.dtos.registerDto.RegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

/**
 * {@code UserController} exposes REST endpoints related to the accountant user entity.
 * <p>
 * It serves operations such as loading user profile details based on the username,
 * typically used after authentication to retrieve user-specific metadata.
 * </p>
 *
 * <p><b>Endpoints:</b></p>
 * <ul>
 *     <li>GET /user/load-details – Retrieves detailed information about a specific accountant user by username</li>
 * </ul>
 *
 * <p><b>Note:</b> This controller relies on {@link AccountantUserService} for all business logic.</p>
 *
 * @author Yosef Nago
 * @see AccountantUserService
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final AccountantUserService accountantUserService;
    private final JwtUtil jwtUtil;

    /**
     * Constructs the {@code UserController} with necessary dependencies.
     *
     * @param accountantUserService the service handling accountant user logic
     * @param jwtUtil               utility for JWT token operations
     * @param passwordEncoder       encoder used for password hashing
     */
    public UserController(@Autowired AccountantUserService accountantUserService,JwtUtil jwtUtil,PasswordEncoder passwordEncoder) {
        this.accountantUserService = accountantUserService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }
    /**
     * Retrieves accountant user details by username.
     *
     * @param username the username to look up
     * @return {@link AccountantDetailsResponse} containing user data or error message
     */
    @GetMapping("/load-details")
    public ResponseEntity<AccountantDetailsResponse> loadClientDetails(@RequestParam String username) {

        Optional<AccountantUser> accountantUser = accountantUserService.findByUsername(username);

        if (accountantUser == null){
            ResponseEntity.ok(new AccountantDetailsResponse(false,
                    "שגיאה בטעינה פרטים",
                    null,
                    null,
                    null,
                    null)
            );
        }
        return ResponseEntity.ok(new AccountantDetailsResponse(
                true,
                   "פרטים נטענו בהצלחה",
                accountantUser.get().getUsername(),
                accountantUser.get().getEmail(),
                accountantUser.get().getPhone(),
                accountantUser.get().getId()

        ));
    }
}
