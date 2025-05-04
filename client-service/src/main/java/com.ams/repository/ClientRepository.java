package com.ams.repository;


import com.ams.entity.ClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * {@code ClientRepository} is the JPA repository interface for managing {@link ClientDetails} entities.
 * <p>
 * It extends {@link JpaRepository}, thus providing full CRUD operations (create, read, update, delete),
 * as well as custom query methods for searching and managing clients.
 * </p>
 *
 * <p>This interface is part of the persistence layer and interacts directly with the database.</p>
 *
 * <p><b>Key Methods:</b></p>
 * <ul>
 *   <li>{@link #findByEmail(String)} – find a client by email</li>
 *   <li>{@link #findByClientId(String)} – find a client by tax ID</li>
 *   <li>{@link #getAllByAccountantName(String)} – retrieve clients for a given accountant</li>
 * </ul>
 *
 * @author Yosef
 */
@Repository
public interface ClientRepository extends JpaRepository<ClientDetails,Long> {

    /**
     * Finds a client entity by their email address.
     *
     * @param email the client's email
     * @return the matching {@link ClientDetails}, or {@code null} if not found
     */
    ClientDetails findByEmail(String email);

    /**
     * Checks if a client with the given email already exists in the database.
     *
     * @param email the email to check for existence
     * @return {@code true} if a client with the email exists, {@code false} otherwise
     */
    boolean existsByEmail(String email);
    boolean existsByBankAccountNumber(String bankAccountNumber);
    /**
     * Deletes a client by their unique client ID (tax ID).
     *
     * @param clientId the client's tax ID
     */
    void deleteByClientId(String clientId);

    /**
     * Finds a client entity by their tax ID.
     *
     * @param clientId the client's tax ID
     * @return the matching {@link ClientDetails}, or {@code null} if not found
     */
    ClientDetails findByClientId(String clientId);
    /**
     * Checks if a client with the given tax ID exists.
     *
     * @param clientId the tax ID to check
     * @return {@code true} if a client exists with the given tax ID, {@code false} otherwise
     */
    boolean existsByClientId(String clientId);

    /**
     * Retrieves all clients associated with a specific accountant.
     *
     * @param accountantName the username of the accountant
     * @return list of {@link ClientDetails} linked to the accountant
     */
    @Query("SELECT c FROM ClientDetails c WHERE c.accountantName = :accountantName")
    List<ClientDetails> getAllByAccountantName(@Param("accountantName") String accountantName);


}
