package com.fdmgroup.creditocube.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fdmgroup.creditocube.model.Bill;
import com.fdmgroup.creditocube.model.CardType;
import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.CreditCardTransaction;
import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.model.DebitAccountTransaction;
import com.fdmgroup.creditocube.service.BillService;
import com.fdmgroup.creditocube.service.CardTypeService;
import com.fdmgroup.creditocube.service.CreditCardService;
import com.fdmgroup.creditocube.service.CreditCardTransactionService;
import com.fdmgroup.creditocube.service.CustomerService;
import com.fdmgroup.creditocube.service.DebitAccountService;
import com.fdmgroup.creditocube.service.DebitAccountTransactionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CreditCardController {

	@Autowired
	private CreditCardService creditCardService;

	@Autowired
	private HttpSession session;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CardTypeService cardTypeService;

	@Autowired
	private DebitAccountService debitAccountService;

	@Autowired
	private DebitAccountTransactionService debitAccountTransactionService;

	@Autowired
	private BillService billService;

	@Autowired
	private CreditCardTransactionService creditCardTransactionService;

	private static Logger logger = LogManager.getLogger(CreditCardController.class);

	// credit card dashboard
	@GetMapping("/creditcard-dashboard")
	public String creditCardDashboard(Principal principal, Model model) {
		// Find the user associated with the provided customer ID.
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());

		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}

		// set customer and their accounts as session attributes to retrieve in view
		// Customer customer = optionalCustomer.get();
		Customer sessionCustomer = optionalCustomer.get();
		session.setAttribute("customer", sessionCustomer);
		model.addAttribute("customer", sessionCustomer);

		List<CreditCard> creditCards = creditCardService.findAllCardsForCustomer(sessionCustomer);
		model.addAttribute("credit_cards", creditCards);
		return "creditcard-dashboard";
	}

//	// Apply for credit card
//	@GetMapping("/apply-creditcard")
//	public String applyForCreditCard(Principal principal, Model model) {
//
//		List<CardType> cardTypes = cardTypeService.findAllCardTypes();
//		model.addAttribute("cardTypes", cardTypes);
//		return "apply-creditcard"; // Name of your Thymeleaf template
//
//	}

	// new method to get customers to register for 3 distinct cards
	@GetMapping("/apply-creditcard")
	public String applyForCreditCard(Principal principal, Model model) {
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}

		Customer customer = optionalCustomer.get();
		List<CardType> availableCardTypes = cardTypeService.findAllCardTypes().stream()
				.filter(cardType -> !creditCardService.customerAlreadyHasCardType(customer, cardType))
				.collect(Collectors.toList());

		model.addAttribute("cardTypes", availableCardTypes);
		return "apply-creditcard";
	}

//	// post mapping for registering for a credit card
//	@PostMapping("/apply-creditcard")
//	public String registerCreditCard(Principal principal, HttpServletRequest request) {
//
//		// Find the user associated with the provided customer ID.
//		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());
//
//		// If the user is not found, redirect to the login page.
//		if (optionalCustomer.isEmpty()) {
//			return "redirect:/login";
//		}
//
//		// Get the authenticated customer session object.
//		Customer sessionCustomer = (Customer) session.getAttribute("customer");
//
//		// validation for customer - must have all their details filled up
//		if (sessionCustomer.getFirstName() == null || sessionCustomer.getLastName() == null
//				|| sessionCustomer.getEmail() == null || sessionCustomer.getPhoneNumber() == null
//				|| sessionCustomer.getNric() == null || sessionCustomer.getAddress() == null
//				|| sessionCustomer.getSalary() == null || sessionCustomer.getGender() == null
//				|| sessionCustomer.getDob() == null) {
//			System.out.println("Customer details are not filled up");
//			return ("apply-creditcard");
//		}
//
//		// other credit card attributes
//		String cardNumber = creditCardService.generateCreditCardNumber();
//		String cardLimitAsString = request.getParameter("creditCardLimit");
//		int balance = 0; // start off with no transactions yet, so no money to owe the bank
//
//		// validation for card limit - must be all numbers
//		// check if what they've entered are all digits
//		for (int i = 0; i < cardLimitAsString.length(); i++) {
//			if (!Character.isDigit(cardLimitAsString.charAt(i))) {
//				System.out.println("Card Number is not all digits");
//				return ("apply-creditcard");
//			}
//		}
//
//		// validation for \card limit - must be larger than their salary
//		int cardLimit = Integer.parseInt(cardLimitAsString); // request this
//		if (cardLimit > sessionCustomer.getSalary()) {
//			System.out.println("Salary is too low");
//			return ("apply-creditcard");
//		}
//
//		// validation for cardtype
//		Optional<CardType> optionalCardType = cardTypeService.findCardTypeByName(request.getParameter("cardType"));
//		System.out.println(request.getParameter("cardType"));
//		if (optionalCardType.isEmpty()) {
//			// no card type of such name
//			System.out.println("No such credit card type");
//			return ("apply-creditcard");
//		}
//
//		CardType cardType = optionalCardType.get();
//
//		CreditCard newCard = new CreditCard(sessionCustomer, cardNumber, balance, cardLimit, cardType);
//		newCard.setCustomer(sessionCustomer);
//		creditCardService.createCreditCard(newCard);
//		System.out.println("Successfully created a new ccredit card");
//		return ("apply-creditcard");
//	}

	@PostMapping("/apply-creditcard")
	public String registerCreditCard(Principal principal, HttpServletRequest request, Model model) {
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}

		Customer customer = optionalCustomer.get();
		if (customer.getCreditCards().size() >= 3) {
			model.addAttribute("error", "You cannot have more than 3 credit cards.");
			return "apply-creditcard";
		}

		if (customer.getFirstName() == null || customer.getLastName() == null || customer.getEmail() == null
				|| customer.getPhoneNumber() == null || customer.getNric() == null || customer.getAddress() == null
				|| customer.getSalary() == null || customer.getGender() == null || customer.getDob() == null) {
			System.out.println("Customer details are not filled up");
			return ("apply-creditcard");
		}

		String cardNumber = creditCardService.generateCreditCardNumber();
		int cardLimit = Integer.parseInt(request.getParameter("creditCardLimit"));
		if (cardLimit > customer.getSalary()) {
			model.addAttribute("error", "Card limit must be less than your salary.");
			return "apply-creditcard";
		}

		Optional<CardType> optionalCardType = cardTypeService.findCardTypeByName(request.getParameter("cardType"));
		if (optionalCardType.isEmpty()) {
			model.addAttribute("error", "Invalid card type selected.");
			return "apply-creditcard";
		}

		CardType cardType = optionalCardType.get();
		if (creditCardService.customerAlreadyHasCardType(customer, cardType)) {
			model.addAttribute("error", "You already have a credit card of this type.");
			return "apply-creditcard";
		}

		CreditCard newCard = new CreditCard(customer, cardNumber, 0, cardLimit, cardType);
		creditCardService.createCreditCard(newCard);
		billService.createBillForNewCard(newCard);

		model.addAttribute("success", "Successfully created a new credit card.");
		return "redirect:/creditcard-dashboard";
	}

	@GetMapping("/pay-creditcard-balance")
	public String payCreditcardBalance(Model model, Principal principal) {

		List<String> payamentOptions = new ArrayList<>();
		payamentOptions.add("Pay Current Balance"); // pays the full bill
		payamentOptions.add("Pay minimum"); // pays the minimum they can without getting penalised
		payamentOptions.add("Pay Custom Amount"); // pays a custom amount
		// if they choose pay a custom amount, i need to get the payment amount
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());

		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}

		// set customer and their accounts as session attributes to retrieve in view
		Customer sessionCustomer = optionalCustomer.get();
		session.setAttribute("customer", sessionCustomer);
		model.addAttribute("customer", sessionCustomer);
		model.addAttribute("accounts", sessionCustomer.getDebitAccounts());
		// model.addAttribute("payamentOptions", payamentOptions);
		return "pay-creditcard-balance"; // Name of your Thymeleaf template

	}

	@PostMapping("/pay-creditcard-balance")
	public String payCreditcardBalance(Principal principal, HttpServletRequest request, Model model,
			@RequestParam("debitAccountNumber") String debitAccountNumber,
			@RequestParam(value = "creditCardNumber") String creditCardNumber,
			// @RequestParam(value = "paymentOption") String paymentOption,
			@RequestParam(value = "paymentAmount", required = false) Double paymentAmount) {

		// Find the user associated with the provided customer ID.
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());
		String paymentOption = request.getParameter("paymentOption");

		System.out.println(paymentOption);
		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}

		// Get the authenticated customer session object.
		Customer sessionCustomer = optionalCustomer.get();

		// find debit accounts of customer
		List<DebitAccount> debitAccountsOfCustomer = sessionCustomer.getDebitAccounts();
		DebitAccount fromAccount = null;

		// verify that debit account exists
		// if they choose a debit account number for a debit account that doesnt exist,
		// exit this method
		for (DebitAccount debitAccount : debitAccountsOfCustomer) {
			if (String.valueOf(debitAccount.getAccountNumber()).equals(debitAccountNumber)) {
				fromAccount = debitAccount;
				break;

			} else {
				logger.info(
						"Debit account number selected does not belong to customer, or there is no such debit account number");
//				System.out.println("Debit account number invalid - you have no such debit accounts with account number "
//						+ debitAccountNumber);
				return ("pay-creditcard-balance");
			}
		}

		// verify if the credit card exists
		// find out which credit card they want to pay off
		List<CreditCard> creditCardsOfCustomer = sessionCustomer.getCreditCards();
		CreditCard cardToBePaidOff = creditCardService.findCardByCardNumber(creditCardNumber).get();

		// if they choose to pay off a credit card that doesnt exist, exit this method

//		for (CreditCard card : creditCardsOfCustomer) {
//			if (String.valueOf(card.getCardNumber()).equals(creditCardNumber)) {
//				cardToBePaidOff = card;
//				break;
//			} else {
//				System.out.println("Credit card number invalid - you have no such credit cards with card number "
//						+ creditCardNumber);
//				return ("pay-creditcard-balance");
//			}
//		}

		// find the balance of this credit card
		double amountPayable = 0;
		Bill bill = cardToBePaidOff.getBill();

		if (paymentOption.equals("minimum")) {

			amountPayable = cardToBePaidOff.getBill().getMinimumAmountDue();
			billService.recordMinimumAmountPayment(bill);
			logger.debug("Customer selected to pay minimum amount");
//			System.out.println("Paid minimum");
		} else if (paymentOption.equals("outstanding")) {
			// pay the outstanding bill
			amountPayable = cardToBePaidOff.getBill().getOutstandingAmount();
			billService.recordOutstandingAmountPayment(bill);
			logger.debug("Customer selected to pay outstanding amount");
//			System.out.println("Paid Outstanding");

		} else if (paymentOption.equals("paymentAmount")) {
			// make sure that payMent amount is not more than the current balance
			if (paymentAmount > cardToBePaidOff.getBalance()) {
				System.out.println("Payment amount is more than the current balance");
				return ("pay-creditcard-balance");
			} else {
//				billService.recordCustomPayment(bill, amountPayable);
				amountPayable = paymentAmount;
				// need to record it in bill service
			}
		} else {
			billService.recordCreditBalancePayment(bill);
			amountPayable = cardToBePaidOff.getBalance();
			logger.debug("Customer selected to pay current balance");
//			System.out.println("Paid Balance");

		}

		// withdraw from their debit account the amount payable
		if (amountPayable > 0 && fromAccount.getAccountBalance() > amountPayable) {
			// do withdrawal
			debitAccountService.changeAccountBalance(fromAccount, amountPayable, false);
//			System.out.println("amountPayable: " + amountPayable);

			// Debit account transaction
			DebitAccountTransaction newTransaction = new DebitAccountTransaction();
			newTransaction.setDebitAccountTransactionAmount(Double.parseDouble(String.format("%.2f", amountPayable)));
			newTransaction.setDebitAccountTransactionType("transfer");
			newTransaction.setFromAccount(fromAccount);
			newTransaction.setToAccountNumber(cardToBePaidOff.getCardNumber());
			debitAccountTransactionService.createDebitAccountTransaction(newTransaction);

			// Credit card transaction
			CreditCardTransaction newCreditCardTransaction = new CreditCardTransaction();
			newCreditCardTransaction.setTransactionAmount(Double.parseDouble(String.format("%.2f", amountPayable)));
			newCreditCardTransaction.setTransactionDate(LocalDateTime.now());
			newCreditCardTransaction
					.setDescription("Made credit card payment +$" + String.format("%.2f", amountPayable));
			newCreditCardTransaction.setTransactionCard(cardToBePaidOff);
			creditCardTransactionService.createCreditCardTransaction(newCreditCardTransaction);

			cardToBePaidOff.setBalance(cardToBePaidOff.getBalance() - amountPayable);
			creditCardService.updateCard(cardToBePaidOff);

//			System.out.println("Successfully withdrawn " + amountPayable + " from " + fromAccount.getAccountNumber());
			logger.debug("Withdrawn $" + amountPayable + "from debit account to pay credit card");
			return "redirect:/creditcard-dashboard";
		} else {
			logger.debug("Amount payable is equal to zero, or account balance is too low");
//			System.out.println("No amount to be paid");
			return ("pay-creditcard-balance");
		}

	}

	@PostMapping("/view-card-bill")
	public String viewCardBill(Principal principal, Model model, HttpServletRequest request) {
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}
		long cardId = new BigDecimal(request.getParameter("cardId")).longValue();
		CreditCard card = creditCardService.findCardByCardId(cardId).orElse(null);
		Bill bill = billService.findBillByCreditCard(card).orElse(null);

//		Customer customer = optionalCustomer.get();

		model.addAttribute("bill", bill);
		return "view-card-bill";
	}

	@PostMapping("/open-card-payment")
	public String openCardPayment(Principal principal, Model model, HttpServletRequest request) {
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}
		long cardId = new BigDecimal(request.getParameter("cardId")).longValue();
		CreditCard card = creditCardService.findCardByCardId(cardId).orElse(null);
		Bill bill = billService.findBillByCreditCard(card).orElse(null);

		Customer customer = optionalCustomer.get();

		model.addAttribute("bill", bill);
		model.addAttribute("card", card);
		model.addAttribute("customer", customer);
		model.addAttribute("accounts", customer.getDebitAccounts());
		return "pay-creditcard-balance";
	}

}
