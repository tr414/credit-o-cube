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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fdmgroup.creditocube.model.Bill;
import com.fdmgroup.creditocube.model.CardType;
import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.CreditCardTransaction;
import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.model.DebitAccountTransaction;
import com.fdmgroup.creditocube.model.Merchant;
import com.fdmgroup.creditocube.service.BillService;
import com.fdmgroup.creditocube.service.CardTypeService;
import com.fdmgroup.creditocube.service.CreditCardService;
import com.fdmgroup.creditocube.service.CreditCardTransactionService;
import com.fdmgroup.creditocube.service.CustomerService;
import com.fdmgroup.creditocube.service.DebitAccountService;
import com.fdmgroup.creditocube.service.DebitAccountTransactionService;
import com.fdmgroup.creditocube.service.InstallmentPaymentService;
import com.fdmgroup.creditocube.service.MerchantService;

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

	@Autowired
	private InstallmentPaymentService installmentService;

	@Autowired
	private MerchantService merchantService;

	private static Logger logger = LogManager.getLogger(CreditCardController.class);

	// credit card dashboard
	@GetMapping("/creditcard-dashboard")
	public String creditCardDashboard(Principal principal, Model model) {
		// Find the user associated with the provided customer ID.
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());
		
		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			logger.info("Could not find customer with username: {}", principal.getName());
			return "redirect:/login";
		}

		// set customer and their accounts as session attributes to retrieve in view
		Customer sessionCustomer = optionalCustomer.get();
		session.setAttribute("customer", sessionCustomer);
		model.addAttribute("customer", sessionCustomer);

		List<CreditCard> activeCreditCards = creditCardService.findAllActiveCreditCardsForCustomer(sessionCustomer);
		model.addAttribute("credit_cards", activeCreditCards);
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
		List<CardType> availableCardTypes = new ArrayList<>();
		List<CardType> allTypesOfActiveCardsOfCustomer = creditCardService.findAllActiveCreditCardsForCustomer(customer)
				.stream().map(CreditCard::getCardType).distinct().collect(Collectors.toList());
		for (CardType cardType : cardTypeService.findAllCardTypes()) {
			if (!allTypesOfActiveCardsOfCustomer.contains(cardType)) {
				availableCardTypes.add(cardType);
			}

		}
		// search all active credit cards of this customer
		// if customer already has an active card of a certain type, don't add that card
		// type to the list of availableCardTypes
		// give me all the card types
		// if the customer has an active card of that card type, that card type cannot
		// be added to the availableCardTypes list

		// if customer doesn't have a card of a certain type
//		List<CardType> availableCardTypes = cardTypeService.findAllCardTypes().stream()
//				.filter(cardType -> !creditCardService.customerAlreadyHasCardType(customer, cardType))
//				.collect(Collectors.toList());

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

	public String registerCreditCard(Principal principal, HttpServletRequest request,
			RedirectAttributes redirectAttrs) {
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}

		Customer customer = optionalCustomer.get();
		List<CreditCard> cardList = customer.getCreditCards();
		List<CreditCard> activeCardList = cardList.stream().filter(c -> c.isActive()).collect(Collectors.toList());

		// if they already hold 3 or more cards, cannot make new ones
		if (activeCardList.size() >= 3) {
			redirectAttrs.addFlashAttribute("error", "You cannot have more than 3 credit cards.");
			return "redirect:/apply-creditcard";
		}

		if (customer.getFirstName() == null || customer.getLastName() == null || customer.getEmail() == null
				|| customer.getPhoneNumber() == null || customer.getNric() == null || customer.getAddress() == null
				|| customer.getSalary() == null || customer.getGender() == null || customer.getDob() == null) {
//			System.out.println("Customer details are not filled up");
			redirectAttrs.addFlashAttribute("error", "Please fill up your personal details");
			return ("redirect:/apply-creditcard");
		}

		String cardNumber = creditCardService.generateCreditCardNumber();
		int cardLimit = Integer.parseInt(request.getParameter("creditCardLimit"));
		if (cardLimit > customer.getSalary()) {
			redirectAttrs.addFlashAttribute("cardLimitAboveSalary", "Card limit must be less than your salary.");
			return "redirect:/apply-creditcard";
		} else if (cardLimit < 0) {
			redirectAttrs.addFlashAttribute("cardLimitNegative", "Card limit must be positive.");
			return "redirect:/apply-creditcard";
		}

		Optional<CardType> optionalCardType = cardTypeService.findCardTypeByName(request.getParameter("cardType"));
		if (optionalCardType.isEmpty()) {
			redirectAttrs.addFlashAttribute("error", "Invalid card type selected.");
			return "redirect:/apply-creditcard";
		}

//		CardType cardType = optionalCardType.get();
//		List<CardType> allTypesOfActiveCardsOfCustomer = creditCardService.findAllActiveCreditCardsForCustomer(customer)
//				.stream().map(CreditCard::getCardType).distinct().collect(Collectors.toList());

//		if (allTypesOfActiveCardsOfCustomer.contains(cardType)) {
//			// if a customer already has an active card of card type cardType
//			model.addAttribute("error", "You already have a credit card of this type.");
//			return "apply-creditcard";
//
//		}

		CardType cardType = optionalCardType.get();
		if (creditCardService.customerAlreadyHasCardType(customer, cardType)) {
			redirectAttrs.addFlashAttribute("error", "You already have a credit card of this type.");
			return "redirect:/apply-creditcard";
		}

		CreditCard newCard = new CreditCard(customer, cardNumber, 0, cardLimit, cardType);
		creditCardService.createCreditCard(newCard);
		billService.createBillForNewCard(newCard);
		
		logger.info("Created new Credit Card with card number: {} for user: {}", cardNumber ,principal.getName());
		redirectAttrs.addFlashAttribute("success", "Successfully created a new credit card.");
		return "redirect:/creditcard-dashboard";
	}

	@PostMapping("/pay-creditcard-balance")
	public String payCreditcardBalance(Principal principal, HttpServletRequest request, Model model,
			@RequestParam("debitAccountNumber") String debitAccountNumber,
			@RequestParam(value = "creditCardNumber") String creditCardNumber,
			@RequestParam(value = "paymentAmount", required = false) Double paymentAmount,
			RedirectAttributes redirectAttrs) {

		// Find the user associated with the provided customer ID.
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());
		String paymentOption = request.getParameter("paymentOption");

		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			return "redirect:/logout";
		}

		// Get the authenticated customer session object.
		Customer sessionCustomer = optionalCustomer.get();

		// find debit accounts of customer

		List<DebitAccount> debitAccountsOfCustomer = debitAccountService
				.findAllDebitAccountsForCustomer(sessionCustomer);
		debitAccountsOfCustomer.removeIf(account -> account.isActive() == false);

		Optional<DebitAccount> fromAccountOptional = debitAccountService
				.findDebitAccountByAccountNumber(debitAccountNumber);

		if (fromAccountOptional.isEmpty()) {
			logger.info("Customer did not select valid Debit Account for transaction");
			redirectAttrs.addFlashAttribute("invalidDebitAccount", "Please select an active Debit Account");
			return "redirect:creditcard-dashboard";
		}

		DebitAccount fromAccount = fromAccountOptional.get();

		// verify if the credit card exists
		// find out which credit card they want to pay off
		List<CreditCard> creditCardsOfCustomer = sessionCustomer.getCreditCards();
		Optional<CreditCard> optionalCard = creditCardService.findCardByCardNumber(creditCardNumber);

		if (optionalCard.isEmpty()) {
			return "redirect:/creditcard-dashboard";
		}
		CreditCard cardToBePaidOff = optionalCard.get();

		double amountPayable = 0;

		Bill bill = cardToBePaidOff.getBill();

		// Removed the option for custom payment : Tanay
		// Also moved bill update to be done after transaction has been performed rather
		// than before
		// So that we do not update the bill in case of an invalid transaction
		if (paymentOption.equals("minimum")) {
			amountPayable = cardToBePaidOff.getBill().getMinimumAmountDue();
			logger.debug("Customer selected to pay minimum amount");
		} else if (paymentOption.equals("outstanding")) {
			// pay the outstanding bill
			amountPayable = cardToBePaidOff.getBill().getOutstandingAmount();
			logger.debug("Customer selected to pay outstanding amount");

		} else {
			amountPayable = cardToBePaidOff.getBalance();
			logger.debug("Customer selected to pay current balance");
		}
		
		
		// withdraw from their debit account the amount payable
		if (amountPayable <= 0) {
			logger.info("Invalid payment amount for credit card. Need to select amount > 0");
			redirectAttrs.addFlashAttribute("invalidPaymentAmount",
					"Payment amount is not valid, please try again after making another transaction");
			return "redirect:/creditcard-dashboard";
		}
		
		if (fromAccount.getAccountBalance() < amountPayable) {
			logger.info("Selected account does not have enough balance to pay the card");
			redirectAttrs.addFlashAttribute("insufficientBalance",
					"You have insufficient balance in your chosen account");
			return "redirect:/creditcard-dashboard";
		}
		
		if (amountPayable > 0 && fromAccount.getAccountBalance() > amountPayable) {
			// do withdrawal
			debitAccountService.changeAccountBalance(fromAccount, amountPayable, false);
			

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
			Optional<Merchant> optionalMerchant = merchantService.findMerchantByMerchantCode("1");
			Merchant credit;
			if (optionalMerchant.isEmpty()) {
				credit = null;
			}
			credit = optionalMerchant.get();
			newCreditCardTransaction.setMerchant(credit);
			creditCardTransactionService.createCreditCardTransaction(newCreditCardTransaction);

			cardToBePaidOff.setBalance(cardToBePaidOff.getBalance() - amountPayable);
			creditCardService.updateCard(cardToBePaidOff);

			logger.debug("Withdrawn $" + amountPayable + "from debit account to pay credit card");
		}

		if (paymentOption.equals("minimum")) {
			billService.recordMinimumAmountPayment(bill);
		} else if (paymentOption.equals("outstanding")) {
			billService.recordOutstandingAmountPayment(bill);

		} else {
			// Customer is paying off all the outstanding balance on the card.
			// Therefore any outstanding installment payments are also being paid and should
			// be removed from the card.
			installmentService.deleteAllInstallmentPayments(cardToBePaidOff);
			billService.recordCreditBalancePayment(bill);
		}

		return "redirect:/creditcard-dashboard";

	}

	@PostMapping("/open-card-payment")
	public String openCardPayment(Principal principal, Model model, HttpServletRequest request) {
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}
		long cardId = new BigDecimal(request.getParameter("cardId")).longValue();

		Optional<CreditCard> optionalCreditCard = creditCardService.findCardByCardId(cardId);
		CreditCard card = optionalCreditCard.get();

		Optional<Bill> optionalBill = billService.findBillByCreditCard(card);
		Bill bill = optionalBill.get();

		Customer customer = optionalCustomer.get();

		model.addAttribute("bill", bill);
		model.addAttribute("card", card);
		model.addAttribute("customer", customer);
		List<DebitAccount> availableAccounts = debitAccountService.findAllDebitAccountsForCustomer(customer);
		availableAccounts.removeIf(account -> account.isActive() == false);
		model.addAttribute("accounts", availableAccounts);
		return "pay-creditcard-balance";
	}

	@PostMapping("/close-credit-card")
	public String closeDebitAccount(@SessionAttribute Customer customer, @RequestParam long cardId,
			RedirectAttributes redirectAttrs) {

		// Find the user associated with the provided customer ID.
		Optional<Customer> optionalCustomer = customerService.findCustomerById(customer.getUser_id());

		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			logger.info("Customer not found in database, redirect to login page");
			return "redirect:/login";
		}

		logger.debug("Customer exists, Customer details retrieved from database");

		// Find the card associated with the provided account number.
		Optional<CreditCard> optionalCreditCard = creditCardService.findCardByCardId(cardId);

		// If the debit account is not found, redirect to the login page.
		if (optionalCreditCard.isEmpty()) {
			logger.info("Credit Card not found in database, redirect to credit card dashboard");
			return "redirect:/creditcard-dashboard";
		}

		CreditCard card = optionalCreditCard.get();
		if (card.getBalance() != 0) {
			logger.debug("Credit card has balance");
			redirectAttrs.addFlashAttribute("cardStillHasBalance",
					"The card you are trying to deactivate still has a balance, please pay off remaining balance first");
			return "redirect:/creditcard-dashboard";
		}
		
		if (card.getCashbackCarriedForward() != 0) {
			logger.debug("Credit card has balance cashback carried forward pending to be credited to the card");
			redirectAttrs.addFlashAttribute("cardStillHasBalance",
					"The card you are trying to deactivate has cashback carried forward from past months pending to be credited, "
					+ "please contact customer service to help with closing the account");
			return "redirect:/creditcard-dashboard";
		}

		logger.debug("Credit card exists, details retrieved from database");

		creditCardService.closeCreditCard(card);
		
		// when a credit card is deactivated, delete the corresponding bill for the credit card
		// this is to avoid the bill being checked for late payments in the current structure of the app
		Bill billToDelete = card.getBill();
		billService.deleteBillById(billToDelete.getId());

		// Return a redirect to the dashboard page.
		return "redirect:/creditcard-dashboard";
	}

//	@GetMapping("/show-inactive-cards")
//	public String showInactiveCards(Principal principal, Model model) {
//
//		// Find the user associated with the provided customer ID.
//		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());
//
//		// If the user is not found, redirect to the login page.
//		if (optionalCustomer.isEmpty()) {
//			return "redirect:/login";
//		}
//
//		// set customer and their accounts as session attributes to retrieve in view
//		// Customer customer = optionalCustomer.get();
//		Customer sessionCustomer = optionalCustomer.get();
//		session.setAttribute("customer", sessionCustomer);
//		model.addAttribute("customer", sessionCustomer);
//
//		List<CreditCard> inactiveCreditCards = creditCardService.findAllInactiveCreditCardsForCustomer(sessionCustomer);
//		model.addAttribute("inactive_credit_cards", inactiveCreditCards);
//		return "show-inactive-cards";
//
//	}

	@GetMapping("/update-credit-limit")
	public String showUpdateCreditLimitForm(@RequestParam long cardId, Model model, HttpServletRequest request) {
		Optional<CreditCard> card = creditCardService.findCardByCardId(cardId);
		if (!card.isPresent()) {
			return "redirect:/creditcard-dashboard"; // redirect if the card is not found
		}
		model.addAttribute("creditCard", card.get());
		return "update-credit-limit";
	}

	@PostMapping("/update-credit-limit")
	public String updateCreditLimit(@RequestParam long cardId, @RequestParam double creditLimit, Model model) {
		Optional<CreditCard> cardOpt = creditCardService.findCardByCardId(cardId);
		if (!cardOpt.isPresent()) {
			model.addAttribute("errorMessage", "Credit card not found!");
			return "redirect:/creditcard-dashboard"; // Or to an error page
		}

		CreditCard creditCard = cardOpt.get();
		// Accessing the customer's salary
		double customerSalary = creditCard.getCustomer().getSalary();

		if (creditLimit > customerSalary) {
			model.addAttribute("errorMessage", "Credit limit cannot exceed your salary.");
			model.addAttribute("creditCard", creditCard); // Ensure creditCard is still available for the form if
															// returning to it
			return "update-credit-limit"; // Stay on the page, show an error
		} else if (creditLimit <= 0) {
			model.addAttribute("errorMessage", "Credit limit must be positive");
			model.addAttribute("creditCard", creditCard); // Ensure creditCard is still available for the form if
															// returning to it
			return "update-credit-limit"; // Stay on the page, show an error
		}

		// Proceed to update if valid
		creditCard.setCardLimit(creditLimit);
		creditCardService.updateCard(creditCard);
		model.addAttribute("successMessage", "Credit limit updated successfully!");
		return "redirect:/creditcard-dashboard"; // Redirect back to the dashboard after update
	}

}
