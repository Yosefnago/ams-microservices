package com.ams.dtos.accountantDto;


/**
 * {@code AccountantUserDto} is a Data Transfer Object representing an accountant user in the system.
 * <p>
 * It is used for transmitting lightweight user-related data such as username and email between layers
 * (e.g., from service to UI or between microservices).
 * </p>
 *
 * <p><b>Fields:</b></p>
 * <ul>
 *   <li>{@code username} – the unique identifier of the accountant (usually the login name)</li>
 *   <li>{@code email} – the contact email address of the accountant</li>
 * </ul>
 *
 * <p>This DTO is immutable and implemented as a {@code record}, introduced in Java 14+.</p>
 *
 * @param username the accountant’s system username
 * @param email    the accountant’s email address
 */
public record AccountantUserDto(String username,String email) {
}
