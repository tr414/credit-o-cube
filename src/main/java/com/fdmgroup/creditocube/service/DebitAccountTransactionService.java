package com.fdmgroup.creditocube.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.model.DebitAccountTransaction;
import com.fdmgroup.creditocube.repository.CustomerRepository;
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

	@Autowired
	private CustomerRepository customerRepository;

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

	public List<DebitAccountTransaction> findRecentTransactionsOfCustomer(Customer customer) {
		List<DebitAccountTransaction> relatedTransactions = new ArrayList<>();

		// Find the customer that is logged in
		Optional<Customer> optionalCustomer = customerRepository.findById(customer.getUser_id());

		if (optionalCustomer.isEmpty()) {
			logger.info("Customer not found in database, returning empty list");
			return relatedTransactions;
		}

		Customer targetCustomer = optionalCustomer.get();

		List<DebitAccount> customerDebitAccounts = targetCustomer.getDebitAccounts();

		for (DebitAccount account : customerDebitAccounts) {
			relatedTransactions.addAll(debitAccountTransactionRepository.findByToAccount(account.getAccountNumber()));
			relatedTransactions.addAll(debitAccountTransactionRepository.findByFromAccount(account.getAccountId()));
		}

		List<DebitAccountTransaction> relatedTransactionsNoDuplicates = new ArrayList<>(
				new HashSet<>(relatedTransactions));

		relatedTransactionsNoDuplicates
				.sort(Comparator.comparing(DebitAccountTransaction::getDebitAccountTransactionDate));

		return relatedTransactionsNoDuplicates;

	}

}
