package com.ams.accountantUser.entity;



import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code AccountantUser} represents a registered accountant user in the system.
 * <p>
 * This entity stores personal and authentication-related information such as
 * name, username, hashed password, contact details, and unique ID.
 * </p>
 *
 * <p><b>Constraints:</b></p>
 * <ul>
 *     <li>Username must be unique</li>
 *     <li>Email must be unique</li>
 *     <li>Phone number must be unique</li>
 * </ul>
 *
 * <p>This entity is persisted in the {@code accountant_Users} table.</p>
 *
 * @author Yosef Nago
 * @see com.ams.accountantUser.reposiroty.AccountantUserRepository
 */
@Table(name = "accountant_Users")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class AccountantUser {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "First_Name",nullable = false,length = 25)
    private String firstName;

    @Column(name = "Last_Name",nullable = false,length = 25)
    private String lastName;

    @Column(name = "Username",nullable = false,length = 25,unique=true)
    private String username;

    @Column(name = "Password",nullable = false)
    private String password;

    @Column(name = "Email",unique = true)
    private String email;

    @Column(name = "Phone",unique = true)
    private String phone;


}
