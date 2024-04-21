package com.fdmgroup.creditocube.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClient;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.CreditCardTransaction;
import com.fdmgroup.creditocube.model.CurrencyExchange;
import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.ForeignCurrencyCreditCardTransaction;
import com.fdmgroup.creditocube.model.Merchant;
import com.fdmgroup.creditocube.repository.MerchantRepository;
import com.fdmgroup.creditocube.service.CreditCardService;
import com.fdmgroup.creditocube.service.CreditCardTransactionService;
import com.fdmgroup.creditocube.service.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CreditCardTransactionController {

	@Autowired
	CreditCardTransactionService transactionService;

	@Autowired
	CreditCardService cardService;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	MerchantRepository merchantRepo;
	
	private final RestClient restClient;
	
	@Value("${API_KEY}")
	private String apiKey;
	
	public CreditCardTransactionController() {
		restClient = RestClient.builder()
				.baseUrl("http://api.exchangeratesapi.io/v1/latest")
				.build();
	}

	@PostMapping("/card-transactions")
	public String getAllCardTransactions(Model model, HttpServletRequest request) {
		BigDecimal cardId = new BigDecimal(request.getParameter("cardId"));

		CreditCard card = cardService.findCardByCardId(cardId.longValue()).orElse(null);

		List<CreditCardTransaction> cardTransactions = transactionService.findAllCreditCardTransactions(card);
		model.addAttribute("transactions", cardTransactions);
		return ("card-transactions");
	}
	
	@GetMapping("/create-card-transaction") 
	public String getCreateCardTransaction(Model model, Principal principal){
		String username = principal.getName();
		Customer customer = customerService.findCustomerByUsername(username).orElse(null);
		
		List<CreditCard> customerCards = cardService.findAllCardsForCustomer(customer);
		List<Merchant> merchantCodes = merchantRepo.findAll();
		
		model.addAttribute("merchants", merchantCodes);
		model.addAttribute("cards", customerCards);
	
		
		return("create-card-transaction");
	}
	
	@PostMapping("/create-card-transaction")
	public String createCardTransaction(HttpServletRequest request) {
		BigDecimal cardId = new BigDecimal(request.getParameter("cardId"));
		CreditCard card = cardService.findCardByCardId(cardId.longValue()).orElse(null);
		
		String merchantCode = request.getParameter("merchantCode");
		Merchant merchant = merchantRepo.findByMerchantCode(merchantCode).orElse(null);
		
		// currency
		String currency = request.getParameter("currency");
		System.out.println(currency);
		
		//date
		LocalDateTime transactionDate = LocalDateTime.now();
		
		//amount
		BigDecimal amount = new BigDecimal(request.getParameter("amount"));
		double transactionAmount = amount.doubleValue();
		
		// TODO apply cashback
		double cashback = 0.0;
		
		// TODO check if transaction is valid based on card limit, current balance, and transaction amount
		boolean valid = true;
		
		
		
		if (currency.equalsIgnoreCase("sgd")) {
			if (validTransaction(transactionAmount, card)) {
				transactionService.createCreditCardTransaction(new CreditCardTransaction(card, merchant, cashback, transactionDate, transactionAmount));
			} 
			
		} else {
			
			CurrencyExchange forexResponse =  restClient.get()
			.uri("?access_key={apiKey}", apiKey)
			.retrieve()
			.body(CurrencyExchange.class);
		
			BigDecimal exchangeRate = forexResponse.exchangeRateToSGD(currency).setScale(5, RoundingMode.HALF_UP);
			double transactionSGDAmount = exchangeRate.multiply(amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
			
			transactionService.createCreditCardTransaction(new ForeignCurrencyCreditCardTransaction(card, merchant, cashback, transactionDate, transactionSGDAmount, currency, exchangeRate.doubleValue()));
		}
			
		
		
		return ("redirect:creditcard-dashboard");
	}

	private boolean validTransaction(double transactionAmount, CreditCard card) {
		if (transactionAmount + card.getBalance() < card.getCardLimit()) {
			return true;
		}
		return false;
	}

}
