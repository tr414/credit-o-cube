package com.fdmgroup.creditocube.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.Bill;
import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.repository.BillRepository;

@Service
public class BillService {
	
	@Autowired
	private BillRepository billRepo;
	
	@Autowired
	private CreditCardService cardService;
	
	@Autowired
	private CreditCardTransactionService cardTransactionService;
	
	private long latePaymentFees = 100;
	
	public BillService() {
		// TODO Auto-generated constructor stub
	}
	
	public Optional<Bill> createBill(Bill bill) {
		return Optional.ofNullable(billRepo.save(bill));
	}
	
	public Optional<Bill> updateBill(Bill bill) {
		Bill currentBill = billRepo.findById(bill.getId()).orElse(null);
		
		return Optional.ofNullable(billRepo.save(bill));
	}
	
	public Optional<Bill> generateBill(long cardId) {
		CreditCard card = cardService.findCardByCardId(cardId).orElse(null);
		double totalAmountDue = card.getBalance();
		double minimumAmountDue = new BigDecimal(totalAmountDue * 0.1).setScale(2, RoundingMode.HALF_UP).doubleValue();
		
		//Bill cardBill = new Bill(card, totalAmountDue, minimumAmountDue, false);
		
		return createBill(new Bill(card, totalAmountDue, minimumAmountDue, false));
	}
	
	public void checkLatePayment(Bill bill) {
		if (!bill.isPaid()) {
			CreditCard card = cardService.findCardByCardId(bill.getCard().getCardId()).orElse(null);
			card.setBalance(card.getBalance() + latePaymentFees);
			cardService.updateCard(card);
			cardTransactionService.createLatePaymentTransaction(card, latePaymentFees);
		}
	}
	
	// This function is called on the 15th of every month at 9 am. It will go through every card, and generate the credit card bill for that
	// billing cycle
	@Scheduled(cron = "0 0 18 21 * *")
	public void generateAllCardBills() {
		List<CreditCard> creditCards = cardService.findAllCreditCards();
		System.out.println("Generating all card bills");
		for (CreditCard card : creditCards) {
			generateBill(card.getCardId());
		}
	}
	
	// This function is called on the 10th of every month at 9 am. It will go through every bill, and if the bill has not been paid, it will
	// add a late payment fee to the credit card to which the bill belongs
	@Scheduled(cron = "0 1 18 21 * *")
	public void checkAllLatePayments() {
		List<Bill> allBills = billRepo.findAll();
		System.out.println("Checking late payments");
		for (Bill bill : allBills) {
			checkLatePayment(bill);
		}
	}

}
