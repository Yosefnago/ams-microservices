package com.ams.service;

import com.ams.dtos.documentDto.DocumentCareGridDto;
import com.ams.dtos.documentDto.DocumentGrid;
import com.ams.dtos.documentDto.DocumentUpdateRequest;
import com.ams.dtos.documentDto.DocumentUploadRequest;
import com.ams.entity.ClientDetails;
import com.ams.entity.Documents;
import com.ams.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * DocumentService handles the business logic related to document management,
 * including upload, status updates, fetching lists for client and accountant views,
 * and accessing binary document data.
 *
 * This service coordinates between {@link DocumentRepository} and {@link ClientService}.
 */
@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ClientService clientService;
    @Autowired
    public DocumentService(DocumentRepository documentRepository,ClientService clientService) {
        this.documentRepository = documentRepository;
        this.clientService = clientService;
    }

    /**
     * Retrieves a list of documents for a given client,
     * projected as {@link DocumentGrid} for UI representation.
     *
     * @param clientId the client ID
     * @return list of document grid DTOs
     */
    public List<DocumentGrid> getAllDocumentsByClientId(String clientId){

        return documentRepository.findAllGridByClientId(clientId);
    }
    /**
     * Returns the number of documents with status "ממתין לטיפול"
     * for all clients managed by a specific accountant.
     *
     * @param accountantName the username of the accountant
     * @return number of pending documents
     */
    public int getNumOfPendingDocumentsByAccountantName(String accountantName) {
        List<ClientDetails> clients = clientService.getAllClientsByaccountantName(accountantName);

        if (clients.isEmpty()) {
            return 0;
        }

        List<String> clientIds = clients.stream()
                .map(ClientDetails::getClientId)
                .toList();

        return documentRepository.countByClientIdInAndStatus(clientIds, "ממתין לטיפול");
    }
    /**
     * Saves a new document to the database using a DTO request.
     *
     * @param request the document upload request DTO
     */
    public void saveDocument(DocumentUploadRequest request){

        try {
            Documents documents = new Documents();

            documents.setClientId(request.clientId());
            documents.setDocumentName(request.documentName());
            documents.setFileData(request.fileData());
            documents.setStatus(request.status());
            documents.setUploadedAt(request.uploadedAt());

            documentRepository.save(documents);
        }catch (Exception e){
            throw new IllegalStateException("Connection is bad..");
        }
    }
    /**
     * Deletes a document by its filename (document name).
     *
     * @param fileName the name of the document to delete
     */
    @Transactional
    public void deleteDocumentByDocId(String fileName){
        documentRepository.deleteByDocumentName(fileName);
    }
    /**
     * Retrieves a full document entity by its name.
     *
     * @param filename the document name
     * @return the document entity or null if not found
     */
    @Transactional(readOnly = true)
    public Documents getDocumentByName(String filename){
        return documentRepository.findByDocumentName(filename).orElse(null);
    }
    /**
     * Updates the status of a document (e.g. approved/rejected) and optionally its rejection reason.
     *
     * @param request the status update request DTO
     */
    @Transactional
    public void updateDocStatus(DocumentUpdateRequest request){
        documentRepository.updateStatus(request.documentName(),request.status(),request.rejectionReason());
    }
    /**
     * Retrieves the rejection reason of a document by its name.
     *
     * @param documentName the document name
     * @return reason string
     */
    public String getRejectedReason(String documentName){
        return documentRepository.getRejectedReason(documentName);
    }
    /**
     * Retrieves a list of documents that are pending for accountant review,
     * including the client's business name, document status, and upload date.
     *
     * @param accountantName the name of the accountant
     * @return list of {@link DocumentCareGridDto}
     */
    @Transactional
    public List<DocumentCareGridDto> getPendingDocumentsCareList(String accountantName) {
        return documentRepository.findPendingDocumentsCareList(accountantName);
    }

}
