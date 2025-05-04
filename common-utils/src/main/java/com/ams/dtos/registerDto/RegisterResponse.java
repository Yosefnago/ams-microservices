package com.ams.dtos.registerDto;

/**
 * Data Transfer Object for the response sent after attempting to register a new user.
 * It contains a success status and a message describing the result of the registration attempt.
 *
 */
public record RegisterResponse(boolean success, String message) {}
