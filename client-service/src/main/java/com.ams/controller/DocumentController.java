package com.ams.controller;

import com.ams.dtos.documentDto.*;
import com.ams.entity.Documents;
import com.ams.service.ClientService;
import com.ams.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * DocumentController handles all REST endpoints related to document management.
 *
 * This includes:
 * - Uploading documents
 * - Downloading/viewing documents
 * - Deleting documents
 * - Updating document statuses
 * - Loading documents for clients and accountants
 */
@RestController
@RequestMapping("/document")
public class DocumentController {

    private final DocumentService documentService;
    private final ClientService clientService;

    public DocumentController(DocumentService documentService, ClientService clientService) {
        this.documentService = documentService;
        this.clientService = clientService;
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
     * Deletes a document by its filename.
     *
     * @param fileName the name of the document file
     * @return HTTP 200 on success
     */
    @DeleteMapping("/delete-document/{fileName}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String fileName){
        documentService.deleteDocumentByDocId(fileName);
        return ResponseEntity.ok().build();
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
            if (!clientService.existsClientById(clientId)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new DocumentUploadResponse(false, "לקוח לא נמצא"));
            }

            DocumentUploadRequest request = new DocumentUploadRequest(
                    file.getOriginalFilename(),
                    file.getBytes(),
                    clientId,
                    status,
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
        Documents document = documentService.getDocumentByName(documentName);

        if (Objects.isNull(document) || Objects.isNull(document.getFileData())) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(document.getFileData());
    }
    /**
     * Returns the rejection reason for a given document.
     *
     * @param documentName the name of the document
     * @return rejection reason as a plain text string
     */
    @GetMapping("/get-document-rejectReason/{documentName}")
    public ResponseEntity<String> getRejectReason(@PathVariable String documentName) {
        String reason = documentService.getRejectedReason(documentName);

        return ResponseEntity.ok(reason);
    }
    /**
     * Updates the status of a document (e.g., approved/rejected).
     *
     * @param request the request object containing updated status and reason
     * @return HTTP 200 on success
     */
    @PutMapping("/update-document-status")
    public ResponseEntity<Void> updateStatus(@RequestBody DocumentUpdateRequest request) {
        documentService.updateDocStatus(request);
        return ResponseEntity.ok().build();
    }
    /**
     * Returns the number of pending documents assigned to a specific accountant.
     *
     * @param username the accountant's username (from header)
     * @return number of pending documents
     */
    @GetMapping("/loadNumOfDocuments")
    public ResponseEntity<Integer> loadNumOfDocs(@RequestHeader("X-User-Name") String username) {

        int count = documentService.getNumOfPendingDocumentsByAccountantName(username);

        return ResponseEntity.ok(count);
    }
    /**
     * Loads a list of documents pending accountant review.
     *
     * @param username the accountant's username (from header)
     * @return list of documents to be handled by the accountant
     */
    @GetMapping("/loadDocumentsCareList")
    public ResponseEntity<LoadDocumentsCareGridResponse> loadDocumentsCareList(@RequestHeader("X-User-Name") String username) {
        try {
            List<DocumentCareGridDto> careList = documentService.getPendingDocumentsCareList(username);
            return ResponseEntity.ok(new LoadDocumentsCareGridResponse(true, "מסמכים נטענו בהצלחה", careList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoadDocumentsCareGridResponse(false, "שגיאה בטעינת מסמכים", List.of()));
        }
    }
}
