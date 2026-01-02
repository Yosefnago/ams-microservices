package com.ams.dtos.invoiceDto;


import java.math.BigDecimal;

public record InvoiceIncomeNumber(String clientId, BigDecimal price) {
}
