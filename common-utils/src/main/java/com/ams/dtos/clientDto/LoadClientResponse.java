package com.ams.dtos.clientDto;

import java.util.List;

/**
 * {@code LoadClientResponse} is a Data Transfer Object used to return a list of clients
 * associated with a specific accountant or user.
 * <p>
 * Typically returned from the {@code /client/load-clients} endpoint, this DTO is used
 * to populate UI grids or lists with summarized client information.
 * </p>
 *
 * <p><b>Fields:</b></p>
 * <ul>
 *   <li>{@code success} – indicates whether the operation succeeded</li>
 *   <li>{@code message} – a readable message describing the result (e.g. "Clients loaded successfully")</li>
 *   <li>{@code clients} – a list of {@link ClientGridDto} objects representing each client’s basic data</li>
 * </ul>
 *
 * @param success indicates if the client list was successfully retrieved
 * @param message a message describing the operation result
 * @param clients a list of simplified client objects to display in UI tables or summaries
 */
public record LoadClientResponse(boolean success, String message, List<ClientGridDto> clients) {}
