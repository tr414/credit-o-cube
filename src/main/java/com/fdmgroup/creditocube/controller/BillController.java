package com.fdmgroup.creditocube.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.service.BillService;
import com.fdmgroup.creditocube.service.CreditCardService;
import com.fdmgroup.creditocube.service.CreditCardTransactionService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BillController {

	@Autowired
	CreditCardService cardService;
	
	@Autowired
	CreditCardTransactionService cardTransactionService;
	
	@Autowired
	BillService billService;
	
	@PostMapping("/generate-bill")
	public String generateBill(HttpServletRequest request) {
		BigDecimal cardId = new BigDecimal(request.getParameter("cardId"));
		
		Optional<CreditCard> optionalCard = cardService.findCardByCardId(cardId.longValue());
		if (optionalCard.isEmpty()) {
			return "redirect:/creditcard-dashboard";
		}
		CreditCard card = optionalCard.get();
		
		billService.generateBill(cardId.longValue());
		
		return "redirect:/creditcard-dashboard";
	}

}
