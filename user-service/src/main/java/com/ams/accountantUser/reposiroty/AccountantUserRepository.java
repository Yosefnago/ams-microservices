package com.ams.accountantUser.reposiroty;

import com.ams.accountantUser.entity.AccountantUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link AccountantUser} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard data access methods,
 * and defines custom queries based on the accountant user's username or ID.
 * </p>
 *
 * <p><b>Usage:</b></p>
 * This interface is used by the service layer to interact with the database without the need to implement SQL logic.
 *
 * @author Yosef Nago
 * @see AccountantUser
 */
@Repository
public interface AccountantUserRepository extends JpaRepository<AccountantUser, Long> {

   /**
    * Finds an accountant user by their username.
    *
    * @param username the unique username of the accountant user
    * @return an {@link Optional} containing the user if found, otherwise {@link Optional#empty()}
    */
   Optional<AccountantUser> findByUsername(String username);

   /**
    * Finds an accountant user by their unique ID.
    *
    * @param id the ID of the accountant user
    * @return an {@link Optional} containing the user if found, otherwise {@link Optional#empty()}
    */
   Optional<AccountantUser> findById(Long id);

   /**
    * Deletes an accountant user by their username.
    *
    * @param username the username of the accountant user to delete
    */
   void deleteByUsername(String username);

   /**
    * Checks if an accountant user with the given username exists.
    *
    * @param username the username to check for existence
    * @return {@code true} if a user with the specified username exists, otherwise {@code false}
    */
   boolean existsByUsername(String username);
}