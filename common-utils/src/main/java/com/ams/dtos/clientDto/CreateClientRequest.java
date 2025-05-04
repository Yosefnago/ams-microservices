package com.ams.dtos.clientDto;

/**
 * {@code CreateClientRequest} is a Data Transfer Object used for receiving client registration data
 * from the user interface or external API calls.
 * <p>
 * This DTO encapsulates all required information to create a new {@code ClientDetails} entity in the system.
 * It includes business details, tax identification, contact information, and bank details.
 * </p>
 *
 * <p><b>Fields:</b></p>
 * <ul>
 *   <li>{@code email} – the client's contact email</li>
 *   <li>{@code phone} – the client's phone number</li>
 *   <li>{@code address} – the physical address of the business/client</li>
 *   <li>{@code zip} – the ZIP/postal code of the client's address</li>
 *   <li>{@code businessName} – the official name of the client or their business</li>
 *   <li>{@code clientType} – the type of client (e.g., עוסק מורשה, עוסק פטור, חברה בע"מ)</li>
 *   <li>{@code tax_id} – the client's unique tax identification number (ת.ז / ח.פ)</li>
 *   <li>{@code bankOwnerName} – the name of the account holder</li>
 *   <li>{@code bankName} – the name of the client's bank</li>
 *   <li>{@code bankBranch} – the branch code of the bank</li>
 *   <li>{@code bankNumber} – the client's bank account number</li>
 *   <li>{@code token} – JWT token of the currently logged-in accountant creating the client</li>
 * </ul>
 *
 * <p>This record is typically used in {@code @PostMapping} endpoints in the {@link com.ams.controller.ClientController}.</p>
 *
 * @param email          the client's email address
 * @param phone          the client's phone number
 * @param address        the client's physical address
 * @param zip            the ZIP code
 * @param businessName   the name of the business/client
 * @param clientType     the classification type of the client
 * @param tax_id         the client's tax identification number
 * @param bankOwnerName  the name of the account owner
 * @param bankName       the name of the client's bank
 * @param bankBranch     the bank branch code
 * @param bankNumber     the client's bank account number
 * @param token          JWT token used to associate the created client with the authenticated accountant
 */
public record CreateClientRequest(
        String email,
        String phone,
        String address,
        String zip,
        String businessName,
        String clientType,
        String tax_id,
        String bankOwnerName,
        String bankName,
        String bankBranch,
        String bankNumber,
        String token
        ){
}
