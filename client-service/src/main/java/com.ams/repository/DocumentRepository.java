package com.ams.repository;

import com.ams.dtos.documentDto.DocumentGrid;
import com.ams.entity.Documents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Documents, Long> {

    List<Documents> findAllByClientId(String clientId);

    List<Documents> findAllByStatus(String status);
    @Query("SELECT new com.ams.dtos.documentDto.DocumentGrid" +
            "(d.documentName, d.clientId, d.uploadedAt, d.status)" +
            " FROM Documents d " +
            "WHERE d.clientId = :clientId")
    List<DocumentGrid> findAllGridByClientId(@Param("clientId") String clientId);

    @Modifying
    @Query("DELETE FROM Documents d WHERE d.documentName = :documentName")
    void deleteByDocumentName(@Param("documentName") String documentName);
}
