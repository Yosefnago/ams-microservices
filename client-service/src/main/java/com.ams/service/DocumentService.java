package com.ams.service;

import com.ams.dtos.documentDto.DocumentGrid;
import com.ams.dtos.documentDto.DocumentUploadRequest;
import com.ams.entity.Documents;
import com.ams.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<DocumentGrid> getAllDocumentsByClientId(String clientId){

        return documentRepository.findAllGridByClientId(clientId);

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
}
