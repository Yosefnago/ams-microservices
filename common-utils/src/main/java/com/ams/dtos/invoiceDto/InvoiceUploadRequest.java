package com.ams.dtos.invoiceDto;


import java.time.LocalDate;

public record InvoiceUploadRequest(
        String invoiceNumber,
        byte[] fileData,
        String clientId,
        String status,
        LocalDate uploadedAt
) {
}
