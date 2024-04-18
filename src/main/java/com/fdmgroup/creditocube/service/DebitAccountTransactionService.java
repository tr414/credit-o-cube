package com.fdmgroup.creditocube.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.DebitAccountTransaction;
import com.fdmgroup.creditocube.repository.DebitAccountTransactionRepository;

@Service
public class DebitAccountTransactionService {

	@Autowired
	private DebitAccountTransactionRepository debitAccountTransactionRepository;

	public DebitAccountTransactionService(DebitAccountTransactionRepository debitAccountTransactionRepository) {
		this.debitAccountTransactionRepository = debitAccountTransactionRepository;
	}

	public void createDebitAccountTransaction(DebitAccountTransaction debitAccountTransaction) {

		debitAccountTransactionRepository.save(debitAccountTransaction);
	}

	public void updateDebitAccountTransaction(DebitAccountTransaction debitAccountTransaction) {

		debitAccountTransactionRepository.save(debitAccountTransaction);
	}
}
