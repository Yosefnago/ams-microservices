package com.ams.dtos.invoiceDto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceGrid(String fileName,
                          String invoiceNumber,
                          String sapakName ,
                          BigDecimal price ,
                          LocalDate uploadedAt,
                          String clientId,
                          String status) {
}
