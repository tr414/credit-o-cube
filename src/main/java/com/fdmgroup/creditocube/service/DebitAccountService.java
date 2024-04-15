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

@Service
public class DebitAccountService {

	@Autowired
	private DebitAccountRepository debitAccountRepository;

	@Autowired
	private UserRepository userRepository;

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

	public void updateAccount(DebitAccount account) {

		// determine if account exists in database
		Optional<DebitAccount> optionalAccount = debitAccountRepository.findByAccountNumber(account.getAccountNumber());

		// if account exists, persist
		if (optionalAccount.isPresent()) {
			debitAccountRepository.save(account);
		}
	}

	public Optional<DebitAccount> findDebitAccountByAccountNumber(long accountNumber) {
		Optional<DebitAccount> optionalAccount = debitAccountRepository.findByAccountNumber(accountNumber);

		if (optionalAccount.isEmpty()) {
			return Optional.empty();
		} else {
			return optionalAccount;
		}
	}

	public List<DebitAccount> findAllDebitAccountsForCustomer(Customer customer) {

		Optional<User> optionalCustomer = userRepository.findById(customer.getUser_id());

		// find all accounts
		List<DebitAccount> allAccountList = debitAccountRepository.findAll();
		List<DebitAccount> accountListInCustomer = new ArrayList<>();

		// check if customer exists
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

	public void closeDebitAccount(DebitAccount account) {
		Optional<DebitAccount> optionalAccount = debitAccountRepository.findByAccountNumber(account.getAccountNumber());

		if (optionalAccount.isEmpty()) {
			return;
		}

		DebitAccount targetAccount = optionalAccount.get();
		debitAccountRepository.delete(targetAccount);
	}

}
