package com.fdmgroup.creditocube.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

	@Query("SELECT d FROM DebitAccount d WHERE d.customer.user_id = :userId AND d.isActive = true")
//	@Query(value = "SELECT * FROM `credit-o-cube`.debit_account d WHERE d.fk_user_id = 1"
//			+ "AND account_is_active = 1", nativeQuery = true)
	public List<DebitAccount> findByCustomer(@Param("userId") int userId);

}
