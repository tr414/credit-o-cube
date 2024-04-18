package com.fdmgroup.creditocube.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fdmgroup.creditocube.model.CreditCard;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

	@Query("SELECT c FROM CreditCard c WHERE c.cardNumber LIKE :cardNumber")
	Optional<CreditCard> findByCardNumber(@Param("cardNumber") String cardNumber);
}
