package com.ams.dtos.registerDto;

/**
 * Data Transfer Object for capturing registration details provided by a user.
 * This DTO contains all the necessary information required for registering a new accountant user.
 *
 */
public record RegisterRequest(String firstName,String lastName,String username, String email, String password, String phoneNumber) {}
