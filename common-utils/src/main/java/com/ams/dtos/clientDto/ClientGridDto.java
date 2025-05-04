package com.ams.dtos.clientDto;

/**
 * {@code ClientGridDto} is a lightweight Data Transfer Object used for displaying basic client information
 * in tabular views such as dashboards or grids.
 * <p>
 * This record is optimized for read-only views and provides only the essential fields
 * required for listing clients in summary format.
 * </p>
 *
 * <p><b>Fields:</b></p>
 * <ul>
 *   <li>{@code businessName} – the name of the client's business or individual</li>
 *   <li>{@code clientId} – the client's unique identifier (tax ID / ת.ז / ח.פ)</li>
 *   <li>{@code email} – the client's email address</li>
 *   <li>{@code phone} – the client's primary phone number</li>
 * </ul>
 *
 * <p>This DTO is typically used in UI layers such as Vaadin {@code Grid}, or in REST responses that
 * return client listings without full details.</p>
 *
 * @param businessName the name of the business or client
 * @param clientId     the unique tax identifier of the client
 * @param email        the client’s email address
 * @param phone        the client’s contact phone number
 */
public record ClientGridDto(String businessName,String clientId,String email,String phone) {
}
