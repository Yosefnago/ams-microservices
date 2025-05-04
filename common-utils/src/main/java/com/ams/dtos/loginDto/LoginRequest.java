package com.ams.dtos.loginDto;

/**
 * A record that represents a request for user authentication.
 * This record is used to encapsulate the username and password provided by a user
 * when attempting to log in to the application.
 *
 * It serves as a data transfer object (DTO) between the client-side and the server-side,
 * ensuring that the login credentials are passed in a structured and type-safe manner.
 *
 * @param username The username of the user trying to authenticate. This is typically
 *                 a unique identifier for the user within the system.
 * @param password The password for the user. This should be handled securely throughout
 *                 the authentication process to prevent exposure of sensitive information.
 */
public record LoginRequest(String username, String password) { }
