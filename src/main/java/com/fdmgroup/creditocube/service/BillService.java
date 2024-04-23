package com.fdmgroup.creditocube.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.Bill;
import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.CreditCardTransaction;
import com.fdmgroup.creditocube.model.InstallmentPayment;
import com.fdmgroup.creditocube.repository.BillRepository;

@Service
public class BillService {

	@Autowired
	private BillRepository billRepo;

	@Autowired
	private CreditCardService cardService;

	@Autowired
	private CreditCardTransactionService cardTransactionService;
	
	@Autowired
	private InstallmentPaymentService installmentService;

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
		currentBill.setMinimumAmountDue(bill.getMinimumAmountDue());
		currentBill.setOutstandingAmount(bill.getOutstandingAmount());
		currentBill.setPaid(bill.isPaid());
		currentBill.setTotalAmountDue(bill.getTotalAmountDue());
		
		return Optional.ofNullable(billRepo.save(currentBill));
	}

	public Optional<Bill> findBillByCreditCard(CreditCard card) {
		return billRepo.findByCardIs(card);
	}

	public Optional<Bill> generateBill(long cardId) {
		CreditCard card = cardService.findCardByCardId(cardId).orElse(null);
		
		// Before generating the bill, check if the customer is eligible for cashback based on the monthly spend, and apply that cashback
		applyCashback(card.getCardId());
		
		double totalAmountDue = card.getBalance();
		Optional<Bill> cardBillOptional = billRepo.findByCardIs(card);
		
		// The bill for this card does not exist, suggesting that this is a new card, and hence we need to generate a new bill
		// Return this newly generated bill
		if (cardBillOptional.isEmpty()) {
			Bill newCardBill = new Bill(card, 0, 0, true);
			return createBill(newCardBill);
		}
		
		Bill cardBill = cardBillOptional.get();
		
		// Modify total amount due by accounting for transactions made on installment plans, subtracting away the used balance that does
		// not need to be paid this month
		double nonDueInstallmentPayments = calculateNonDueInstallmentPayments(cardId);
		
		totalAmountDue = totalAmountDue - nonDueInstallmentPayments;
		
		double minimumAmountDue = new BigDecimal(totalAmountDue * 0.1).setScale(2, RoundingMode.HALF_UP).doubleValue();
		
		cardBill.setMinimumAmountDue(minimumAmountDue);
		cardBill.setTotalAmountDue(totalAmountDue);
		cardBill.setOutstandingAmount(totalAmountDue);
		cardBill.setPaid(false);
		
		try {
			return updateBill(cardBill);
		} catch (Exception e) {
			LOGGER.error("Unable to generate bill for card number: {}", card.getCardNumber(), e);
			return Optional.of(null);
		}

	}

	private double calculateNonDueInstallmentPayments(long cardId) {
		CreditCard card = cardService.findCardByCardId(cardId).orElse(null);
		List<InstallmentPayment> allInstallmentPayments =  installmentService.findAllInstallmentPaymentsByCard(card);
		
		double totalNonDueInstallmentPayments = 0.0;
		
		for (InstallmentPayment installment : allInstallmentPayments) {
			
			CreditCardTransaction installmentTransaction = new CreditCardTransaction();
			installmentTransaction.setTransactionCard(card);
			installmentTransaction.setTransactionDate(LocalDateTime.now());
			installmentTransaction.setDescription("Installment payment for outstanding big ticket purchase");
			
			// If this is the final installment, the entire amount needs to be paid, so no contribution
			// to nondueinstallmentpayments. Simply delete this entry from the database and ensure that it is added to the current bill
			// delete the entry from the table as this is the final installment, and move into the next InstallmentPayment in the list
			// Create a transaction to reflect in the user bill for the month
			if (installment.getInstallmentsLeft() ==  1) {
				installmentTransaction.setTransactionAmount(installment.getAmountLeft());
				cardTransactionService.createCreditCardTransaction(installmentTransaction);
				installmentService.deleteInstallmentPayment(installment);
				continue;
			}
			
			// If this is not the last installment, calculate how much money will be due in future installments and should be subtracted
			// from the current bill.
			
			double installmentAmount = new BigDecimal((installment.getAmountLeft() / installment.getInstallmentsLeft()))
											.setScale(2, RoundingMode.HALF_UP)
											.doubleValue();
			
			// Create a transaction to reflect in the user bill for the month
			installmentTransaction.setTransactionAmount(installmentAmount);
			cardTransactionService.createCreditCardTransaction(installmentTransaction);
			
			int remainingInstallments = installment.getInstallmentsLeft() - 1;
			double nonDueInstallmentPayments = new BigDecimal((installment.getAmountLeft() - installmentAmount))
												.setScale(2, RoundingMode.HALF_UP)
												.doubleValue();
			
			totalNonDueInstallmentPayments = totalNonDueInstallmentPayments + nonDueInstallmentPayments; 
			
			// Update the installment payment to reflect that the current month's installment will be charged in the bill
			// Hence reducing the amount left by the amount paid in the current installment
			// And reducing the number of installment payments left for this item by 1
			installment.setAmountLeft(nonDueInstallmentPayments);
			installment.setInstallmentsLeft(remainingInstallments);
			installmentService.updateInstallmentPayment(installment);
			
			
		}
		
		return totalNonDueInstallmentPayments;
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
	@Scheduled(cron = "0 27 12 23 4 *")
	public void generateAllCardBills() {
		List<CreditCard> creditCards = cardService.findAllCreditCards();

		LOGGER.info("Scheduled task: generating all credit card bills.");

		for (CreditCard card : creditCards) {
			generateBill(card.getCardId());
		}

		LOGGER.info("Scheduled task: generating all credit card bills completed.");
	}

	private void applyCashback(long cardId) {
		CreditCard card = cardService.findCardByCardId(cardId).orElse(null);
		double monthlySpend = card.getMonthlySpend();
		
		// if monthly spend exceeds minimum spend requirement, credit cashback for the month 
		// create a transaction to show that the cashback was credited
		if (monthlySpend >= 600) {
			card.setBalance(card.getBalance() - card.getCashback());
			cardTransactionService.createCashbackTransaction(card, card.getCashback());
		}
		
		// once cashback has been credited, reset accrued cashback to 0, reset spending in billing cycle to 0, and persist the new state of the card
		card.setCashback(0.0);
		card.setMonthlySpend(0.0);
		cardService.updateCard(card);
		
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
		}

		Bill generatedBill = generatedBillOptional.get();
		generatedBill.setPaid(true);
		updateBill(generatedBill);

	}

}
