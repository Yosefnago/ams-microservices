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

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ClientService clientService;
    @Autowired
    public DocumentService(DocumentRepository documentRepository,ClientService clientService) {
        this.documentRepository = documentRepository;
        this.clientService = clientService;
    }

    public List<DocumentGrid> getAllDocumentsByClientId(String clientId){

        return documentRepository.findAllGridByClientId(clientId);

    }
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
    @Transactional
    public void deleteDocumentByDocId(String fileName){
        documentRepository.deleteByDocumentName(fileName);
    }
    @Transactional(readOnly = true)
    public Documents getDocumentByName(String filename){
        return documentRepository.findByDocumentName(filename).orElse(null);
    }
    @Transactional
    public void updateDocStatus(DocumentUpdateRequest request){
        documentRepository.updateStatus(request.documentName(),request.status(),request.rejectionReason());
    }
    public String getRejectedReason(String documentName){
        return documentRepository.getRejectedReason(documentName);
    }
    @Transactional
    public List<DocumentCareGridDto> getPendingDocumentsCareList(String accountantName) {
        return documentRepository.findPendingDocumentsCareList(accountantName);
    }

}
