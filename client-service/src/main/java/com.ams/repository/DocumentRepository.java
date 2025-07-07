package com.ams.repository;

import com.ams.dtos.documentDto.DocumentCareGridDto;
import com.ams.dtos.documentDto.DocumentGrid;
import com.ams.entity.Documents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * DocumentRepository is the JPA repository interface for {@link Documents} entity.
 * It provides custom queries for managing and retrieving documents with projections
 * for UI views (like DocumentGrid and DocumentCareGridDto).
 */
@Repository
public interface DocumentRepository extends JpaRepository<Documents, Long> {

    /**
     * Retrieves a projection of documents into {@link DocumentGrid} for client UI display.
     *
     * @param clientId the client ID
     * @return list of document grid DTOs
     */
    @Query("SELECT new com.ams.dtos.documentDto.DocumentGrid" +
            "(d.documentName, d.clientId, d.uploadedAt, d.status)" +
            " FROM Documents d " +
            "WHERE d.clientId = :clientId")
    List<DocumentGrid> findAllGridByClientId(@Param("clientId") String clientId);

    /**
     * Deletes a document from the database by its document name.
     *
     * @param documentName the name of the document to delete
     */
    @Modifying
    @Query("DELETE FROM Documents d WHERE d.documentName = :documentName")
    void deleteByDocumentName(@Param("documentName") String documentName);

    /**
     * Updates the status and rejection reason of a document.
     *
     * @param name   the document name
     * @param status the new status (e.g., "אושר", "נדחה")
     * @param reason the reason for rejection (nullable)
     */
    @Modifying
    @Query("UPDATE Documents d SET d.status = :status, d.reason = :reason WHERE d.documentName = :name")
    void updateStatus(@Param("name") String name, @Param("status") String status, @Param("reason") String reason);

    /**
     * Retrieves a document entity by its document name (optional wrapper).
     *
     * @param documentName the document name
     * @return Optional of document entity
     */
    Optional<Documents> findByDocumentName(String documentName);

    /**
     * Gets the rejection reason of a specific document.
     *
     * @param documentName the document name
     * @return the rejection reason string
     */
    @Query("select d.reason from Documents d where d.documentName = :documentName")
    String getRejectedReason(String documentName);

    /**
     * Counts how many documents are in a given status for a list of client IDs.
     *
     * @param clientIds list of client IDs
     * @param status    the status to filter by
     * @return the number of documents
     */
    @Query("SELECT COUNT(d) FROM Documents d WHERE d.clientId IN :clientIds AND d.status = :status")
    int countByClientIdInAndStatus(@Param("clientIds") List<String> clientIds, @Param("status") String status);

    /**
     * Retrieves documents that are pending review by an accountant.
     * Joins the Documents entity with ClientDetails entity to get business name.
     *
     * @param accountantName the accountant's username
     * @return list of DocumentCareGridDto for UI grid
     */
    @Query("SELECT new com.ams.dtos.documentDto.DocumentCareGridDto(d.documentName, c.businessName, d.uploadedAt, d.status) " +
            "FROM Documents d JOIN ClientDetails c ON d.clientId = c.clientId " +
            "WHERE c.accountantName = :accountantName AND d.status = 'ממתין לטיפול'")
    List<DocumentCareGridDto> findPendingDocumentsCareList(@Param("accountantName") String accountantName);
}
