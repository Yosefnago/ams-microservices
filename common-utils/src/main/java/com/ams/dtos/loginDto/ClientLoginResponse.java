package com.ams.dtos.loginDto;

public record ClientLoginResponse(boolean success, String message, String token, String clientId) {}

