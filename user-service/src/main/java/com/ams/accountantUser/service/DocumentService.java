package com.ams.accountantUser.service;

import com.ams.accountantUser.repository.DocumentRepository;
import com.ams.dtos.documentDto.DocumentGrid;
import com.ams.dtos.documentDto.DocumentUploadRequest;
import com.ams.entity.Documents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * DocumentService handles the business logic related to document management,
 * including upload, status updates, fetching lists for client and accountant views,
 * and accessing binary document data.
 *
 */
@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
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
    public void deleteDocumentByDocName(String fileName){
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


}