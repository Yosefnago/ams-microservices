package com.ams.ui.api.clientside.impl;

import com.ams.dtos.clientDto.*;
import com.ams.ui.api.clientside.ClientHttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ClientServiceImpl implements ClientHttpService {

    private final RestTemplate restTemplate;

    @Autowired
    public ClientServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public LoadNumOfClientsResponse loadNumOfClients(String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<LoadNumOfClientsResponse> response = restTemplate.exchange(
                "http://localhost:8080/client/load-numOfclients",
                HttpMethod.GET,
                entity,
                LoadNumOfClientsResponse.class);

        return response.getBody();

    }
    @Override
    public GrantAccessResponse grantAccess(GrantAccessRequestDto request, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<GrantAccessRequestDto> entity = new HttpEntity<>(request, headers);

        String url = "http://localhost:8080/client/grant-access";

        try {
            ResponseEntity<GrantAccessResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, GrantAccessResponse.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        }catch (HttpClientErrorException e){
            String message = e.getResponseBodyAsString();
            return new GrantAccessResponse(false,message);
        }
        return null;
    }
    @Override
    public LoadClientDetailsCaseResponse loadClientDetails(String token, String clientId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = "http://localhost:8080/client/load-case-details?clientId=" + clientId;

        ResponseEntity<LoadClientDetailsCaseResponse> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, LoadClientDetailsCaseResponse.class);

        return response.getBody();
    }
}
