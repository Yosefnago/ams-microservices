package com.ams.dtos.documentDto;


import java.util.List;
public record LoadDocumentsResponse(boolean success, String message, List<DocumentGrid> documentGrids) {
}
