package com.fdmgroup.creditocube.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.InstallmentPayment;

public interface InstallmentPaymentRepository extends JpaRepository<InstallmentPayment, Long> {
	List<InstallmentPayment> findByTransactionCardIs(CreditCard card);
}
