package com.ams.dtos.documentDto;

import java.time.LocalDate;

public record DocumentGrid(String fileName, String clientId,LocalDate uploadedAt, String status) {
}
