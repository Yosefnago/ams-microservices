package com.ams.controller;



import com.ams.commonsecurity.utils.JwtUtil;
import com.ams.dtos.clientDto.*;
import com.ams.dtos.documentDto.*;
import com.ams.dtos.invoiceDto.*;
import com.ams.dtos.loginDto.ClientLoginRequest;
import com.ams.dtos.loginDto.ClientLoginResponse;
import com.ams.entity.ClientDetails;
import com.ams.entity.Documents;
import com.ams.entity.Invoice;
import com.ams.repository.ClientRepository;
import com.ams.service.ClientService;
import com.ams.service.DocumentService;
import com.ams.service.InvoiceOCRExtractor;
import com.ams.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * {@code ClientController} is a REST controller responsible for handling all client-related operations
 * in the accounting management system.
 * <p>
 * This includes creating, deleting, retrieving, and updating client information.
 * </p>
 * <p>
 * It delegates business logic to {@link ClientService} and accesses data via {@link ClientRepository}.
 * </p>
 *
 * <p>Main responsibilities:</p>
 * <ul>
 *     <li>Create a new client</li>
 *     <li>Delete an existing client</li>
 *     <li>Fetch full client details for case display</li>
 *     <li>Load all clients belonging to an accountant</li>
 *     <li>Fetch the total number of clients for dashboard display</li>
 * </ul>
 *
 * @author Yosef Nago
 */
@RestController
@RequestMapping("/client")
public class ClientController {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ClientService clientService;
    private final DocumentService documentService;
    private final InvoiceService invoiceService;
    private final InvoiceOCRExtractor invoiceOCRExtractor;
    /**
     * Constructs a {@code ClientController} with required dependencies.
     *
     * @param clientService the business logic layer for client operations
     * @param jwtUtil utility for handling JWT tokens
     */
    @Autowired
    public ClientController(ClientService clientService,
                            DocumentService documentService,
                            InvoiceService invoiceService,
                            InvoiceOCRExtractor invoiceOCRExtractor,
                            JwtUtil jwtUtil,
                            PasswordEncoder passwordEncoder) {

        this.clientService = clientService;
        this.documentService = documentService;
        this.invoiceService = invoiceService;
        this.invoiceOCRExtractor = invoiceOCRExtractor;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Creates a new client if the identifiers (tax ID, email, bank account) are valid and unique.
     *
     * @param createClientRequest request body containing all required client details
     * @return a {@link ResponseEntity} with {@link CreateClientResponse} indicating success or validation error
     */
    @PostMapping("/create")
    public ResponseEntity<CreateClientResponse> createClient(@RequestBody CreateClientRequest createClientRequest) {



        if (clientService.existsClientById(createClientRequest.tax_id())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CreateClientResponse(false, "מספר ח.פ/ת.ז לא תקין"));
        } else if (clientService.existsClientByEmail(createClientRequest.email())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CreateClientResponse(false, "אימייל לא תקין"));
        } else if (clientService.existsByAccountNumber(createClientRequest.bankNumber())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CreateClientResponse(false, "מספר חשבון בנק לא תקין"));
        }

        try {
            clientService.createNewClient(createClientRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CreateClientResponse(true, "לקוח נרשם בהצלחה"));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CreateClientResponse(false, e.getMessage()));
        }
    }

    /**
     * Deletes a client from the system by tax ID.
     *
     * @param id the client's tax ID or national ID
     * @return HTTP 200 OK if deletion is successful
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable String id) {
        clientService.deleteClientByClientId(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Loads summary client details for display in the case view header.
     *
     * @param clientId the unique identifier of the client
     * @return a {@link LoadClientDetailsCaseResponse} with client info or an error message if not found
     */
    @GetMapping("/load-case-details")
    public ResponseEntity<LoadClientDetailsCaseResponse> loadClientDetails(@RequestParam String clientId) {
        ClientDetails clientDetails = clientService.getClientsByTaxId(clientId);

        //Client not found
        if (clientDetails == null) {
            return ResponseEntity.ok(
                    new LoadClientDetailsCaseResponse(false,
                            "לקוח לא נמצא",
                            null, null, null, null, null, null));
        }

        //Client found, return client info
        return ResponseEntity.ok(
                new LoadClientDetailsCaseResponse(
                        true,
                        "פרטי לקוח נטענו בהצלחה",
                        clientDetails.getBusinessName(),
                        clientDetails.getClientId(),
                        clientDetails.getEmail(),
                        clientDetails.getPhone(),
                        clientDetails.getAddress(),
                        clientDetails.getBusinessType()
                )
        );
    }
    /**
     * Updates existing client information in the system.
     *
     * @param request a {@link LoadClientCaseDetailsRequest} containing updated client data
     * @return a {@link UpdateClientResponse} with confirmation message
     */
    @PutMapping("/update")
    public ResponseEntity<UpdateClientResponse> updateClientCaseDetails(@RequestBody LoadClientCaseDetailsRequest request){
        //Internal server error
        if (request == null){
            return ResponseEntity.badRequest().body(new UpdateClientResponse("תקלה בשרת"));
        }


        ClientDetails clientDetails = new ClientDetails();
        clientDetails.setClientId(request.clientId());
        clientDetails.setBusinessName(request.businessName());
        clientDetails.setEmail(request.email());
        clientDetails.setPhone(request.phone());
        clientDetails.setAddress(request.address());
        clientDetails.setZip(request.zip());
        clientDetails.setBusinessType(request.businessType());
        clientDetails.setBankName(request.bankName());
        clientDetails.setBankBranch(request.bankBranch());

        clientService.updateClient(clientDetails);

        return ResponseEntity.ok(new UpdateClientResponse("לקוח עודכן בהצלחה"));
    }
    /**
     * Loads full editable case details for a client based on their ID.
     *
     * @param clientId the unique identifier of the client
     * @return a {@link LoadClientCaseDetailsRequest} with pre-filled fields or empty if not found
     */
    @GetMapping("/load-client-case")
    public ResponseEntity<LoadClientCaseDetailsRequest> loadClientCase(@RequestParam String clientId) {

        ClientDetails clientDetails = clientService.getClientsByTaxId(clientId);

        //Client not found
        if (Objects.isNull(clientDetails)){
            return ResponseEntity.ok(new LoadClientCaseDetailsRequest(null,null,null,null,null,null,null,null,null,null));
        }

        //Client found, return client info
        return ResponseEntity.ok(
                new LoadClientCaseDetailsRequest(
                        clientDetails.getClientId(),
                        clientDetails.getBusinessName(),
                        clientDetails.getEmail(),
                        clientDetails.getPhone(),
                        clientDetails.getAddress(),
                        clientDetails.getZip(),
                        clientDetails.getBusinessType(),
                        clientDetails.getBankName(),
                        clientDetails.getBankBranch(),
                        clientDetails.getBankAccountNumber()

                )
        );

    }

    /**
     * Loads all clients associated with a given accountant.
     *
     * @param username the accountant’s username (sent via header)
     * @return a {@link LoadClientResponse} containing a list of {@link ClientGridDto}
     */
    @GetMapping("/load-clients")
    public ResponseEntity<LoadClientResponse> loadClients(@RequestHeader("X-User-Name") String username) {
        List<ClientDetails> clientDetailsList = clientService.getAllClientsByaccountantName(username);

        if (Objects.isNull(clientDetailsList)) {
            return ResponseEntity.status(HttpStatus.CONTINUE)
                    .body(new LoadClientResponse(false,"לא נמצאו לקוחות ",Collections.emptyList()));
        }

        try {

            List<ClientGridDto> clientGridDtos = clientDetailsList.stream()
                    .map(details -> new ClientGridDto(
                            details.getBusinessName(),
                            details.getClientId(),
                            details.getEmail(),
                            details.getPhone()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new LoadClientResponse(true, "לקוחות נטענו בהצלחה", clientGridDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoadClientResponse(false, "שגיאה בטעינת לקוחות", List.of()));
        }

    }

    /**
     * Retrieves the total number of clients associated with a given accountant.
     *
     * @param username the accountant's username (sent via header)
     * @return a {@link LoadNumOfClientsResponse} containing the client count
     */
    @GetMapping("/load-numOfclients")
    public ResponseEntity<LoadNumOfClientsResponse> loadNumOfClients(@RequestHeader("X-User-Name") String username) {

        List<ClientDetails> clientDetailsList = clientService.getAllClientsByaccountantName(username);
        int numOfClients = clientDetailsList.size();
        return ResponseEntity.ok(new LoadNumOfClientsResponse(true, "מספר לקוחות נטענו", numOfClients));
    }
    @PostMapping("/grant-access")
    public ResponseEntity<GrantAccessResponse> grantAccessToClient(@RequestBody GrantAccessRequestDto grantAccessRequestDto) {

        try {
            clientService.grantLoginAccess(
                    grantAccessRequestDto.clientName(),
                    grantAccessRequestDto.clientUsername(),
                    grantAccessRequestDto.clientPassword()
            );
            return ResponseEntity.ok(new GrantAccessResponse(true, "עודכנה גישת התחברות ללקוח"));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new GrantAccessResponse(false, "לא ניתן לתת גישה ללקוח"));
        }
    }
    @PostMapping("/login")
    public ResponseEntity<ClientLoginResponse> login(@RequestBody ClientLoginRequest request){
        //Check if client exists
        Optional<ClientDetails> client = clientService.getClientByClientUsername(request.username());

        //if client exists, check password,generate token,set role,set id
        if (Objects.nonNull(client)){
            String token = jwtUtil.generateToken(client.get().getClientUsername(),"CLIENT",client.get().getClientId());

            if (client.isPresent() && passwordEncoder.matches(request.password(),client.get().getClientPassword())){
                return ResponseEntity.ok(new ClientLoginResponse(true,"התחברת בהצלחה",token,client.get().getClientId()));
            }

        }

        return ResponseEntity
                .ok(new ClientLoginResponse(false, "לא ניתן להתחבר", "", ""));
    }

}

