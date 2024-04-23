package com.fdmgroup.creditocube.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.CreditCardTransaction;

public interface CreditCardTransactionRepository extends JpaRepository<CreditCardTransaction, Long> {
	List<CreditCardTransaction> findByTransactionCardIs(CreditCard card);
	List<CreditCardTransaction> findByTransactionCardIsAndTransactionDateAfterAndTransactionDateBefore(CreditCard card, LocalDateTime start, LocalDateTime end);
}
