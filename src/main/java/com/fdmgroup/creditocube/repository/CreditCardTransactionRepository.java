package com.fdmgroup.creditocube.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.CreditCardTransaction;

public interface CreditCardTransactionRepository extends JpaRepository<CreditCardTransaction, Long> {
	List<CreditCardTransaction> findByTransactionCardIs(CreditCard card);

	@Query("SELECT t FROM CreditCardTransaction t WHERE t.transactionDate >= :startDateTime AND t.transactionDate <= :endDateTime AND t.transactionCard = :card")
	List<CreditCardTransaction> findByTransactionDate(@Param("startDateTime") LocalDateTime startDateTime,
			@Param("endDateTime") LocalDateTime endDateTime, @Param("card") CreditCard card);

	@Query("SELECT t FROM CreditCardTransaction t WHERE MONTH(transactionDate) = :month AND t.transactionCard = :card")
	public List<CreditCardTransaction> findTransactionsByMonth(@Param("month") int month,
			@Param("card") CreditCard card);

	List<CreditCardTransaction> findByTransactionCardIsAndTransactionDateAfterAndTransactionDateBefore(CreditCard card,
			LocalDateTime start, LocalDateTime end);
}
