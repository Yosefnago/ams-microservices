package com.ams.service;

import com.ams.dtos.invoiceDto.InvoiceGrid;
import com.ams.dtos.invoiceDto.InvoiceOutComeM;
import com.ams.dtos.invoiceDto.InvoiceUpdateRequest;
import com.ams.entity.Invoice;
import com.ams.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * InvoiceService handles business logic related to invoices,
 * including saving, updating, deleting, and retrieving invoices and their summaries.
 *
 * This service interacts directly with {@link InvoiceRepository}.
 */
@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;


    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;

    }
    /**
     * Retrieves all invoices belonging to a specific client, formatted as grid DTOs.
     *
     * @param clientId the client's unique ID
     * @return list of {@link InvoiceGrid}
     */
    public List<InvoiceGrid> getAllInvoices(String clientId){

        return invoiceRepository.findAllInvoicesByClientId(clientId);
    }
    /**
     * Persists a new or updated invoice entity into the database.
     *
     * @param invoice the invoice entity to save
     */
    public void saveInvoice(Invoice invoice) {

        invoiceRepository.save(invoice);
    }
    /**
     * Retrieves the rejection reason of a given invoice by its invoice number.
     *
     * @param invoiceNumber the invoice number
     * @return the rejection reason string
     */
    public String getRejectedReasonInvoice(String fileName){
        return invoiceRepository.getRejectedReason(fileName);
    }
    /**
     * Updates an invoice's status, rejection reason, and extracted fields.
     *
     * @param request the invoice update request DTO
     */
    @Transactional
    public void updateInvoiceStatus(InvoiceUpdateRequest request) {
        Invoice invoice = invoiceRepository.findByFileName(request.fileName())
                .orElseThrow(() -> new RuntimeException("חשבונית לא נמצאה"));

        invoice.setStatus(request.status());
        invoice.setReason(request.rejectionReason());
        invoice.setInvoiceNumber(request.invoiceNumber());
        invoice.setSapakName(request.sapakName());
        invoice.setPrice(request.price());
        invoice.setUpdatedAt(LocalDate.now());

        invoiceRepository.save(invoice); // עדכון
    }
    /**
     * Deletes an invoice by its invoice number.
     *
     * @param invoiceNumber the invoice number
     */
    @Transactional
    public void deleteByFileName(String fileName){

        invoiceRepository.deleteByFileName(fileName);
    }
    /**
     * Retrieves the binary (PDF) file of an invoice.
     *
     * @param fileName the stored filename
     * @return byte array of the invoice file
     */
    @Transactional(readOnly = true)
    public byte[] getInvoiceFile(String fileName) {
        Invoice invoice = invoiceRepository.findByFileName(fileName)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return invoice.getFileData();
    }
    /**
     * Retrieves an invoice entity by its stored filename.
     *
     * @param filename the stored filename
     * @return the invoice entity, or null if not found
     */
    @Transactional(readOnly = true)
    public Invoice getInvoiceByName(String filename){

        return invoiceRepository.findByFileName(filename).orElse(null);
    }
    /**
     * Calculates the total of approved invoices for a client.
     *
     * @param clientId the client ID
     * @return an {@link InvoiceOutComeM} DTO with the total amount
     */
    public InvoiceOutComeM getInvoiceTotal(String clientId){

        return invoiceRepository.getOutcomeDto(clientId);
    }
}
