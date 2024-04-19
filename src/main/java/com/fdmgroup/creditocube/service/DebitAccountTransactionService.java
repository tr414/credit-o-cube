package com.fdmgroup.creditocube.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.model.DebitAccountTransaction;
import com.fdmgroup.creditocube.repository.DebitAccountRepository;
import com.fdmgroup.creditocube.repository.DebitAccountTransactionRepository;

/**
 * Service class for managing debit account transactions
 *
 * @author timothy.chai
 */
@Service
public class DebitAccountTransactionService {

	@Autowired
	private DebitAccountTransactionRepository debitAccountTransactionRepository;

	@Autowired
	private DebitAccountRepository debitAccountRepository;

	private static Logger logger = LogManager.getLogger(DebitAccountTransactionService.class);

	/**
	 * Constructor for DebitAccountTransactionService.
	 *
	 * @param debitAccountTransactionRepository the repository for debit account
	 *                                          transactions
	 */
	public DebitAccountTransactionService(DebitAccountTransactionRepository debitAccountTransactionRepository) {
		this.debitAccountTransactionRepository = debitAccountTransactionRepository;
	}

	/**
	 * Creates a new debit account transaction and saves it to the repository.
	 *
	 * @param debitAccountTransaction the debit account transaction to be created
	 */
	public void createDebitAccountTransaction(DebitAccountTransaction debitAccountTransaction) {
		debitAccountTransactionRepository.save(debitAccountTransaction);
	}

	/**
	 * Updates an existing debit account transaction and saves it to the repository.
	 *
	 * @param debitAccountTransaction the debit account transaction to be updated
	 */
	public void updateDebitAccountTransaction(DebitAccountTransaction debitAccountTransaction) {

		// Save the updated debit account transaction to the repository
		debitAccountTransactionRepository.save(debitAccountTransaction);
	}

	/**
	 * Finds all transactions related to the given debit account.
	 *
	 * @param account the debit account for which to find transactions
	 * @return a list of debit account transactions related to the given account
	 */
	public List<DebitAccountTransaction> findTransactionsOfAccount(DebitAccount account) {

		List<DebitAccountTransaction> relatedTransactions = new ArrayList<>();

		// check if account exists in database
		Optional<DebitAccount> optionalAccount = debitAccountRepository.findById(account.getAccountId());

		if (optionalAccount.isEmpty()) {
			return relatedTransactions;
		}

		DebitAccount targetAccount = optionalAccount.get();

		// get transactions where account is toAccount or fromAccount
		relatedTransactions.addAll(debitAccountTransactionRepository.findByToAccount(targetAccount.getAccountNumber()));
		relatedTransactions.addAll(debitAccountTransactionRepository.findByFromAccount(targetAccount.getAccountId()));

		// Sort the transactions by their transaction date
		relatedTransactions.sort(Comparator.comparing(DebitAccountTransaction::getDebitAccountTransactionDate));

		return relatedTransactions;
	}

}
