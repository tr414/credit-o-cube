package com.fdmgroup.creditocube.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.CreditCardTransaction;
import com.fdmgroup.creditocube.repository.CreditCardTransactionRepository;

@Service
public class CreditCardTransactionService {
	
	@Autowired
	private CreditCardTransactionRepository repo;
	
	public Optional<CreditCardTransaction> createCreditCardTransaction(CreditCardTransaction transaction) {
		Optional<CreditCardTransaction> createdTransaction;
		
		try {
			createdTransaction = Optional.ofNullable(repo.save(transaction));
		} catch(Exception e) {
			//LOGGER.error("Unable to create new task in Task Service.", e);
			return Optional.ofNullable(null);
		}
		
		//LOGGER.info("Created new task with id: {}", createdTask.get().getId());
		return createdTransaction;
	}
	
	public List<CreditCardTransaction> findAllCreditCardTransactions(CreditCard card) {
		return repo.findByTransactionCardIs(card);
	}
	
	public Optional<CreditCardTransaction> updateCreditCardTransaction(CreditCardTransaction transaction) {
		Optional<CreditCardTransaction> updatedTransaction;
		
		try {
			updatedTransaction = Optional.ofNullable(repo.save(transaction));
		} catch(Exception e) {
			//LOGGER.error("Unable to create new task in Task Service.", e);
			return Optional.ofNullable(null);
		}
		
		//LOGGER.info("Created new task with id: {}", createdTask.get().getId());
		return updatedTransaction;
	}

}
