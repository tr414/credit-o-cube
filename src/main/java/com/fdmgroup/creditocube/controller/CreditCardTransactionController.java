package com.fdmgroup.creditocube.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.CreditCardTransaction;
import com.fdmgroup.creditocube.model.Customer;
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
		
		//date
		LocalDateTime transactionDate = LocalDateTime.now();
		
		//amount
		BigDecimal amount = new BigDecimal(request.getParameter("amount"));
		double transactionAmount = amount.doubleValue();
		
		// TODO apply cashback
		double cashback = 0.0;
		
		// TODO check if transaction is valid based on card limit, current balance, and transaction amount
		boolean valid = true;
		
		
		if (valid) {
			new CreditCardTransaction(card, merchant, cashback, transactionDate, transactionAmount);
		}
		
		return ("redirect:creditcard-dashboard");
	}

}
