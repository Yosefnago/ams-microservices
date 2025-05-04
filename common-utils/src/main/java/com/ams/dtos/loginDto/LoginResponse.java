package com.ams.dtos.loginDto;

/**
 * A record that represents the response sent back to the client after an authentication attempt.
 * This record provides feedback on the success or failure of a login attempt, and in the case of success,
 * includes a token that can be used for accessing authenticated services.
 *
 * @param success A boolean indicating whether the login attempt was successful.
 *                True means the user was authenticated successfully; false means the authentication failed.
 * @param message A human-readable message providing more details about the outcome of the login attempt.
 *                This can include error messages in case of failure or general information messages.
 * @param token   A JWT (JSON Web Token) or similar security token that the client can use to make authenticated
 *                requests to the server. This token is only provided if the login is successful.
 */
public record LoginResponse(boolean success, String message, String token) {
}