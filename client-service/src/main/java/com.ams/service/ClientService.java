package com.ams.service;


import com.ams.commonsecurity.utils.JwtUtil;
import com.ams.dtos.clientDto.CreateClientRequest;
import com.ams.entity.ClientDetails;
import com.ams.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * {@code ClientService} encapsulates the core business logic related to managing clients.
 * <p>
 * It provides methods to create, retrieve, update, and delete client records.
 * It also includes utility methods for validation and existence checks.
 * </p>
 *
 * <p>
 * This service sits between the {@code ClientController} and the {@code ClientRepository},
 * ensuring separation of concerns and centralizing domain rules.
 * </p>
 *
 * <p>
 * Changes in business rules for clients should be handled here.
 * </p>
 *
 * @author Yosef Nago
 */
@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final JwtUtil jwtUtil;

    /**
     * Constructs a new {@code ClientService} with required dependencies.
     *
     * @param clientRepository the repository for data access operations
     * @param jwtUtil utility class for extracting information from JWT tokens
     */
    public ClientService(@Autowired ClientRepository clientRepository, JwtUtil jwtUtil) {
        this.clientRepository = clientRepository;
        this.jwtUtil = jwtUtil;

    }

    /**
     * Creates and saves a new client entity.
     * <p>
     * Extracts the accountant's username from the JWT token in the request
     * and assigns it to the client.
     * </p>
     *
     * @param request the client data wrapped in {@link CreateClientRequest}
     * @throws IllegalStateException if saving fails or if an exception occurs
     */
    public void createNewClient(CreateClientRequest request) {
        try{

            ClientDetails clientEntity = new ClientDetails();
            clientEntity.setBusinessName(request.businessName());
            clientEntity.setClientId(request.tax_id());
            clientEntity.setEmail(request.email());
            clientEntity.setPhone(request.phone());
            clientEntity.setContactPhone(request.phone());
            clientEntity.setAddress(request.address());
            clientEntity.setZip(request.zip());
            clientEntity.setBusinessType(request.clientType());
            clientEntity.setBankName(request.bankName());
            clientEntity.setBankBranch(request.bankBranch());
            clientEntity.setBankAccountNumber(request.bankNumber());
            clientEntity.setAccountOwnerName(request.bankOwnerName());
            String username = jwtUtil.extractUsername(request.token());
            clientEntity.setAccountantName(username);

            clientRepository.save(clientEntity);

        } catch (Exception e) {
            throw new IllegalStateException("Connection is bad..");
        }

    }
    /**
     * Retrieves a client by their tax ID (ת.ז/ח.פ).
     *
     * @param clientId the unique tax ID of the client
     * @return the {@link ClientDetails} or {@code null} if not found
     */
    public ClientDetails getClientsByTaxId(String clientId) {

        return clientRepository.findByClientId(clientId);
    }


    /**
     * Retrieves a client by their email address.
     *
     * @param email the client's email
     * @return the {@link ClientDetails} or {@code null} if not found
     */
    public ClientDetails getClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    /**
     * Retrieves all clients associated with a given accountant.
     *
     * @param accountantName the username of the accountant
     * @return list of {@link ClientDetails}
     */
    public List<ClientDetails> getAllClientsByaccountantName(String accountantName) {
        return clientRepository.getAllByAccountantName(accountantName);
    }
    /**
     * Deletes a client from the system based on their client ID.
     *
     * @param id the client's unique ID
     */
    @Transactional
    public void deleteClientByClientId(String id) {
        clientRepository.deleteByClientId(id);
    }
    /**
     * Checks whether a client exists by their tax ID.
     *
     * @param id the tax ID
     * @return {@code true} if exists, {@code false} otherwise
     */
    public boolean existsClientById(String id) {
        return clientRepository.existsByClientId(id);
    }
    /**
     * Checks whether a client exists by their email.
     *
     * @param email the email address
     * @return {@code true} if exists, {@code false} otherwise
     */
    public boolean existsClientByEmail(String email){
        return clientRepository.existsByEmail(email);
    }
    /**
     * Checks whether a client exists by their bank account number.
     *
     * @param bankAccountNumber the bank account number
     * @return {@code true} if exists, {@code false} otherwise
     */
    public boolean existsByAccountNumber(String bankAccountNumber){
        return clientRepository.existsByBankAccountNumber(bankAccountNumber);
    }
    /**
     * Updates an existing client's details with non-null values provided in the request.
     *
     * @param clientDetails the {@link ClientDetails} object with updated fields
     * @throws IllegalStateException if the client does not exist
     */
    public void updateClient(ClientDetails clientDetails){

        ClientDetails existing = clientRepository.findByClientId(clientDetails.getClientId());

        if (existing == null) {
            throw new IllegalStateException("Client not found with id: " + clientDetails.getClientId());
        }

        if (clientDetails.getBusinessName() != null) {
            existing.setBusinessName(clientDetails.getBusinessName());
        }

        if (clientDetails.getEmail() != null) {
            existing.setEmail(clientDetails.getEmail());
        }

        if (clientDetails.getPhone() != null) {
            existing.setPhone(clientDetails.getPhone());
        }

        if (clientDetails.getAddress() != null) {
            existing.setAddress(clientDetails.getAddress());
        }

        if (clientDetails.getZip() != null) {
            existing.setZip(clientDetails.getZip());
        }

        if (clientDetails.getBusinessType() != null) {
            existing.setBusinessType(clientDetails.getBusinessType());
        }

        if (clientDetails.getBankName() != null) {
            existing.setBankName(clientDetails.getBankName());
        }

        if (clientDetails.getBankBranch() != null) {
            existing.setBankBranch(clientDetails.getBankBranch());
        }

        if (clientDetails.getBankAccountNumber() != null) {
            existing.setBankAccountNumber(clientDetails.getBankAccountNumber());
        }

        clientRepository.save(existing);
    }
}
