package com.ams.accountantUser.controller;

import com.ams.accountantUser.entity.AccountantUser;
import com.ams.accountantUser.service.AccountantUserService;
import com.ams.accountantUser.service.DocumentService;
import com.ams.commonsecurity.utils.JwtUtil;
import com.ams.dtos.accountantDto.AccountantDetailsResponse;
import com.ams.dtos.clientDto.LoadClientDetailsCaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final DocumentService documentService;
    private final JwtUtil jwtUtil;

    /**
     * Constructs the {@code UserController} with necessary dependencies.
     *
     * @param accountantUserService the service handling accountant user logic
     * @param jwtUtil               utility for JWT token operations
     * @param passwordEncoder       encoder used for password hashing
     */
    public UserController(@Autowired AccountantUserService accountantUserService,DocumentService documentService,JwtUtil jwtUtil,PasswordEncoder passwordEncoder) {
        this.accountantUserService = accountantUserService;
        this.jwtUtil = jwtUtil;
        this.documentService = documentService;
        this.passwordEncoder = passwordEncoder;
    }
    /**
     * Retrieves accountant user details by username.
     *
     * @param username the username to look up
     * @return {@link AccountantDetailsResponse} containing user data or error message
     */

    @GetMapping("/load-case-details")
    public ResponseEntity<LoadClientDetailsCaseResponse> loadClientDetails(@RequestParam Long  clientId) {
        Optional<AccountantUser> clientDetails = accountantUserService.getClientById(clientId);

        //Client not found
        if (clientDetails.isEmpty()) {
            return ResponseEntity.ok(
                    new LoadClientDetailsCaseResponse(false,
                            "לקוח לא נמצא",
                            null, null, null, null));
        }

        //Client found, return client info
        return ResponseEntity.ok(
                new LoadClientDetailsCaseResponse(
                        true,
                        "פרטי לקוח נטענו בהצלחה",
                        clientDetails.get().getBusinessName(),
                        clientDetails.get().getTaxId(),
                        clientDetails.get().getEmail(),
                        clientDetails.get().getPhone()

                )
        );
    }

}
