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
    public LoadClientDetailsCaseResponse loadClientDetails(String token, String clientId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = "http://localhost:8085/user/load-case-details?clientId=" + clientId;

        ResponseEntity<LoadClientDetailsCaseResponse> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, LoadClientDetailsCaseResponse.class);

        return response.getBody();
    }
}
