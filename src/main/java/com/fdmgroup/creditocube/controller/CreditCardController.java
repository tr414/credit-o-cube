package com.fdmgroup.creditocube.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.service.CreditCardService;
import com.fdmgroup.creditocube.service.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CreditCardController {

	@Autowired
	private CreditCardService creditCardService;

	@Autowired
	private CustomerService customerService;

	// credit card dashboard
	@GetMapping("/creditcard-dashboard")
	public String creditCardDashboard(Principal principal, Model model) {
		return "creditcard-dashboard";
	}
	
	// Apply for credit card
    @GetMapping("/apply-creditcard")
    public String applyForCreditCard(Principal principal, Model model) {
        return "apply-creditcard";
    }

	// post mapping for registering for a credit card
	@PostMapping("/credit-card-registration")
	public String registerCreditCard(Principal principal, HttpServletRequest request) {
		// this method takes in the params that they fill in from the form
		// I save those parameters using creditCardService

		// validation
		/*
		 * 1. cannot create a card if they already have 3 or more cards 2. cannot create
		 * a card if their salary is below $3000 3. Cannot create a card if they haven't
		 * filled up all of their customer details 4. cannot ask for a limit that's
		 * above their salary
		 * 
		 */

		// Find the user associated with the provided customer ID.
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());

		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}

		// things i need to make a new credit cad: String cardNumber, int balance, int
		// cardLimit, CardType cardType
		String creditCardNumber = creditCardService.generateCreditCardNumber();
		int balance = 0; // start off with no transactions yet, so no money to owe the bank
		int cardLimit = Integer.parseInt(request.getParameter("credit-card-limit")); // request this
		String cardTypeName = request.getParameter("card-type");

		// Get the authenticated customer session object.
		Customer sessionCustomer = optionalCustomer.get();
		CreditCard newCard = new CreditCard();
		newCard.setCustomer(sessionCustomer);
		creditCardService.createCreditCard(newCard);
		System.out.println("Successfully created a new ccredit card");
		return ("credit-card-registration");
	}

}
