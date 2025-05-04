package com.ams.dtos.clientDto;

/**
 * {@code CreateClientResponse} is a simple Data Transfer Object used to communicate the result
 * of a client creation operation back to the client (UI or API consumer).
 * <p>
 * It contains a boolean status flag indicating success or failure, and a descriptive message.
 * </p>
 *
 * <p>Typical usage: return from {@code @PostMapping("/client/create")} endpoint.</p>
 *
 * @param success {@code true} if the client was successfully created, {@code false} otherwise
 * @param message a human-readable message describing the outcome of the operation
 */
public record CreateClientResponse(boolean success, String message) {
}
