package com.fdmgroup.creditocube.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fdmgroup.creditocube.model.Bill;
import com.fdmgroup.creditocube.model.CreditCard;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
	public Optional<Bill> findByCardIs(CreditCard card);
}
