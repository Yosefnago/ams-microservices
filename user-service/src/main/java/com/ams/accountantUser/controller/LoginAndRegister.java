package com.ams.accountantUser.controller;


import com.ams.accountantUser.entity.AccountantUser;
import com.ams.accountantUser.reposiroty.AccountantUserRepository;
import com.ams.accountantUser.service.AccountantUserService;
import com.ams.commonsecurity.utils.JwtUtil;
import com.ams.dtos.loginDto.LoginRequest;
import com.ams.dtos.loginDto.LoginResponse;
import com.ams.dtos.registerDto.RegisterRequest;
import com.ams.dtos.registerDto.RegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
/**
 * Controller that handles user authentication operations such as registration and login.
 * <p>
 * Exposes REST endpoints under the "/auth" path, allowing new accountant users to register
 * and existing users to authenticate and receive a JWT token for secured access.
 * </p>
 *
 * <p>
 * This controller relies on the {@link AccountantUserService} for business logic,
 * {@link PasswordEncoder} for password verification, and {@link JwtUtil} for token generation.
 * </p>
 *
 * <p><b>Endpoints:</b></p>
 * <ul>
 *     <li>POST /auth/register - Registers a new user</li>
 *     <li>POST /auth/login - Authenticates a user and returns a JWT</li>
 * </ul>
 *
 * @author Yosef Nago
 */
@RestController
@RequestMapping("/auth")
public class LoginAndRegister {

    private final JwtUtil jwtUtil;
    private final AccountantUserService accountantUserService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs the LoginAndRegister controller with necessary dependencies.
     *
     * @param accountantUserService the service managing accountant user logic
     * @param passwordEncoder       encoder for hashing and verifying passwords
     * @param jwtUtil               utility for generating and parsing JWT tokens
     */
    @Autowired
    public LoginAndRegister(AccountantUserService accountantUserService,PasswordEncoder passwordEncoder,JwtUtil jwtUtil){
        this.passwordEncoder = passwordEncoder;
        this.accountantUserService = accountantUserService;
        this.jwtUtil = jwtUtil;
    }
    /**
     * Endpoint for registering a new user.
     * <p>
     * Accepts a {@link RegisterRequest} with username and password.
     * If the username is already taken, returns a 400 Bad Request.
     * Otherwise, registers the user and returns 201 Created.
     * </p>
     *
     * @param request the registration request containing username and password
     * @return a {@link RegisterResponse} indicating success or failure
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {

        if (accountantUserService.existsByUsername(request.username())){
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RegisterResponse(false,"שם משתמש קיים כבר"));
        }
        accountantUserService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(true,"ההרשמה בוצעה בהצלחה"));
    }
    /**
     * Endpoint for authenticating a user.
     * <p>
     * Accepts a {@link LoginRequest} containing credentials.
     * If the credentials are valid, generates and returns a JWT token.
     * If invalid, returns 401 Unauthorized.
     * </p>
     *
     * @param loginRequest the login request containing username and password
     * @return a {@link LoginResponse} containing the result and token (if successful)
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Optional<AccountantUser> user = accountantUserService.findByUsername(loginRequest.username());

        if (user.isPresent() && passwordEncoder.matches(loginRequest.password(), user.get().getPassword())) {
            String token = jwtUtil.generateToken(user.get().getUsername(), "ACCOUNTANT");
            return ResponseEntity.ok(new LoginResponse(true, "התחברת בהצלחה", token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse(false, "שם משתמש או סיסמא שגויים", ""));
    }
}
