package com.ams.dtos.clientDto;

/**
 * {@code LoadNumOfClientsResponse} is a Data Transfer Object used to return the total number of clients
 * associated with a specific accountant or user.
 * <p>
 * Typically used in summary dashboards or reporting views to show how many clients are linked
 * to the authenticated accountant.
 * </p>
 *
 * <p><b>Fields:</b></p>
 * <ul>
 *   <li>{@code success} – indicates if the operation was successful</li>
 *   <li>{@code message} – descriptive message about the result (e.g., "Number of clients loaded")</li>
 *   <li>{@code numOfClients} – the actual number of clients associated with the user</li>
 * </ul>
 *
 * @param success       whether the count retrieval operation was successful
 * @param message       a message describing the outcome of the operation
 * @param numOfClients  the total number of clients found
 */
public record LoadNumOfClientsResponse(boolean success, String message,int numOfClients) {
}
