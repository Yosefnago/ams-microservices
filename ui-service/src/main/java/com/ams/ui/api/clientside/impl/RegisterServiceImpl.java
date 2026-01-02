package com.ams.ui.api.clientside.impl;

import com.ams.dtos.registerDto.RegisterRequest;
import com.ams.dtos.registerDto.RegisterResponse;
import com.ams.ui.api.clientside.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final RestTemplate restTemplate;


    @Autowired
    public RegisterServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<RegisterRequest> entity = new HttpEntity<>(registerRequest, headers);

            ResponseEntity<RegisterResponse> response = restTemplate.postForEntity(
                    "http://localhost:8085/auth/register",
                    entity,
                    RegisterResponse.class
            );
            return response.getBody();
    }
}
