package com.fdmgroup.creditocube.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.model.DebitAccountTransaction;

/**
 * Repository for managing DebitAccountTransaction entities.
 *
 * @author timothy.chai
 */
@Repository
public interface DebitAccountTransactionRepository extends JpaRepository<DebitAccountTransaction, Long> {

	/**
	 * Finds all DebitAccountTransactions where the toAccount's accountId matches
	 * the provided accountId.
	 *
	 * @param accountId the id of the account to filter by
	 * @return a list of DebitAccountTransactions where the toAccount's accountId
	 *         matches the provided accountId
	 */
	@Query("SELECT d FROM DebitAccountTransaction d WHERE d.toAccountNumber LIKE :account_Number")
	public List<DebitAccountTransaction> findByToAccountNumber(@Param("account_Number") String accountNumber);

	/**
	 * Finds all DebitAccountTransactions where the fromAccount's accountId matches
	 * the provided accountId.
	 *
	 * @param accountId the id of the account to filter by
	 * @return a list of DebitAccountTransactions where the fromAccount's accountId
	 *         matches the provided accountId
	 */
	@Query("SELECT d FROM DebitAccountTransaction d WHERE d.fromAccount.accountId = :account_Id")
	public List<DebitAccountTransaction> findByFromAccount(@Param("account_Id") long accountId);

	@Query("SELECT d FROM DebitAccountTransaction d WHERE d.debitAccountTransactionDate >= :startDateTime AND d.debitAccountTransactionDate <= :endDateTime AND (d.fromAccount = :fromAccount OR d.toAccountNumber = :toAccountNumber)")
	List<DebitAccountTransaction> findByTransactionDate(@Param("startDateTime") Date startDateTime,
			@Param("endDateTime") Date endDateTime, @Param("fromAccount") DebitAccount fromAccount,
			@Param("toAccountNumber") String toAccountNumber);

	@Query("SELECT d FROM DebitAccountTransaction d WHERE MONTH(debitAccountTransactionDate) = :month AND d.fromAccount = :fromAccount")
	public List<DebitAccountTransaction> findTransactionsByMonth(@Param("month") int month,
			@Param("fromAccount") DebitAccount fromAccount);
}
