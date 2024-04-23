package com.fdmgroup.creditocube.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.fdmgroup.creditocube.model.Bill;
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
		
		billService.generateBill(cardId.longValue());
		
		return "redirect:/creditcard-dashboard";
	}
	
	@PostMapping("/simulate-late-payment")
	public String simulateLatePaymentAndInterest(HttpServletRequest request) {
		long cardId = new BigDecimal(request.getParameter("cardId")).longValue();
		Bill bill = billService.findBillByCreditCard(cardService.findCardByCardId(cardId).get()).get();
		billService.checkLatePayment(bill);
		
		return "redirect:/creditcard-dashboard";
	}

}
