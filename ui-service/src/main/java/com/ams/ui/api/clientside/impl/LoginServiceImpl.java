package com.ams.ui.api.clientside.impl;


import com.ams.dtos.loginDto.ClientLoginRequest;
import com.ams.dtos.loginDto.ClientLoginResponse;
import com.ams.dtos.loginDto.LoginRequest;
import com.ams.dtos.loginDto.LoginResponse;
import com.ams.ui.api.clientside.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;


@Service
public class LoginServiceImpl implements LoginService {

    private final RestTemplate restTemplate;


    @Autowired
    public LoginServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public LoginResponse loginAccountant(LoginRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<LoginResponse> response =
                    restTemplate.postForEntity("http://localhost:8080/auth/login", entity, LoginResponse.class);

            if(response.getStatusCode().is2xxSuccessful()){
                return response.getBody();
            }
        }catch (HttpStatusCodeException e){
            String errorMessage = e.getResponseBodyAsString();
            return new LoginResponse(false, errorMessage,null);
        }
        return null;
    }

    @Override
    public ClientLoginResponse clientLogin(ClientLoginRequest request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClientLoginRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ClientLoginResponse> response =
                restTemplate.postForEntity("http://localhost:8080/client/login", entity, ClientLoginResponse.class);

        return response.getBody();
    }

}
