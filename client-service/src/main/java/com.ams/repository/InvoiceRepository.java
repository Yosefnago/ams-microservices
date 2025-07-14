package com.ams.repository;

import com.ams.dtos.invoiceDto.InvoiceGrid;
import com.ams.dtos.invoiceDto.InvoiceGridDto;
import com.ams.dtos.invoiceDto.InvoiceOutComeM;
import com.ams.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * InvoiceRepository provides data access operations for the {@link Invoice} entity.
 *
 * This includes:
 * - Retrieving invoices by client or identifiers
 * - Updating status and rejection reasons
 * - Calculating totals (outcomes)
 * - Returning custom projections (DTOs)
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * Retrieves the rejection reason for a specific invoice by invoice number.
     *
     * @param invoiceNumber the invoice number
     * @return the rejection reason string
     */
    @Query("select i.reason from Invoice i where i.fileName = :fileName")
    String getRejectedReason(String fileName);

    /**
     * Updates the status and rejection reason of an invoice.
     *
     * @param status        the new status (e.g., "אושר", "נדחה")
     * @param reason        the reason for rejection (nullable if approved)
     * @param invoiceNumber the invoice number
     */
    @Modifying
    @Query("UPDATE Invoice i SET i.status = :status, i.reason = :reason WHERE i.invoiceNumber = :invoiceNumber")
    void updateStatus(@Param("status") String status,
                      @Param("reason") String reason,
                      @Param("invoiceNumber") String invoiceNumber);

    /**
     * Deletes an invoice by its invoice number.
     *
     * @param invoiceNumber the invoice number to delete
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Invoice i WHERE i.fileName = :fileName")
    void deleteByFileName(@Param("fileName") String fileName);

    /**
     * Retrieves all invoices belonging to a specific client,
     * projected into {@link InvoiceGrid} for UI display.
     *
     * @param clientId the client ID
     * @return list of invoice grid DTOs
     */
    @Query("SELECT new com.ams.dtos.invoiceDto.InvoiceGrid(" +
            "d.fileName, d.invoiceNumber, d.sapakName, d.price, d.updatedAt, d.clientId, d.status) " +
            "FROM Invoice d WHERE d.clientId = :clientId")
    List<InvoiceGrid> findAllInvoicesByClientId(@Param("clientId") String clientId);

    /**
     * Finds an invoice by its stored file name.
     *
     * @param fileName the stored file name
     * @return Optional containing the invoice if found
     */
    Optional<Invoice> findByFileName(String fileName);

    /**
     * Calculates the total approved outcome (SUM of price)
     * for a given client, returning a summary DTO.
     *
     * @param clientId the client ID
     * @return outcome DTO containing total approved sum
     */
    @Query("SELECT new com.ams.dtos.invoiceDto.InvoiceOutComeM(i.clientId, SUM(i.price)) " +
            "FROM Invoice i WHERE i.status = 'אושר' AND i.clientId = :clientId GROUP BY i.clientId")
    InvoiceOutComeM getOutcomeDto(@Param("clientId") String clientId);


}



