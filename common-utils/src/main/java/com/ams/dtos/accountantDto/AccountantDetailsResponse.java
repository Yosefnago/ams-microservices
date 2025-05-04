package com.ams.dtos.accountantDto;

/**
 * {@code AccountantDetailsResponse} is a data transfer object (DTO)
 * used to encapsulate detailed information about an accountant.
 *
 * <p>
 * This DTO is typically returned from API endpoints that fetch accountant profile data.
 * </p>
 *
 * @param success   indicates whether the request was successful
 * @param message   a descriptive message (e.g., success confirmation or error description)
 * @param username  the accountant's system username
 * @param email     the accountant's email address
 * @param phone     the accountant's phone number
 * @param id        the unique database identifier of the accountant
 */
public record AccountantDetailsResponse (boolean success,String message,String username, String email,String phone,Long id){
}
