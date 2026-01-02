package com.ams.accountantUser.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoice_incomes")
@Data
public class InvoiceIncomes {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "invoice_id")
    private long invoiceId;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "buss_name")
    private String ownerName;

    @Column(name = "buss_id")
    private String ownerId;

    @Column(name = "invoice_to_id")
    private String invoice_to_id;

    @Column(name = "invoice_to_name")
    private String invoice_to_name;

    @Column(name = "description")
    private String invoice_description;

    @Column(name = "price_unit")
    private String price_unit;

    @Column(name = "total_price_before_vat")
    private String total_price_before_vat;

    @Column(name = "total_price_after_vat")
    private BigDecimal total_price_after_vat;

    @Column(name = "vat_amount")
    private String vat_amount;

    @Column(name = "vat")
    private String vat;

    @Column(name = "date_to_pay")
    private LocalDate date_to_pay;

    @Column(name = "date_to_refund")
    private LocalDate date_to_refund;


}
