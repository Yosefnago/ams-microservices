package com.ams.ui.api.clientside.impl;

import com.ams.ui.api.clientside.DocumentHttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DocumentServiceImpl implements DocumentHttpService {

    private final RestTemplate restTemplate;

    public DocumentServiceImpl(@Autowired RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Integer loadNumOfDocuments(String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Integer> response = restTemplate.exchange(
                "http://localhost:8080/document/loadNumOfDocuments",
                HttpMethod.GET,
                entity,
                Integer.class
        );

        return response.getBody();
    }

}
