package com.fdmgroup.creditocube.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.Customer;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

	@Query("SELECT c FROM CreditCard c WHERE c.cardNumber LIKE :cardNumber")
	Optional<CreditCard> findByCardNumber(@Param("cardNumber") String cardNumber);

	List<CreditCard> findAllByCustomer(Customer customer);

	@Query("SELECT c FROM CreditCard c WHERE c.isActive = true AND c.customer.user_id = :customerId")
	public List<CreditCard> findAllActiveCreditCardsofCustomer(@Param("customerId") long customerId);

	@Query("SELECT c FROM CreditCard c WHERE c.isActive = false AND c.customer.user_id = :customerId")
	public List<CreditCard> findAllInactiveCreditCardsofCustomer(@Param("customerId") long customerId);

}
