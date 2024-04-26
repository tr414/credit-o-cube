package com.fdmgroup.creditocube.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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

	public DebitAccountTransactionService(DebitAccountTransactionRepository debitAccountTransactionRepository,
			DebitAccountRepository debitAccountRepository, CustomerRepository customerRepository) {
		super();
		this.debitAccountTransactionRepository = debitAccountTransactionRepository;
		this.debitAccountRepository = debitAccountRepository;
		this.customerRepository = customerRepository;
	}

	/**
	 * Creates a new debit account transaction and saves it to the repository.
	 *
	 * @param debitAccountTransaction the debit account transaction to be created
	 */
	public void createDebitAccountTransaction(DebitAccountTransaction debitAccountTransaction) {

		if (debitAccountTransaction.getDebitAccountTransactionAmount() == 0) {
			logger.info("Transaction amount is zero, transaction not saved to database");
			return;
		}

		if (debitAccountTransaction.getDebitAccountTransactionType() == null) {
			logger.info("Transaction type is null, transaction not saved to database");
			return;
		}

		if (debitAccountTransaction.getFromAccount() != null) {
			String fromAccountNumber = debitAccountTransaction.getFromAccount().getAccountNumber();
			if (fromAccountNumber.equals(debitAccountTransaction.getToAccountNumber())) {
				logger.info("Transaction from and to account are the same, abort transaction");
				return;
			}
		}

		logger.debug("Transaction details saved to database");
		debitAccountTransactionRepository.save(debitAccountTransaction);
	}

	/**
	 * Updates an existing debit account transaction and saves it to the repository.
	 *
	 * @param debitAccountTransaction the debit account transaction to be updated
	 */
	public void updateDebitAccountTransaction(DebitAccountTransaction debitAccountTransaction) {

		if (debitAccountTransaction.getDebitAccountTransactionAmount() == 0) {
			logger.info("Transaction amount is zero, transaction not saved to database");
			return;
		}

		if (debitAccountTransaction.getDebitAccountTransactionType() == null) {
			logger.info("Transaction type is null, transaction not saved to database");
			return;
		}

		Optional<DebitAccountTransaction> optionalTransaction = debitAccountTransactionRepository
				.findById(debitAccountTransaction.getDebitAccountTransactionId());

		if (optionalTransaction.isEmpty()) {
			logger.info("Transaction not found in database, abort transaction");
			return;
		}

		if (debitAccountTransaction.getFromAccount() != null) {
			String fromAccountNumber = debitAccountTransaction.getFromAccount().getAccountNumber();
			if (fromAccountNumber.equals(debitAccountTransaction.getToAccountNumber())) {
				logger.info("Transaction from and to account are the same, abort transaction");
				return;
			}
		}

		// perform deep copy and save managed version of transaction
		DebitAccountTransaction targetTransaction = optionalTransaction.get();
		targetTransaction.setDebitAccountTransactionAmount(debitAccountTransaction.getDebitAccountTransactionAmount());
		targetTransaction.setDebitAccountTransactionDate(debitAccountTransaction.getDebitAccountTransactionDate());
		targetTransaction.setDebitAccountTransactionType(debitAccountTransaction.getDebitAccountTransactionType());
		targetTransaction.setFromAccount(debitAccountTransaction.getFromAccount());
		targetTransaction.setToAccountNumber(debitAccountTransaction.getToAccountNumber());

		logger.debug("Transaction details saved to database");

		// Save the updated debit account transaction to the repository
		debitAccountTransactionRepository.save(targetTransaction);
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
		relatedTransactions
				.addAll(debitAccountTransactionRepository.findByToAccountNumber(targetAccount.getAccountNumber()));
		relatedTransactions.addAll(debitAccountTransactionRepository.findByFromAccount(targetAccount.getAccountId()));

		// Sort the transactions by their transaction date
		relatedTransactions.sort(Comparator.comparing(DebitAccountTransaction::getDebitAccountTransactionDate).reversed());

		return relatedTransactions;
	}

	/**
	 * Finds recent transactions of a customer.
	 *
	 * @param customer the customer for which to find recent transactions
	 * @return a list of recent debit account transactions related to the given
	 *         customer
	 */
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
			relatedTransactions
					.addAll(debitAccountTransactionRepository.findByToAccountNumber(account.getAccountNumber()));
			relatedTransactions.addAll(debitAccountTransactionRepository.findByFromAccount(account.getAccountId()));
		}

		List<DebitAccountTransaction> relatedTransactionsNoDuplicates = new ArrayList<>(
				new HashSet<>(relatedTransactions));
		logger.debug("Removed duplicate transactions");

		Comparator<DebitAccountTransaction> latestFirst = (transaction1, transaction2) -> {
			return transaction2.getDebitAccountTransactionDate()
					.compareTo(transaction1.getDebitAccountTransactionDate());
		};

		relatedTransactionsNoDuplicates.sort(latestFirst);
		logger.debug("Sorted transactions in chronological order");

//		relatedTransactionsNoDuplicates.removeIf(transaction -> transaction.getDebitAccountTransactionDate()
//				.before(new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)));
//		logger.debug("Remove transactions more than 7 days ago");

		if (relatedTransactionsNoDuplicates.size() < 6) {
			logger.debug("Customer has no transactions");
			return relatedTransactionsNoDuplicates;
		}

		relatedTransactionsNoDuplicates.subList(5, relatedTransactionsNoDuplicates.size()).clear();
		logger.debug("Keep only most recent 5 transactions");

		return relatedTransactionsNoDuplicates;

	}

	public List<DebitAccountTransaction> findByTransactionDate(Date startDateTime, Date endDateTime,
			DebitAccount fromAccount, String toAccountNumber) {
		return debitAccountTransactionRepository.findByTransactionDate(startDateTime, endDateTime, fromAccount,
				toAccountNumber);
	}

	public List<DebitAccountTransaction> findTransactionsByMonth(int month, DebitAccount fromAccount) {
		return debitAccountTransactionRepository.findTransactionsByMonth(month, fromAccount);
	}

}
