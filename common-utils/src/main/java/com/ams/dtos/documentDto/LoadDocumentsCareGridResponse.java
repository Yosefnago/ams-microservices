package com.ams.dtos.documentDto;

import java.util.List;

public record LoadDocumentsCareGridResponse(boolean success, String message, List<DocumentCareGridDto> documentGrids) {
}
