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
		
		if (currentBill == null) {
			return Optional.of(null);
		}
		currentBill.setMinimumAmountDue(bill.getMinimumAmountDue());
		currentBill.setOutstandingAmount(bill.getOutstandingAmount());
		currentBill.setPaid(bill.isPaid());
		currentBill.setTotalAmountDue(bill.getTotalAmountDue());
		currentBill.setCashbackEarned(bill.getCashbackEarned());
		currentBill.setMonthlySpend(bill.getMonthlySpend());

		return Optional.ofNullable(billRepo.save(currentBill));
	}

	public Optional<Bill> findBillByCreditCard(CreditCard card) {
		return billRepo.findByCardIs(card);
	}

	public Optional<Bill> findBillById(long id) {
		return billRepo.findById(id);
	}
	
	public void deleteBillById(long id) {
		billRepo.deleteById(id);
	}

	public Optional<Bill> generateBill(long cardId) {
		CreditCard card = cardService.findCardByCardId(cardId).orElse(null);
		
		if (card == null) {
			LOGGER.info("Unable to find card with cardId {}", cardId);
			return Optional.of(null);
		}
		
		LOGGER.info("Generating bill for card number: {}", card.getCardNumber());
		
		

		
		Optional<Bill> cardBillOptional = billRepo.findByCardIs(card);

		// The bill for this card does not exist, suggesting that this is a new card,
		// and hence we need to generate a new bill
		// Return this newly generated bill
		if (cardBillOptional.isEmpty()) {
			Bill newCardBill = new Bill(card, 0, 0, true);
			newCardBill.setBillIssueTime(LocalDateTime.now());
			newCardBill.setPreviousBillIssueTime(LocalDateTime.now().minusDays(1));
			newCardBill.setPreviousBillOutstandingAmount(0);
			LOGGER.info("Generated empty bill for new card number: {}", card.getCardNumber());
			return createBill(newCardBill);
		}
		
		// Before generating the bill, check if the customer is eligible for cashback
				// based on the monthly spend, and apply that cashback
		applyCashback(card.getCardId());
		double totalAmountDue = card.getBalance();
		
		Bill cardBill = cardBillOptional.get();

		// Modify total amount due by accounting for transactions made on installment
		// plans, subtracting away the used balance that does
		// not need to be paid this month
		double nonDueInstallmentPayments = calculateNonDueInstallmentPayments(cardId);

		totalAmountDue = new BigDecimal(totalAmountDue - nonDueInstallmentPayments).setScale(2, RoundingMode.HALF_UP)
				.doubleValue();

		double minimumAmountDue = new BigDecimal(totalAmountDue * 0.1).setScale(2, RoundingMode.HALF_UP).doubleValue();

		cardBill.setPreviousBillIssueTime(cardBill.getBillIssueTime());
		cardBill.setPreviousBillOutstandingAmount(cardBill.getTotalAmountDue());
		cardBill.setMinimumAmountDue(minimumAmountDue);
		cardBill.setTotalAmountDue(totalAmountDue);
		cardBill.setOutstandingAmount(totalAmountDue);
		
		// If there is no outstanding amount due for the bill, set 'paid' to true so that the bill is not
		// flagged for late payment, and the customer is aware that no payment is required
		if (cardBill.getOutstandingAmount() > 0) {
			cardBill.setPaid(false);
		} else {
			cardBill.setPaid(true);
		}
		cardBill.setBillIssueTime(LocalDateTime.now());

		try {
			return updateBill(cardBill);
		} catch (Exception e) {
			LOGGER.error("Unable to generate bill for card number: {}", card.getCardNumber(), e);
			return Optional.of(null);
		}

	}

	private double calculateNonDueInstallmentPayments(long cardId) {
		CreditCard card = cardService.findCardByCardId(cardId).orElse(null);
		
		List<InstallmentPayment> allInstallmentPayments = installmentService.findAllInstallmentPaymentsByCard(card);
		
		LOGGER.info("Calculating non-due installment payments for card number: {}", card.getCardNumber());
		
		double totalNonDueInstallmentPayments = 0.0;

		for (InstallmentPayment installment : allInstallmentPayments) {

			CreditCardTransaction installmentTransaction = new CreditCardTransaction();
			installmentTransaction.setTransactionCard(card);
			installmentTransaction.setTransactionDate(LocalDateTime.now());
			
			String installmentNumber = String.valueOf(7 - installment.getInstallmentsLeft());
			
			installmentTransaction.setDescription(String.format("Installment payment %s / 6 for purchase", installmentNumber));

			// If this is the final installment, the entire amount needs to be paid, so no
			// contribution
			// to nondueinstallmentpayments. Simply delete this entry from the database and
			// ensure that it is added to the current bill
			// delete the entry from the table as this is the final installment, and move
			// into the next InstallmentPayment in the list
			// Create a transaction to reflect in the user bill for the month
			if (installment.getInstallmentsLeft() == 1) {
				installmentTransaction.setTransactionAmount(installment.getAmountLeft());
				cardTransactionService.createCreditCardTransaction(installmentTransaction);
				installmentService.deleteInstallmentPayment(installment);
				continue;
			}

			// If this is not the last installment, calculate how much money will be due in
			// future installments and should be subtracted
			// from the current bill.

			double installmentAmount = new BigDecimal((installment.getAmountLeft() / installment.getInstallmentsLeft()))
					.setScale(2, RoundingMode.HALF_UP).doubleValue();

			// Create a transaction to reflect in the user bill for the month
			installmentTransaction.setTransactionAmount(installmentAmount);
			cardTransactionService.createCreditCardTransaction(installmentTransaction);

			int remainingInstallments = installment.getInstallmentsLeft() - 1;
			double nonDueInstallmentPayments = new BigDecimal((installment.getAmountLeft() - installmentAmount))
					.setScale(2, RoundingMode.HALF_UP).doubleValue();

			totalNonDueInstallmentPayments = totalNonDueInstallmentPayments + nonDueInstallmentPayments;

			// Update the installment payment to reflect that the current month's
			// installment will be charged in the bill
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
			card.setBalance( new BigDecimal(cardBalance + interestFee).setScale(2, RoundingMode.HALF_UP).doubleValue() ); 
			cardTransactionService.createInterestFeeTransaction(card, interestFee);
			LOGGER.info("Interest fee charged on outstanding amount for card number: {}", card.getCardNumber());
		}

		if (!bill.isPaid()) {
			card.setBalance( new BigDecimal(card.getBalance() + latePaymentFees).setScale(2, RoundingMode.HALF_UP).doubleValue() ); 
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
		List<CreditCard> creditCards = cardService.findAllActiveCreditCards();

		LOGGER.info("Scheduled task: generating all credit card bills.");

		for (CreditCard card : creditCards) {
			generateBill(card.getCardId());
		}

		LOGGER.info("Scheduled task: generating all credit card bills completed.");
	}

	private void applyCashback(long cardId) {
		CreditCard card = cardService.findCardByCardId(cardId).orElse(null);
		Bill bill = findBillByCreditCard(card).orElse(null);
		
		
		double monthlySpend = card.getMonthlySpend();
		double cashbackAccrued = card.getCashback();
		double cashbackCarriedForward = card.getCashbackCarriedForward();
		double cashbackCredited = 0.0;
		
		
		// Update the monthly spend in the bill
		bill.setMonthlySpend(monthlySpend);
		
		// of the cashback carried forward, credit the entire amount to the card balance, or = to card balance, whichever is smaller
		double cashbackCarriedForwardCredited =  cashbackCarriedForward <= card.getBalance() ? cashbackCarriedForward : card.getBalance();
		
		if ( cashbackCarriedForwardCredited > 0 ) {
			cardTransactionService.createCashbackCarryForwardTransaction(card, cashbackCarriedForwardCredited);
		}
		
		// reset the cashback carried forward based on how much has already been credited to the customer
		cashbackCarriedForward = cashbackCarriedForward - cashbackCarriedForwardCredited;
		
		// deduct the cashback carried forward from the card balance
		card.setBalance( new BigDecimal( card.getBalance() - cashbackCarriedForwardCredited ).setScale(2, RoundingMode.HALF_UP).doubleValue() ); 
		card.setCashbackCarriedForward(cashbackCarriedForward);
		
		
		
		// if monthly spend exceeds minimum spend requirement, credit cashback for the
		// month
		// create a transaction to show that the cashback was credited
		if (monthlySpend >= 600 && cashbackAccrued > 0) {
			if (cashbackAccrued > card.getBalance()) {
				cashbackCredited = card.getBalance();
			}
			else {
				cashbackCredited = cashbackAccrued;
			}
			card.setBalance( new BigDecimal( card.getBalance() - cashbackCredited ).setScale(2, RoundingMode.HALF_UP).doubleValue() ); 
			
			// If the full cashback accrued was not credited due to the card balance being too low, carry forward the card balance to the
			// Next billing cycle
			card.setCashbackCarriedForward(cashbackCarriedForward + cashbackAccrued - cashbackCredited);
			
			if ( cashbackCredited > 0) {
				cardTransactionService.createCashbackTransaction(card, cashbackCredited);
			}
		}
		
		bill.setCashbackEarned(cashbackCredited);
		
		updateBill(bill);

		// Since cashback will be credited to account, reset the card cashback once cashback has been credited reset spending
		// in billing cycle to 0, and persist the new state of the card
		// card.setCashbackCarriedForward(cashbackAccrued - cashbackCredited);
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
		
		LOGGER.info("Scheduled task: finished checking all credit card bills for late payment.");
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

	public List<CreditCardTransaction> findBillingCycleTransactions(Bill bill) {

		LocalDateTime billIssueTime = bill.getBillIssueTime();
		LocalDateTime billingCycleStartTime = bill.getPreviousBillIssueTime();

		return cardTransactionService.findBillTransactionsBetween(bill, billingCycleStartTime, billIssueTime);
	}

}
