package com.fdmgroup.creditocube.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.InstallmentPayment;
import com.fdmgroup.creditocube.repository.InstallmentPaymentRepository;

@Service
public class InstallmentPaymentService {
	
	@Autowired
	InstallmentPaymentRepository installmentRepo;
	
	public Optional<InstallmentPayment> createInstallmentPayment(InstallmentPayment installmentPayment) {
		return Optional.ofNullable(installmentRepo.save(installmentPayment));
	}
	
	public Optional<InstallmentPayment> updateInstallmentPayment(InstallmentPayment installmentPayment) {
		return Optional.ofNullable(installmentRepo.save(installmentPayment));
	}
	
	
	public List<InstallmentPayment> findAllInstallmentPaymentsByCard(CreditCard card) {
		return installmentRepo.findByTransactionCardIs(card);
	}
	
	public void deleteInstallmentPayment(InstallmentPayment installmentPayment) {
		installmentRepo.deleteById(installmentPayment.getInstallmentPaymentId());
	}
}
