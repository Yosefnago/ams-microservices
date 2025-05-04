package com.ams.dtos.clientDto;

/**
 * {@code UpdateClientResponse} is a simple data transfer object (DTO)
 * representing the outcome of a client update operation.
 *
 * <p>
 * It typically contains a message indicating whether the update was successful or if any issue occurred.
 * </p>
 *
 * @param message a descriptive message about the result of the update (e.g., success or error details)
 */
public record UpdateClientResponse(String message) {}
