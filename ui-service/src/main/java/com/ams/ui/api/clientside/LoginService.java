package com.ams.ui.api.clientside;


import com.ams.dtos.loginDto.ClientLoginRequest;
import com.ams.dtos.loginDto.ClientLoginResponse;
import com.ams.dtos.loginDto.LoginRequest;
import com.ams.dtos.loginDto.LoginResponse;

public interface LoginService {

    LoginResponse loginAccountant(LoginRequest request);
    ClientLoginResponse clientLogin(ClientLoginRequest request);
}
