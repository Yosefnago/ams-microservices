package com.ams.dtos.invoiceDto;


import java.util.List;

public record LoadInvoicesResponse(boolean success, String message, List<InvoiceGrid> invoiceGrid) {
}
