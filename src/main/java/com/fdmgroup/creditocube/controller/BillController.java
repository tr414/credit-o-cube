package com.fdmgroup.creditocube.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import com.fdmgroup.creditocube.model.Bill;
import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.CreditCardTransaction;
import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.service.BillService;
import com.fdmgroup.creditocube.service.CreditCardService;
import com.fdmgroup.creditocube.service.CreditCardTransactionService;
import com.fdmgroup.creditocube.service.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BillController {

	@Autowired
	CreditCardService cardService;
	
	@Autowired
	CreditCardTransactionService cardTransactionService;
	
	@Autowired
	CustomerService customerService;
	
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
	
	@PostMapping("/view-card-bill")
	public String viewCardBill(Principal principal, Model model, HttpServletRequest request) {
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}
		long cardId = new BigDecimal(request.getParameter("cardId")).longValue();
		CreditCard card = cardService.findCardByCardId(cardId).orElse(null);
		Bill bill = billService.findBillByCreditCard(card).orElse(null);
		
		List<CreditCardTransaction> billingCycleTransactions = billService.findBillingCycleTransactions(bill);

//		Customer customer = optionalCustomer.get();

		model.addAttribute("bill", bill);
		model.addAttribute("transactions", billingCycleTransactions);
		return "view-card-bill";
	}

}
