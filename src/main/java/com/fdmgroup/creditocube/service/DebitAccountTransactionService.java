package com.fdmgroup.creditocube.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.model.DebitAccountTransaction;
import com.fdmgroup.creditocube.repository.DebitAccountRepository;
import com.fdmgroup.creditocube.repository.DebitAccountTransactionRepository;

@Service
public class DebitAccountTransactionService {

	@Autowired
	private DebitAccountTransactionRepository debitAccountTransactionRepository;

	@Autowired
	private DebitAccountRepository debitAccountRepository;

	public DebitAccountTransactionService(DebitAccountTransactionRepository debitAccountTransactionRepository) {
		this.debitAccountTransactionRepository = debitAccountTransactionRepository;
	}

	public void createDebitAccountTransaction(DebitAccountTransaction debitAccountTransaction) {

		debitAccountTransactionRepository.save(debitAccountTransaction);
	}

	public void updateDebitAccountTransaction(DebitAccountTransaction debitAccountTransaction) {

		debitAccountTransactionRepository.save(debitAccountTransaction);
	}

	public List<DebitAccountTransaction> findTransactionsOfAccount(DebitAccount account) {

		List<DebitAccountTransaction> relatedTransactions = new ArrayList<>();

		Optional<DebitAccount> optionalAccount = debitAccountRepository.findById(account.getAccountId());

		if (optionalAccount.isEmpty()) {
			return relatedTransactions;
		}

		DebitAccount targetAccount = optionalAccount.get();

		relatedTransactions.addAll(debitAccountTransactionRepository.findByToAccount(targetAccount.getAccountId()));
		relatedTransactions.addAll(debitAccountTransactionRepository.findByFromAccount(targetAccount.getAccountId()));

		Comparator<DebitAccountTransaction> byDate = (DebitAccountTransaction transaction1,
				DebitAccountTransaction transaction2) -> {
			return transaction1.getDebitAccountTransactionDate()
					.compareTo(transaction2.getDebitAccountTransactionDate());
		};

		return relatedTransactions;
	}

}
