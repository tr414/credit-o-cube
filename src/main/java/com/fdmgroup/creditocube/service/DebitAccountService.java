package com.fdmgroup.creditocube.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.model.User;
import com.fdmgroup.creditocube.repository.DebitAccountRepository;
import com.fdmgroup.creditocube.repository.UserRepository;

/**
 * This class provides services for managing debit accounts.
 */
@Service
public class DebitAccountService {

	@Autowired
	private DebitAccountRepository debitAccountRepository;

	@Autowired
	private UserRepository userRepository;

	/**
	 * Creates a new debit account in the system.
	 * 
	 * @param account the debit account to be created
	 */
	public void createAccount(DebitAccount account) {

		// determine if account exists in database
		Optional<DebitAccount> optionalAccount = debitAccountRepository.findByAccountNumber(account.getAccountNumber());

		if (optionalAccount.isPresent()) {
			System.out.println("Account already exists: ");
			return;
		}

		// otherwise, persist
		else {
			debitAccountRepository.save(account);
		}

	}

	/**
	 * Updates an existing debit account in the system.
	 * 
	 * @param account the debit account to be updated
	 */
	public void updateAccount(DebitAccount account) {

		// determine if account exists in database
		Optional<DebitAccount> optionalAccount = debitAccountRepository.findByAccountNumber(account.getAccountNumber());

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
	public Optional<DebitAccount> findDebitAccountByAccountNumber(long accountNumber) {

		Optional<DebitAccount> optionalAccount = debitAccountRepository.findByAccountNumber(accountNumber);

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
		Optional<User> optionalCustomer = userRepository.findById(customer.getUser_id());
		if (optionalCustomer.isEmpty()) {
			return accountListInCustomer;
		}

		Customer targetCustomer = (Customer) optionalCustomer.get();

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
		Optional<DebitAccount> optionalAccount = debitAccountRepository.findByAccountNumber(account.getAccountNumber());

		if (optionalAccount.isEmpty()) {
			return;
		}

		DebitAccount targetAccount = optionalAccount.get();
		debitAccountRepository.delete(targetAccount);
	}

}
