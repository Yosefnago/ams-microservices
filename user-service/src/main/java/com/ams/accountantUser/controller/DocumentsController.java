package com.ams.accountantUser.controller;

import com.ams.accountantUser.service.AccountantUserService;
import com.ams.accountantUser.service.DocumentService;
import com.ams.commonsecurity.utils.JwtUtil;
import com.ams.dtos.documentDto.DocumentGrid;
import com.ams.dtos.documentDto.DocumentUploadRequest;
import com.ams.dtos.documentDto.DocumentUploadResponse;
import com.ams.dtos.documentDto.LoadDocumentsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/document")
public class DocumentsController {

    private final AccountantUserService accountantUserService;
    private final DocumentService documentService;
    private final JwtUtil jwtUtil;

    public DocumentsController(@Autowired AccountantUserService accountantUserService, DocumentService documentService, JwtUtil jwtUtil) {
        this.accountantUserService = accountantUserService;
        this.jwtUtil = jwtUtil;
        this.documentService = documentService;
    }
    /**
     * Loads all documents for a specific client.
     *
     * @param clientId the ID of the client (from request header)
     * @return list of documents for the client wrapped in LoadDocumentsResponse
     */
    @GetMapping("/load-documents")
    public ResponseEntity<LoadDocumentsResponse> loadDocuments(@RequestHeader("clientId") String clientId){
        try {
            List<DocumentGrid> documentGrids = documentService.getAllDocumentsByClientId(clientId);
            return ResponseEntity.ok(new LoadDocumentsResponse(true, "מסמכים נטענו", documentGrids));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new LoadDocumentsResponse(false, "שגיאה ", List.of()));
        }
    }

    /**
     * Uploads a new document to the system using multipart/form-data.
     * Validates the client exists before saving.
     *
     * @param file         the file to upload
     * @param clientId     the client ID associated with the document
     * @param status       initial status of the document (e.g., "ממתין לטיפול")
     * @param uploadedAtStr the upload date in ISO format (yyyy-MM-dd)
     * @return upload success/failure message wrapped in DocumentUploadResponse
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentUploadResponse> uploadDocument(
            @RequestPart("file") MultipartFile file,
            @RequestParam("clientId") String clientId,
            @RequestParam("status") String status,
            @RequestParam("uploadedAt") String uploadedAtStr){

        try {

            DocumentUploadRequest request = new DocumentUploadRequest(
                    file.getOriginalFilename(),
                    file.getBytes(),
                    clientId,
                    LocalDate.parse(uploadedAtStr)
            );

            documentService.saveDocument(request);

            return ResponseEntity.ok(new DocumentUploadResponse(true, "ההעלאה בוצעה"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DocumentUploadResponse(false, "שגיאה בהעלאת קובץ"));
        }

    }
    /**
     * Retrieves the binary content of a document by its name (PDF format).
     *
     * @param documentName the name of the document
     * @return byte[] of the PDF file if found
     */
    @GetMapping("/get-document/{documentName}")
    public ResponseEntity<byte[]> getDocument(@PathVariable String documentName) {
        com.ams.entity.Documents document = documentService.getDocumentByName(documentName);

        if (Objects.isNull(document) || Objects.isNull(document.getFileData())) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(document.getFileData());
    }
    @DeleteMapping("/delete-document/{fileName}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable String fileName){
        documentService.deleteDocumentByDocName(fileName);
        return ResponseEntity.ok().build();
    }
}
