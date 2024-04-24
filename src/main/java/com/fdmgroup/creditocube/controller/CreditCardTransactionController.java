package com.fdmgroup.creditocube.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.CreditCardTransaction;
import com.fdmgroup.creditocube.model.CurrencyExchange;
import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.ForeignCurrencyCreditCardTransaction;
import com.fdmgroup.creditocube.model.Merchant;
import com.fdmgroup.creditocube.model.Rewards;
import com.fdmgroup.creditocube.repository.RewardsRepository;
import com.fdmgroup.creditocube.service.CreditCardService;
import com.fdmgroup.creditocube.service.CreditCardTransactionService;
import com.fdmgroup.creditocube.service.CustomerService;
import com.fdmgroup.creditocube.service.MerchantService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CreditCardTransactionController {

	@Autowired
	CreditCardTransactionService transactionService;

	@Autowired
	HttpSession session;

	@Autowired
	CreditCardService cardService;

	@Autowired
	CustomerService customerService;

	@Autowired
	MerchantService merchantService;

	@Autowired
	RewardsRepository rewardsRepo;

	private final RestClient restClient;

	private static Logger logger = LogManager.getLogger(CreditCardTransactionController.class);

	@Value("${API_KEY}")
	private String apiKey;

	public CreditCardTransactionController() {
		restClient = RestClient.builder().baseUrl("http://api.exchangeratesapi.io/v1/latest").build();
	}

	@RequestMapping("/card-transactions")
	public String getAllCardTransactions(Model model, HttpServletRequest request, Principal principal) {

		String stringCardId = request.getParameter("cardId");
		BigDecimal cardId;
		if (stringCardId != null) {
			cardId = new BigDecimal(request.getParameter("cardId"));
		} else {
			return "redirect:creditcard-dashboard";
		}

		// amended code above to return optional rather than null - tim
		Optional<CreditCard> optionalCard = cardService.findCardByCardId(cardId.longValue());
		if (optionalCard.isEmpty()) {
			return "redirect:/creditcard-dashboard";
		}
		CreditCard card = optionalCard.get();

		List<CreditCardTransaction> cardTransactions = transactionService.findAllCreditCardTransactions(card);
		model.addAttribute("transactions", cardTransactions);
		model.addAttribute("cardId", cardId);
//		session.setAttribute("stringCardId", stringCardId);

		return ("card-transactions");
	}

	@GetMapping("/create-card-transaction")
	public String getCreateCardTransaction(Model model, Principal principal) {
		String username = principal.getName();

		// Customer customer =
		// customerService.findCustomerByUsername(username).orElse(null);
		// amended code above to return optional rather than null - tim
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(username);
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}
		Customer customer = optionalCustomer.get();

//		List<CreditCard> customerCards = cardService.findAllCardsForCustomer(customer);
		List<CreditCard> customerCards = cardService.findAllActiveCreditCardsForCustomer(customer);

		List<Merchant> merchantCodes = merchantService.findAllMerchants();

		model.addAttribute("merchants", merchantCodes);
		model.addAttribute("cards", customerCards);

		return ("create-card-transaction");
	}

	@PostMapping("/create-card-transaction")
	public String createCardTransaction(HttpServletRequest request) {
		BigDecimal cardId = new BigDecimal(request.getParameter("cardId"));
		System.out.println(request.getParameter("cardId"));

		// CreditCard card =
		// cardService.findCardByCardId(cardId.longValue()).orElse(null);
		// amended code above to return optional rather than null - tim
		Optional<CreditCard> optionalCard = cardService.findCardByCardId(cardId.longValue());
		if (optionalCard.isEmpty()) {
			return "redirect:/creditcard-dashboard";
		}
		CreditCard card = optionalCard.get();

		String merchantCode = request.getParameter("merchantCode");
		Merchant merchant;
		Optional<Merchant> merchantOptional = merchantService.findMerchantByMerchantCode(merchantCode);

		boolean installmentPayment = (request.getParameter("installment") != null);
		System.out.println(installmentPayment);

		if (merchantOptional.isEmpty()) {
			return "redirect:create-card-transaction";
		} else {
			merchant = merchantOptional.get();
		}

		String merchantCategory = merchant.getCategory();

		// find reward based on category of transaction and card type
		Rewards reward = rewardsRepo.findByCardTypeIsAndCategoryIs(card.getCardType(), merchantCategory).orElse(null);

		double cashbackRate = 0.0;

		if (reward != null) {
			cashbackRate = reward.getCashback_rate() / 100;
		}

		// currency
		String currency = request.getParameter("currency");

		// date
		LocalDateTime transactionDate = LocalDateTime.now();

		// amount
		BigDecimal amount = new BigDecimal(request.getParameter("amount")).setScale(2, RoundingMode.HALF_UP);
		double transactionAmount = amount.doubleValue();

		// TODO apply cashback

		if (currency.equalsIgnoreCase("sgd")) {
			if (validTransaction(transactionAmount, card)) {
				card.setBalance(card.getBalance() + transactionAmount);

				// calculate cashback based on merchant category and card reward
				double cashback = new BigDecimal(transactionAmount * cashbackRate).setScale(2, RoundingMode.HALF_UP)
						.doubleValue();
				card.setCashback(card.getCashback() + cashback);

				// add the transaction to monthly spend on the card for eventual cashback
				// eligibility check
				card.setMonthlySpend(transactionAmount + card.getMonthlySpend());

				cardService.updateCard(card);

				String description;

				// If the transaction is an installment payment, create the installment payment
				// and associate it with the card
				// Inform the customer through the description that this transaction will be
				// paid in installments over the next 6 months
				if (installmentPayment) {
					transactionService.createInstallmentPayment(card, transactionAmount);
					description = "This transaction will be paid in 6 installments which will be charged to your card over the next 6 months.";

				} else {
					description = String.format("Payment made to merchant code %s in category %s", merchantCode,
							merchantCategory);
				}
				System.out.println("Transaction in sgd: " + description);
				transactionService.createCreditCardTransaction(new CreditCardTransaction(card, merchant, cashback,
						transactionDate, transactionAmount, description));
				System.out.println("Transaction created");
			} else {
				return "redirect:creditcard-dashboard";
			}

		} else {

			CurrencyExchange forexResponse = restClient.get().uri("?access_key={apiKey}", apiKey).retrieve()
					.body(CurrencyExchange.class);

			BigDecimal exchangeRate = forexResponse.exchangeRateToSGD(currency).setScale(5, RoundingMode.HALF_UP);
			double transactionSGDAmount = exchangeRate.multiply(amount).setScale(2, RoundingMode.HALF_UP).doubleValue();

			if (validTransaction(transactionSGDAmount, card)) {
				card.setBalance(card.getBalance() + transactionSGDAmount);

				double cashback = new BigDecimal(transactionSGDAmount * cashbackRate).setScale(2, RoundingMode.HALF_UP)
						.doubleValue();
				card.setCashback(card.getCashback() + cashback);

				// add the transaction to monthly spend on the card for eventual cashback
				// eligibility check
				card.setMonthlySpend(transactionSGDAmount + card.getMonthlySpend());

				cardService.updateCard(card);

				String description;

				if (installmentPayment) {
					transactionService.createInstallmentPayment(card, transactionSGDAmount);
					description = String.format(
							"This transaction will be paid in 6 installments which will be charged to your card over the next 6 months. Original currency: %s. Exchange rate from currency to SGD: %s",
							currency, exchangeRate.toString());

				} else {
					description = String.format(
							"Foreign currency payment made to merchant code %s in category %s. Original currency: %s. Exchange rate from currency to SGD: %s",
							merchantCode, merchantCategory, currency, exchangeRate.toString());
				}

				transactionService.createCreditCardTransaction(
						new ForeignCurrencyCreditCardTransaction(card, merchant, cashback, transactionDate,
								transactionSGDAmount, description, currency, exchangeRate.doubleValue()));
			} else {
				return "redirect:creditcard-dashboard";
			}

		}

//		if (currency.equalsIgnoreCase("sgd")) {
//			CreditCardTransaction newTransaction = new CreditCardTransaction(card, merchant, cashback, transactionDate,
//					transactionAmount);
//			transactionService.createCreditCardTransaction(newTransaction);
//			cardService.updateBalance(card, newTransaction);
//		} else {
//
//			CurrencyExchange forexResponse = restClient.get().uri("?access_key={apiKey}", apiKey).retrieve()
//					.body(CurrencyExchange.class);
//
//			BigDecimal exchangeRate = forexResponse.exchangeRateToSGD(currency).setScale(5, RoundingMode.HALF_UP);
//			double transactionSGDAmount = exchangeRate.multiply(amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
//
//			CreditCardTransaction newTransaction = new ForeignCurrencyCreditCardTransaction(card, merchant, cashback,
//					transactionDate, transactionSGDAmount, currency, exchangeRate.doubleValue());
//			transactionService.createCreditCardTransaction(newTransaction);
//			cardService.updateBalance(card, newTransaction);
//		}

		return ("redirect:creditcard-dashboard");
	}

	private boolean validTransaction(double transactionAmount, CreditCard card) {
		if (transactionAmount + card.getBalance() < card.getCardLimit()) {
			return true;
		}
		return false;
	}

	@PostMapping("/find-by-date")
	public String findByDate(@RequestParam("dateFrom") LocalDate dateFrom, @RequestParam("dateTo") LocalDate dateTo,
			Model model, HttpServletRequest request, @RequestParam("cardId") Long cardId) {

		System.out.println(cardId);

		Optional<CreditCard> optionalCard = cardService.findCardByCardId(cardId.longValue());
		if (optionalCard.isEmpty()) {
			return "redirect:/creditcard-dashboard";
		}
		CreditCard card = optionalCard.get();
		LocalDateTime endDateTime = dateTo.atTime(23, 59, 59, 999999999);
		LocalDateTime startDateTime = dateFrom.atTime(0, 0, 0, 0);

		List<CreditCardTransaction> transactions = transactionService.findByTransactionDate(startDateTime, endDateTime,
				card);
		model.addAttribute("transactions", transactions);
		model.addAttribute("cardId", cardId);

		return "card-transactions";
	}

	@PostMapping("/find-by-month")
	public String findByMonth(@RequestParam("month") Integer month, Model model, HttpServletRequest request,
			@RequestParam("cardId") Long cardId) {

		Optional<CreditCard> optionalCard = cardService.findCardByCardId(cardId.longValue());
		if (optionalCard.isEmpty()) {
			return "redirect:/creditcard-dashboard";
		}
		CreditCard card = optionalCard.get();

		List<CreditCardTransaction> transactionsThatMonth;
		for (int x = 1; x <= 12; x++) {
			if (month == x) {
				transactionsThatMonth = transactionService.findTransactionsByMonth(month, card);
				model.addAttribute("transactions", transactionsThatMonth);
				return "card-transactions";

			} else {
				System.out.println("No transactions this month");
			}
		}
		model.addAttribute("cardId", cardId);
		return "card-transactions";

	}

}
