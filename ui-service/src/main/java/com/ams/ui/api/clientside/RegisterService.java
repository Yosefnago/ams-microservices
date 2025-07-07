package com.ams.ui.api.clientside;

import com.ams.dtos.registerDto.RegisterRequest;
import com.ams.dtos.registerDto.RegisterResponse;

public interface RegisterService {
    RegisterResponse register(RegisterRequest registerRequest);
}
