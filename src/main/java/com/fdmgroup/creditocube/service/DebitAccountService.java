package com.fdmgroup.creditocube.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.repository.CustomerRepository;
import com.fdmgroup.creditocube.repository.DebitAccountRepository;

/**
 * This class provides services for managing debit accounts.
 * 
 * @author - timothy.chai
 */
@Service
public class DebitAccountService {

	/**
	 * Autowired DebitAccountRepository.
	 */
	@Autowired
	private DebitAccountRepository debitAccountRepository;

	/**
	 * Autowired CustomerRepository.
	 */
	@Autowired
	private CustomerRepository customerRepository;

	private static Logger logger = LogManager.getLogger(DebitAccountService.class);

	/**
	 * Constructor with no parameters.
	 */
	public DebitAccountService() {
	}

	/**
	 * Constructor with DebitAccountRepository and CustomerRepository as parameters.
	 *
	 * @param debitAccountRepository the debit account repository
	 * @param customerRepository     the customer repository
	 */
	public DebitAccountService(DebitAccountRepository debitAccountRepository, CustomerRepository customerRepository) {
		this.debitAccountRepository = debitAccountRepository;
		this.customerRepository = customerRepository;
	}

	/**
	 * Creates a new debit account in the system.
	 * 
	 * @param account the debit account to be created
	 */
	public void createAccount(DebitAccount account) {

		// determine if account exists in database
		Optional<DebitAccount> optionalAccount = debitAccountRepository.findById(account.getAccountId());

		if (optionalAccount.isPresent()) {
			logger.info("Account already exists: " + optionalAccount.get().getAccountId()
					+ " abort creating debit account");
			return;
		}

		// determine if customer exists in database
		Customer target = account.getCustomer();

		Optional<Customer> optionalCustomer = customerRepository.findById(target.getUser_id());

		if (optionalCustomer.isEmpty()) {
			logger.info("Customer not found, abort creating debit account");
			return;
		}

		// determine if customer has 5 or more accounts
		Customer accountHolder = optionalCustomer.get();
		List<DebitAccount> accountList = accountHolder.getDebitAccounts();

		if (accountList.size() >= 5) {
			logger.info("Customer has 5 or more debit accounts, abort creating debit account");
			return;
		}

		// check account balance is positive
		if (account.getAccountBalance() <= 0) {
			logger.info(
					"Customer tried to create debit account with non-positive balance, abort creating debit account");
			System.out.println("Account balance is not positive");
			return;
		}

		// otherwise, add account
		accountList.add(account);
		accountHolder.setDebitAccounts(accountList);
		debitAccountRepository.save(account);
		customerRepository.save(accountHolder);

		logger.debug("Create debit account successful");
	}

	/**
	 * Updates an existing debit account in the system.
	 * 
	 * @param account the debit account to be updated
	 */
	public void updateAccount(DebitAccount account) {

		// determine if account exists in database
		Optional<DebitAccount> optionalAccount = debitAccountRepository.findById(account.getAccountId());

		// if account exists, persist
		if (optionalAccount.isPresent()) {
			debitAccountRepository.save(account);
			logger.debug("Debit account exists, persistence successful");
			return;
		}

		logger.info("Debit account does not exist in database, persistence failed");
	}

	/**
	 * Searches for a debit account in the system based on the account number.
	 * 
	 * @param accountNumber the account number to search for
	 * @return an optional debit account, or an empty optional if no account was
	 *         found
	 */
	public Optional<DebitAccount> findDebitAccountByAccountNumber(String accountNumber) {

		Optional<DebitAccount> optionalAccount = debitAccountRepository.findByAccountNumber(accountNumber);

		if (optionalAccount.isEmpty()) {
			logger.info("Debit account not found, returning empty Optional");
			return Optional.empty();
		} else {
			logger.debug("Debit account found, returning Optional-wrapped account");
			return optionalAccount;
		}
	}

	/**
	 * Finds a debit account in the system based on the account id.
	 *
	 * @param id the unique identifier of the debit account to be found
	 * @return an optional debit account, or an empty optional if no account was
	 *         found
	 */
	public Optional<DebitAccount> findDebitAccountByAccountId(long id) {
		Optional<DebitAccount> optionalAccount = debitAccountRepository.findById(id);

		if (optionalAccount.isEmpty()) {
			logger.info("Debit account not found, returning empty Optional");
			return Optional.empty();
		} else {
			logger.debug("Debit account found, returning Optional-wrapped account");
			return optionalAccount;
		}
	}

	/**
	 * Returns a list of all debit accounts in the system that belong to a specific
	 * customer.
	 * 
	 * @param customer the customer whose debit accounts are to be retrieved
	 * @return a list of debit accounts that belong to the specified customer, or an
	 *         empty list if no accounts were found
	 */
	public List<DebitAccount> findAllDebitAccountsForCustomer(Customer customer) {

		// find all accounts
		List<DebitAccount> allAccountList = debitAccountRepository.findAll();
		List<DebitAccount> accountListInCustomer = new ArrayList<>();

		// check if customer exists
		Optional<Customer> optionalCustomer = customerRepository.findById(customer.getUser_id());
		if (optionalCustomer.isEmpty()) {
			logger.info("Customer not found in database, returing empty list");
			return accountListInCustomer;
		}

		Customer targetCustomer = optionalCustomer.get();
		logger.debug("Customer found in database");

		// retrieve acounts that belong to a customer
		allAccountList.forEach(account -> {
			if (account.getCustomer() == targetCustomer) {
				accountListInCustomer.add(account);
			}
		});

		if (accountListInCustomer.size() == 0) {
			logger.info("Customer does not have any debit accounts, returning empty list");
			return accountListInCustomer;
		}

		logger.debug("Returning debit accounts of customer");
		return accountListInCustomer;

	}

	/**
	 * Closes a debit account in the system.
	 *
	 * @param account the debit account to be closed
	 */
	public void closeDebitAccount(DebitAccount account) {
		// Check if the account exists in the database
		Optional<DebitAccount> optionalAccount = debitAccountRepository.findById(account.getAccountId());

		if (optionalAccount.isEmpty()) {
			logger.info("Debit account is not found in database, abort deletion");
			return;
		}

		// Get the target debit account
		DebitAccount targetAccount = optionalAccount.get();
		logger.debug("Debit account found in database");

		// Determine if the customer exists in the database
		Optional<Customer> optionalCustomer = customerRepository.findById(targetAccount.getCustomer().getUser_id());

		if (optionalCustomer.isEmpty()) {
			logger.info("Customer not found, abort deletion");
			return;
		}

		// Get the customer who owns the account

		Customer accountHolder = optionalCustomer.get();
		logger.debug("Customer found in database");

		if (targetAccount.getAccountBalance() != 0) {
			logger.info("Account balance is not equal to zero, deposit or withdraw to continue, abort deletion");
			return;
		}

		// Remove the target debit account from the customer's list of accounts
		accountHolder.getDebitAccounts().remove(targetAccount);

		// Save the updated customer to the database
		customerRepository.save(accountHolder);
		logger.debug("Customer details updated");

		// Delete the target debit account from the database
		debitAccountRepository.delete(targetAccount);
		logger.debug("Debit account deleted");

	}

	/**
	 * Changes the balance of a debit account, called for deposits and withdrawals.
	 * Typically called along with creating a DebitAccountTransaction
	 *
	 * @param account   the debit account whose balance is to be changed
	 * @param amount    the amount to be deposited or withdrawn
	 * @param isDeposit a boolean value indicating whether the operation is a
	 *                  deposit or withdrawal
	 * 
	 * @see DebitAccountTransactionService#createDebitAccountTransaction(com.fdmgroup.creditocube.model.DebitAccountTransaction)
	 */
	public void changeAccountBalance(DebitAccount account, double amount, boolean isDeposit) {
		// Check if the account exists in the database
		Optional<DebitAccount> optionalAccount = debitAccountRepository.findByAccountNumber(account.getAccountNumber());

		if (optionalAccount.isEmpty()) {
			logger.info("Debit account is not found in database, abort transaction");
			return;
		}

		DebitAccount targetAccount = optionalAccount.get();
		logger.debug("Debit account found in database");

		Customer targetCustomer = account.getCustomer();

		// Check if account holder is present in database
		Optional<Customer> optionalAccountHolder = customerRepository.findById(targetCustomer.getUser_id());

		if (optionalAccountHolder.isEmpty()) {
			logger.info("Account holder not found in database, abort transaction");
			return;
		}

		// Get the account holder
		Customer accountHolder = optionalAccountHolder.get();
		logger.debug("Account holder found in database");

		if (amount <= 0.00) {
			logger.info("Transaction amount is not positive, abort transaction");
			return;
		}

		double newBalance;
		double currentBalance = targetAccount.getAccountBalance();

		if (isDeposit) {
			newBalance = currentBalance + amount;
			logger.debug("Customer wishes to deposit " + amount + " into account");
		} else {
			newBalance = (amount > currentBalance) ? 0 : currentBalance - amount;
			logger.debug("Customer wishes to withdraw " + amount + " from account");
		}

		targetAccount.setAccountBalance(newBalance);
		logger.debug("New balance is updated into database");
		debitAccountRepository.save(targetAccount);
		customerRepository.save(accountHolder);
		logger.debug("Customer and account information updated");
	}

	public void transferToAccountNumber(DebitAccount fromAccount, String toAccountNumber, double amount) {

		// Check if fromAccount exists in the database
		Optional<DebitAccount> optionalAccount = debitAccountRepository.findById(fromAccount.getAccountId());

		if (optionalAccount.isEmpty()) {
			logger.info("Debit account is not found in database, abort transaction");
			return;
		}

		DebitAccount targetAccount = optionalAccount.get();
		logger.debug("Target fromAccount found in database");

		// transactions are assumed to be withdrawing from fromAccount and depositing
		// into toAccount

		if (amount <= 0.00) {
			logger.info("Transaction amount is not positive, abort transaction");
			return;
		}

		double currentBalance = targetAccount.getAccountBalance();
		double newBalance = (amount > currentBalance) ? 0 : currentBalance - amount;
		logger.debug("Withdrawing " + amount + " from fromAccount + ");

		targetAccount.setAccountBalance(newBalance);
		logger.debug("New balance is updated into database");
		debitAccountRepository.save(targetAccount);

		Optional<DebitAccount> optionalToAccount = debitAccountRepository.findByAccountNumber(toAccountNumber);

		if (optionalToAccount.isEmpty()) {
			logger.debug("Target toAccount not found in database, assume toAccount is in another bank");
			return;
		} else {
			DebitAccount toAccount = optionalToAccount.get();
			logger.debug("Target toAccount found in database");

			double toCurrentBalance = toAccount.getAccountBalance();
			double toNewBalance = toCurrentBalance + amount;
			logger.debug("Depositing " + amount + " to toAccount + ");

			toAccount.setAccountBalance(toNewBalance);
			debitAccountRepository.save(toAccount);
		}

	}

	/**
	 * Generates a unique debit account number.
	 *
	 * @return a unique debit account number
	 */
	public String generateUniqueDebitAccountNumber() {
		String accountNumber;
		final Random random = new Random();
		do {
			long number = (long) (100000000 + random.nextInt(900000000));
			accountNumber = String.format("%09d", number);
		} while (accountNumberExists(accountNumber));
		return accountNumber;
	}

	/**
	 * Checks if a debit account number already exists in the database.
	 *
	 * @param accountNumber the debit account number to check
	 * @return true if the account number exists, false otherwise
	 */
	private boolean accountNumberExists(String accountNumber) {
		Optional<DebitAccount> optionalAccount = debitAccountRepository.findByAccountNumber(accountNumber);
		return optionalAccount.isPresent();
	}

}
