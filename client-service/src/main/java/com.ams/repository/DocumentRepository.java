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

    @Modifying
    @Query("UPDATE Documents d SET d.status = :status, d.reason = :reason WHERE d.documentName = :name")
    void updateStatus(@Param("name") String name, @Param("status") String status, @Param("reason") String reason);

    Documents getDocumentsByDocumentName(String documentName);
    Optional<Documents> findByDocumentName(String documentName);

    @Query("select d.reason from Documents d where d.documentName = :documentName")
    String getRejectedReason(String documentName);

    @Query("SELECT COUNT(d) FROM Documents d WHERE d.clientId IN :clientIds AND d.status = :status")
    int countByClientIdInAndStatus(@Param("clientIds") List<String> clientIds, @Param("status") String status);

    @Query("SELECT new com.ams.dtos.documentDto.DocumentCareGridDto(d.documentName, c.businessName, d.uploadedAt, d.status) " +
            "FROM Documents d JOIN ClientDetails c ON d.clientId = c.clientId " +
            "WHERE c.accountantName = :accountantName AND d.status = 'ממתין לטיפול'")
    List<DocumentCareGridDto> findPendingDocumentsCareList(@Param("accountantName") String accountantName);
}
