package com.ams.dtos.documentDto;

public record DocumentUpdateRequest(String documentName, String status,String rejectionReason) {
}
