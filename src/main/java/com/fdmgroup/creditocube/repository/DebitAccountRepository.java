package com.fdmgroup.creditocube.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fdmgroup.creditocube.model.DebitAccount;

/**
 * This interface provides methods to interact with the database and retrieve
 * information about DebitAccount entities.
 * 
 * @author timothy.chai
 *
 */
public interface DebitAccountRepository extends JpaRepository<DebitAccount, Long> {

	/**
	 * This method finds a DebitAccount entity based on the account number.
	 * 
	 * @param accountNumber the account number of the DebitAccount entity to be
	 *                      found
	 * @return an Optional object containing the DebitAccount entity if found, or an
	 *         empty Optional if not found
	 */
	public Optional<DebitAccount> findByAccountNumber(String accountNumber);

}
