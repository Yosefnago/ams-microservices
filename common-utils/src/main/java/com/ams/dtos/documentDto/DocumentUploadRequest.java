package com.ams.dtos.documentDto;

import java.time.LocalDate;

public record DocumentUploadRequest(
        String documentName,
        byte[] fileData,
        String clientId,
        String status,
        LocalDate uploadedAt

) {
}
