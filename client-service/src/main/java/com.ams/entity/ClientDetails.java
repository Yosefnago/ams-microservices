package com.ams.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Represents a client entity in the AMS system.
 * This entity stores all the necessary information about a client.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "client_details")
public class ClientDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Business_name", nullable = false)
    private String businessName; // שם העסק או החברה

    @Column(name = "accountant_name")
    private String accountantName; // מזהה רואה חשבון

    @Column(name = "client_id")
    private String clientId; // תעוזת זהות / ח.פ

    @Column(name = "Email", nullable = false)
    private String email; // כתובת אימייל ליצירת קשר

    @Column(name = "Phone")
    private String phone; // טלפון ראשי

    @Column(name = "Contact_phone")
    private String contactPhone;

    @Column(name = "Address")
    private String address; // כתובת מלאה

    @Column(name = "Zip_code")
    private String zip; // מיקוד

    @Column(name = "business_type")
    private String businessType;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_branch")
    private String bankBranch;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;


    @Column(name = "account_owner_name")
    private String accountOwnerName;



}
