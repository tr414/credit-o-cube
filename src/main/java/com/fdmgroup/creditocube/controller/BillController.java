package com.fdmgroup.creditocube.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.CreditCardTransaction;
import com.fdmgroup.creditocube.service.CreditCardService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BillController {

	@Autowired
	CreditCardService cardService;

	@PostMapping("/pay-outstanding-balance")
	public String payOutstandingBalance(Model model, HttpServletRequest request) {
		BigDecimal cardId = new BigDecimal(request.getParameter("cardId"));

//		CreditCard card = cardService.findCardByCardId(cardId.longValue()).orElse(null);
		// amended code above to return optional rather than null - tim
		Optional<CreditCard> optionalCard = cardService.findCardByCardId(cardId.longValue());
		if (optionalCard.isEmpty()) {
			return "redirect:/creditcard-dashboard";
		}
		CreditCard card = optionalCard.get();

		List<CreditCardTransaction> cardTransactions = transactionService.findAllCreditCardTransactions(card);
		model.addAttribute("transactions", cardTransactions);

		return ("card-transactions");
	}

	@PostMapping("/pay-minimum-balance")
	public String payMinimumBalance(Model model, HttpServletRequest request) {
		BigDecimal cardId = new BigDecimal(request.getParameter("cardId"));

//		CreditCard card = cardService.findCardByCardId(cardId.longValue()).orElse(null);
		// amended code above to return optional rather than null - tim
		Optional<CreditCard> optionalCard = cardService.findCardByCardId(cardId.longValue());
		if (optionalCard.isEmpty()) {
			return "redirect:/creditcard-dashboard";
		}
		CreditCard card = optionalCard.get();

		List<CreditCardTransaction> cardTransactions = transactionService.findAllCreditCardTransactions(card);
		model.addAttribute("transactions", cardTransactions);

		return ("card-transactions");
	}
}
