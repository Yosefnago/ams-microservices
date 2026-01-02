package com.ams.dtos.invoiceDto;


import java.time.LocalDate;

public record InvoiceIncomeDto(
        String invoiceId,              // מספר חשבונית
        LocalDate invoiceDate,         // תאריך חשבונית
        String businessName,           // שם העסק
        String businessId,             // ח.פ / ת.ז של העסק
        String customerName,           // שם לקוח
        String customerId,             // ת.ז / ח.פ לקוח
        String description,            // פירוט השירות/המוצר
        Double unitPrice,              // מחיר יחידה
        Double totalPriceBeforeVat,    // מחיר לפני מע״מ
        Double vatPercent,             // אחוז מע״מ
        Double vatAmount,              // סכום מע״מ
        Double totalPriceAfterVat,     // מחיר אחרי מע״מ
        LocalDate dueDate,             // תאריך יעד לתשלום
        LocalDate paymentDate          // תאריך תשלום בפועל (אם שולם)
) {
}
