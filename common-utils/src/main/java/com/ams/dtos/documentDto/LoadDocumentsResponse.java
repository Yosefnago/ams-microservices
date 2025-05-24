package com.ams.dtos.documentDto;

import com.ams.dtos.clientDto.ClientGridDto;

import java.util.List;

public record LoadDocumentsResponse(boolean success, String message, List<DocumentGrid> documentGrids) {
}
