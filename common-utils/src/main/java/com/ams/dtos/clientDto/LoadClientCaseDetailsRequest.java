package com.ams.dtos.clientDto;

/**
 * {@code LoadClientCaseDetailsRequest} is a data transfer object (DTO)
 * used to encapsulate the full editable case details of a specific client.
 *
 * <p>
 * This DTO is typically used when loading client data for editing in the UI,
 * or when returning the existing details of a client case from the backend service.
 * </p>
 *
 * <p>
 * Fields include business, contact, and banking information, as well as identifiers.
 * </p>
 *
 * @param clientId           the unique identifier of the client (tax ID)
 * @param businessName       the name of the client’s business
 * @param email              the email address of the client
 * @param phone              the phone number of the client
 * @param address            the street address of the client
 * @param zip                the ZIP/postal code of the client
 * @param businessType       the type/category of the client’s business
 * @param bankName           the name of the client’s bank
 * @param bankBranch         the branch name or code of the bank
 * @param bankAccountNumber  the client's bank account number
 */
public record LoadClientCaseDetailsRequest(
        String clientId,
        String businessName,
        String email,
        String phone,
        String address,
        String zip,
        String businessType,
        String bankName,
        String bankBranch,
        String bankAccountNumber) {
}
