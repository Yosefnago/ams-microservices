package com.ams.accountantUser.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file_data")
    public byte[] fileData;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "sapak_name")
    private String sapakName;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "price_before_vat")
    private BigDecimal priceBeforeVat;

    @Column(name = "vat_amount")
    private BigDecimal vatAmount;

    @Column(name = "price_after_vat")
    private BigDecimal priceAfterVat;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(name = "client_id")
    public String clientId;

    @Column(name = "reason")
    public String reason;

}