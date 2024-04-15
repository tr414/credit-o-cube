package com.fdmgroup.creditocube.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.repository.DebitAccountRepository;

@Service
public class DebitAccountService {

	@Autowired
	private DebitAccountRepository debitAccountRepository;

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

		List<DebitAccount> allAccountList = debitAccountRepository.findAll();
		List<DebitAccount> accountListInCustomer = new ArrayList<>();

		allAccountList.forEach(account -> {
			accountListInCustomer.add(account);
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
