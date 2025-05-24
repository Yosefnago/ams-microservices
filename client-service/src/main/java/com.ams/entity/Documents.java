package com.ams.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "documents")
@Data
public class Documents {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "document_name")
    public String documentName;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file_data")
    public byte[] fileData;

    @Column(name = "client_id")
    public String clientId;

    @Column(name = "status")
    public String status;

    @Column(name = "uploadedAt")
    public LocalDate uploadedAt;


}
