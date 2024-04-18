package com.fdmgroup.creditocube.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.CreditCardTransaction;
import com.fdmgroup.creditocube.service.CreditCardTransactionService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CreditCardTransactionController {

	@Autowired
	CreditCardTransactionService transactionService;
	
//	@Autowired
//	CreditCardService cardService;
	
	@PostMapping("/card-transactions")
	public String getAllCardTransactions(Model model, HttpServletRequest request) {
		BigDecimal cardId = new BigDecimal(request.getParameter("cardId"));
		
		//CreditCard card = cardService.findCreditCardById(cardId.longValue()).orElse(null);
		CreditCard card = new CreditCard();
		
		List<CreditCardTransaction> cardTransactions = transactionService.findAllCreditCardTransactions(card);
		model.addAttribute("transactions", cardTransactions);
		return("card-transactions");
	}
	

}
