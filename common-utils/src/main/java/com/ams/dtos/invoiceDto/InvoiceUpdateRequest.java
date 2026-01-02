package com.ams.dtos.invoiceDto;


import java.math.BigDecimal;

public record InvoiceUpdateRequest(
        String fileName,
        String status,
        String rejectionReason,
        String invoiceNumber,
        String sapakName,
        BigDecimal price
) {}