package com.ams.accountantUser.repository;

import com.ams.dtos.documentDto.DocumentGrid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<com.ams.entity.Documents, Long> {


    /**
     * Retrieves a projection of documents into {@link DocumentGrid} for client UI display.
     *
     * @param clientId the client ID
     * @return list of document grid DTOs
     */
    @Query("SELECT new com.ams.dtos.documentDto.DocumentGrid" +
            "(d.documentName, d.clientId, d.uploadedAt)" +
            " FROM Documents d " +
            "WHERE d.clientId = :clientId")
    List<DocumentGrid> findAllGridByClientId(@Param("clientId") String clientId);

    @Modifying
    @Query("DELETE FROM Documents d WHERE d.documentName = :documentName")
    void deleteByDocumentName(@Param("documentName") String documentName);

    Optional<com.ams.entity.Documents> findByDocumentName(String documentName);}
