package com.fdmgroup.creditocube.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.repository.CustomerRepository;
import com.fdmgroup.creditocube.repository.DebitAccountRepository;

/**
 * This class provides services for managing debit accounts.
 */
@Service
public class DebitAccountService {

	@Autowired
	private DebitAccountRepository debitAccountRepository;

	@Autowired
	private CustomerRepository customerRepository;

	public DebitAccountService() {
	}

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
			System.out.println("Account already exists: " + optionalAccount.get().getAccountId());
			return;
		}

		// determine if customer exists in database
		Customer target = account.getCustomer();

		Optional<Customer> optionalCustomer = customerRepository.findById(target.getUser_id());

		if (optionalCustomer.isEmpty()) {
			System.out.println("Customer not found");
			return;
		}

		// determine if customer has 5 or more accounts
		Customer accountHolder = optionalCustomer.get();
		List<DebitAccount> accountList = accountHolder.getDebitAccounts();

		if (accountList.size() >= 5) {
			System.out.println("You cannot have 5 or more debit accounts per customer.");
			return;
		}

		// check account balance is positive
		if (account.getAccountBalance() <= 0) {
			System.out.println("Account balance is not positive");
			return;
		}

		// otherwise, add account
		accountList.add(account);
		accountHolder.setDebitAccounts(accountList);
		debitAccountRepository.save(account);
		customerRepository.save(accountHolder);

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
		}
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
			return Optional.empty();
		} else {
			return optionalAccount;
		}
	}

	public Optional<DebitAccount> findDebitAccountByAccountId(long id) {

		Optional<DebitAccount> optionalAccount = debitAccountRepository.findById(id);

		if (optionalAccount.isEmpty()) {
			return Optional.empty();
		} else {
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
			return accountListInCustomer;
		}

		Customer targetCustomer = optionalCustomer.get();

		// retrieve acounts that belong to a customer
		allAccountList.forEach(account -> {
			if (account.getCustomer() == targetCustomer) {
				accountListInCustomer.add(account);
			}
		});
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
			return;
		}

		// Get the target debit account
		DebitAccount targetAccount = optionalAccount.get();

		// Determine if the customer exists in the database
		Optional<Customer> optionalCustomer = customerRepository.findById(targetAccount.getCustomer().getUser_id());

		if (optionalCustomer.isEmpty()) {
			System.out.println("Customer not found");
			return;
		}

		// Get the customer who owns the account

		Customer accountHolder = optionalCustomer.get();

		if (targetAccount.getAccountBalance() != 0) {
			System.out.println("Account balance is not equal to zero, please deposit");
			return;
		}

		// Remove the target debit account from the customer's list of accounts
		accountHolder.getDebitAccounts().remove(targetAccount);

		// Delete the target debit account from the database
		debitAccountRepository.delete(targetAccount);

		// Save the updated customer to the database
		customerRepository.save(accountHolder);
	}

	public void changeAccountBalance(DebitAccount account, double amount, boolean isDeposit) {
		// Check if the account exists in the database
		Optional<DebitAccount> optionalAccount = debitAccountRepository.findByAccountNumber(account.getAccountNumber());

		if (optionalAccount.isEmpty()) {
			return;
		}

		DebitAccount targetAccount = optionalAccount.get();
		Customer targetCustomer = account.getCustomer();

		// Check if account holder is present in database
		Optional<Customer> optionalAccountHolder = customerRepository.findById(targetCustomer.getUser_id());

		if (optionalAccountHolder.isEmpty()) {
			System.out.println("Customer not found");
			return;
		}

		// Get the account holder
		Customer accountHolder = optionalAccountHolder.get();

		if (amount <= 0.00) {
			System.out.println("deposit/withdraw amount must be positive");
			return;
		}

		double newBalance;
		double currentBalance = targetAccount.getAccountBalance();

		if (isDeposit) {
			newBalance = currentBalance + amount;
		} else {
			newBalance = (amount > currentBalance) ? 0 : currentBalance - amount;

		}

		targetAccount.setAccountBalance(newBalance);
//		System.out.println("target account balance: " + targetAccount.getAccountBalance());
		debitAccountRepository.save(targetAccount);
		customerRepository.save(accountHolder);
	}

	public String generateUniqueDebitAccountNumber() {
		String accountNumber;
		final Random random = new Random();
		do {
			long number = (long) (100000000 + random.nextInt(900000000));
			accountNumber = String.format("%09d", number);
		} while (accountNumberExists(accountNumber));
		return accountNumber;

	}

	private boolean accountNumberExists(String accountNumber) {
		Optional<DebitAccount> optionalAccount = debitAccountRepository.findByAccountNumber(accountNumber);

		return optionalAccount.isPresent();
	}

}
