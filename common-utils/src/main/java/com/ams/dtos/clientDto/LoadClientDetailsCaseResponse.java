package com.ams.dtos.clientDto;

/**
 * {@code LoadClientDetailsCaseResponse} is a Data Transfer Object representing the full details
 * of a client case for display in a detailed view or case management screen.
 * <p>
 * This DTO is typically returned from the {@code /client/load-case-details} endpoint and used to
 * populate the UI when a specific client is selected.
 * </p>
 *
 * <p><b>Fields:</b></p>
 * <ul>
 *   <li>{@code success} – indicates if the operation to load client details was successful</li>
 *   <li>{@code message} – descriptive message (e.g., "Client loaded successfully" or "Client not found")</li>
 *   <li>{@code businessName} – the name of the client's business</li>
 *   <li>{@code clientId} – the client's unique tax ID (ת.ז / ח.פ)</li>
 *   <li>{@code email} – the client's email address</li>
 *   <li>{@code phone} – the client's contact number</li>
 *   <li>{@code address} – the client's physical address</li>
 *   <li>{@code businessType} – the type of business entity (e.g., עוסק מורשה, חברה בע"מ)</li>
 * </ul>
 *
 * @param success       whether the client data was successfully retrieved
 * @param message       a message describing the outcome of the request
 * @param businessName  the client's business name
 * @param clientId      the client's tax identifier
 * @param email         the client's email address
 * @param phone         the client's phone number
 * @param address       the client's physical address
 * @param businessType  the type of business (individual, registered business, etc.)
 */
public record LoadClientDetailsCaseResponse(
        boolean success,
        String message,
        String businessName,
        String clientId,
        String email,
        String phone,
        String address,
        String businessType
) {}
