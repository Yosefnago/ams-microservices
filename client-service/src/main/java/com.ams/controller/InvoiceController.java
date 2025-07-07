package com.ams.controller;

import com.ams.dtos.invoiceDto.*;
import com.ams.entity.Invoice;
import com.ams.service.ClientService;
import com.ams.service.InvoiceOCRExtractor;
import com.ams.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * InvoiceController exposes REST API endpoints for managing invoice files.
 *
 * Functionality includes:
 * - Uploading invoice PDFs
 * - Extracting data using OCR
 * - Viewing and deleting invoices
 * - Updating invoice status (approved/rejected)
 * - Fetching invoice statistics
 */
@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    private final InvoiceOCRExtractor invoiceOCRExtractor;
    private final InvoiceService invoiceService;
    private final ClientService clientService;

    public InvoiceController(InvoiceOCRExtractor invoiceOCRExtractor, InvoiceService invoiceService,ClientService clientService) {
        this.invoiceOCRExtractor = invoiceOCRExtractor;
        this.invoiceService = invoiceService;
        this.clientService = clientService;
    }
    /**
     * Loads all invoices belonging to a specific client.
     *
     * @param clientId the client ID (from request header)
     * @return list of invoices in grid format
     */
    @GetMapping("/load-invoices")
    public ResponseEntity<LoadInvoicesResponse> loadInvoices(@RequestHeader("clientId") String clientId){
        try {
            List<InvoiceGrid> invoiceGrid = invoiceService.getAllInvoices(clientId);
            return ResponseEntity.ok(new LoadInvoicesResponse(true, "מסמכים נטענו", invoiceGrid));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new LoadInvoicesResponse(false, "שגיאה ", List.of()));
        }
    }

    /**
     * Deletes an invoice by its invoice number.
     *
     * @param invoiceNumber the invoice number to delete
     * @return HTTP 200 OK on success
     */
    @DeleteMapping("/delete-invoice/{invoiceNumber}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable String invoiceNumber){
        invoiceService.deleteInvoiceByInvoiceNumber(invoiceNumber);
        return ResponseEntity.ok().build();
    }

    /**
     * Uploads an invoice file (PDF or image) for a specific client.
     *
     * @param file           the invoice file (MultipartFile)
     * @param clientId       client ID to associate the invoice with
     * @param status         initial status (e.g., "ממתין")
     * @param uploadedAtStr  the upload date in ISO format
     * @return success/failure response message
     */
    @PostMapping("/upload-invoice")
    public ResponseEntity<InvoiceUploadResponse> uploadInvoice(
            @RequestPart("file") MultipartFile file,
            @RequestParam("clientId") String clientId,
            @RequestParam("status") String status,
            @RequestParam("uploadedAt") String uploadedAtStr) {

        try {
            if (!clientService.existsClientById(clientId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new InvoiceUploadResponse(false, "לקוח לא נמצא במערכת"));
            }

            Invoice invoice = new Invoice();
            invoice.setFileName(file.getOriginalFilename());
            invoice.setFileData(file.getBytes());
            invoice.setClientId(clientId);
            invoice.setStatus(status);
            invoice.setCreatedAt(LocalDate.now());
            invoice.setUpdatedAt(LocalDate.now());

            invoiceService.saveInvoice(invoice);

            return ResponseEntity.ok(new InvoiceUploadResponse(true, "קובץ נשמר במסד הנתונים"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new InvoiceUploadResponse(false, "שגיאה בהעלאת הקובץ"));
        }
    }
    /**
     * Performs OCR (Optical Character Recognition) analysis on an uploaded invoice file.
     * Extracts invoice number, supplier name, price, VAT, etc.
     *
     * @param fileName the name of the invoice file
     * @return extracted invoice data as {@link InvoiceGridDto}
     */
    @GetMapping("/analyze-invoice/{fileName}")
    public ResponseEntity<InvoiceGridDto> analyzeInvoice(@PathVariable String fileName) {
        try {
            byte[] file = invoiceService.getInvoiceFile(fileName);

            if (file == null || file.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Invoice invoice = invoiceOCRExtractor.extractInvoiceFromImage(file);

            InvoiceGridDto dto = new InvoiceGridDto(
                    invoice.getFileName() != null ? invoice.getFileName() : "",
                    invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : "",
                    invoice.getSapakName() != null ? invoice.getSapakName() : "",
                    invoice.getPrice() != null ? invoice.getPrice() : BigDecimal.ZERO,
                    invoice.getPriceBeforeVat() != null ? invoice.getPriceBeforeVat() : BigDecimal.ZERO,
                    invoice.getPriceAfterVat() != null ? invoice.getPriceAfterVat() : BigDecimal.ZERO,
                    invoice.getVatAmount() != null ? invoice.getVatAmount() : BigDecimal.ZERO,
                    invoice.getUpdatedAt() != null ? invoice.getUpdatedAt() : LocalDate.now(),
                    invoice.getClientId() != null ? invoice.getClientId() : String.valueOf(0L),
                    "ממתין"
            );

            return ResponseEntity.ok(dto);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
     * Retrieves the binary (PDF) content of an invoice for preview or download.
     *
     * @param fileName the name of the invoice file
     * @return PDF file as byte array
     */
    @GetMapping("/get-invoice/{fileName}")
    public ResponseEntity<byte[]> getInvoice(@PathVariable String fileName) {
        Invoice invoice = invoiceService.getInvoiceByName(fileName);

        if (Objects.isNull(invoice) || Objects.isNull(invoice.getFileData())) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(invoice.getFileData());
    }
    /**
     * Returns the rejection reason for a specific invoice, if any.
     *
     * @param invoiceNumber the invoice number
     * @return rejection reason as plain text
     */
    @GetMapping("/get-invoice-rejectReason/{invoiceNumber}")
    public ResponseEntity<String> getRejectReasonInvoice(@PathVariable String invoiceNumber) {
        String reason = invoiceService.getRejectedReasonInvoice(invoiceNumber);

        return ResponseEntity.ok(reason);
    }
    /**
     * Updates the status of an invoice (approved, rejected, etc.), including optional rejection reason.
     *
     * @param invoiceUpdateRequest DTO with updated status info
     * @return HTTP 200 OK on success
     */
    @PutMapping("/update-invoice-status")
    public ResponseEntity<Void> updateInvoiceStatus(@RequestBody InvoiceUpdateRequest invoiceUpdateRequest) {
        invoiceService.updateInvoiceStatus(invoiceUpdateRequest);
        return ResponseEntity.ok().build();
    }
    /**
     * Retrieves total outcome summary from all invoices of a given client.
     *
     * @param clientId the client ID
     * @return summary totals including price, VAT, etc.
     */
    @GetMapping("/get-all-outcomes")
    public ResponseEntity<InvoiceOutComeM> getAllOutcomes(@RequestParam String clientId) {
        InvoiceOutComeM total = invoiceService.getInvoiceTotal(clientId);
        return ResponseEntity.ok(total);
    }
}
