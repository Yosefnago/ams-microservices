package com.ams.dtos.invoiceDto;


import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceGridDto(String fileName,
                             String invoiceNumber,
                             String sapakName ,
                             BigDecimal price ,
                             BigDecimal priceBeforeVat,
                             BigDecimal priceAfterVat,
                             BigDecimal vatAmount,
                             LocalDate uploadedAt,
                             String clientId,
                             String status) {
}
