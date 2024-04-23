package com.fdmgroup.creditocube.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

	private static final Logger LOGGER = LogManager.getLogger(BillService.class);

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

	public Optional<Bill> findBillByCreditCard(CreditCard card) {
		return billRepo.findByCardIs(card);
	}

	public Optional<Bill> generateBill(long cardId) {
		CreditCard card = cardService.findCardByCardId(cardId).orElse(null);
		double totalAmountDue = card.getBalance();
		double minimumAmountDue = new BigDecimal(totalAmountDue * 0.1).setScale(2, RoundingMode.HALF_UP).doubleValue();

		try {
			return createBill(new Bill(card, totalAmountDue, minimumAmountDue, false));
		} catch (Exception e) {
			LOGGER.error("Unable to generate bill for card number: {}", card.getCardNumber(), e);
			return Optional.of(null);
		}

	}

	// This method is called for every bill at the end of the billing cycle.
	// If no payment has been made towards the bill, it charges a late payment fee
	// If the bill has not been paid in full, an interest fee is charged on the
	// outstanding bill amount and added to the card balance.
	public void checkLatePayment(Bill bill) {

		CreditCard card = cardService.findCardByCardId(bill.getCard().getCardId()).orElse(null);
		double cardBalance = card.getBalance();

		if (bill.getOutstandingAmount() > 0) {

			// Interest rate is hard coded as 10% at the moment
			double interestFee = new BigDecimal(bill.getOutstandingAmount() * 0.1).setScale(2, RoundingMode.HALF_UP)
					.doubleValue();
			card.setBalance(cardBalance + interestFee);
			cardTransactionService.createInterestFeeTransaction(card, interestFee);
			LOGGER.info("Interest fee charged on outstanding amount for card number: {}", card.getCardNumber());
		}

		if (!bill.isPaid()) {
			card.setBalance(card.getBalance() + latePaymentFees);
			cardTransactionService.createLatePaymentTransaction(card, latePaymentFees);
			LOGGER.info("Late payment fee charged to card number: {}", card.getCardNumber());
		}

		cardService.updateCard(card);
	}

	public void recordMinimumAmountPayment(Bill bill) {
		// Record that the bill payment has been made so that the bill will not be
		// flagged for late payment fees
		bill.setPaid(true);

		// Update the outstanding amount to reflect that the minimum amount has been
		// paid
		bill.setOutstandingAmount(bill.getOutstandingAmount() - bill.getMinimumAmountDue());

		// Reset the minimum amount due to 0 as it has been paid
		bill.setMinimumAmountDue(0.0);

		updateBill(bill);
	}

	public void recordOutstandingAmountPayment(Bill bill) {
		// Record that the bill payment has been made so that the bill will not be
		// flagged for late payment fees
		bill.setPaid(true);

		// Update the outstanding amount to reflect that the outstanding amount has been
		// paid
		bill.setOutstandingAmount(0.0);

		// Reset the minimum amount due to 0 as there is no payment due
		bill.setMinimumAmountDue(0.0);

		updateBill(bill);
	}

	public void recordCreditBalancePayment(Bill bill) {
		// Record that the bill payment has been made so that the bill will not be
		// flagged for late payment fees
		bill.setPaid(true);

		// Update the outstanding amount to reflect that the outstanding amount has been
		// paid
		bill.setOutstandingAmount(0.0);

		// Reset the minimum amount due to 0 as there is no payment due
		bill.setMinimumAmountDue(0.0);

		updateBill(bill);
	}

	// This function is called on the 15th of every month at 6 am. It will go
	// through every card, and generate the credit card bill for that
	// billing cycle
	@Scheduled(cron = "0 0 6 15 * *")
	public void generateAllCardBills() {
		List<CreditCard> creditCards = cardService.findAllCreditCards();

		LOGGER.info("Scheduled task: generating all credit card bills.");

		for (CreditCard card : creditCards) {
			generateBill(card.getCardId());
		}

		LOGGER.info("Scheduled task: generating all credit card bills completed.");
	}

	// This function is called on the 10th of every month at 6 am. It will go
	// through every bill, and if the bill has not been paid, it will
	// add a late payment fee to the credit card to which the bill belongs, and
	// record this as a transaction on the card
	@Scheduled(cron = "0 0 6 10 * *")
	public void checkAllLatePayments() {
		List<Bill> allBills = billRepo.findAll();

		LOGGER.info("Scheduled task: checking all credit card bills for late payment.");

		for (Bill bill : allBills) {
			checkLatePayment(bill);
		}
	}

	public void createBillForNewCard(CreditCard newCard) {
		Optional<Bill> generatedBillOptional = generateBill(newCard.getCardId());

		if (generatedBillOptional.isEmpty()) {
			// What to do
			System.out.println("No bill generated");
		}

		Bill generatedBill = generatedBillOptional.get();
		generatedBill.setPaid(true);
		updateBill(generatedBill);

	}

}
