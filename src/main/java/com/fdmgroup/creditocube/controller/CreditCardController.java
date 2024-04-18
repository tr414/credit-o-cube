package com.fdmgroup.creditocube.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.fdmgroup.creditocube.model.CardType;
import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.service.CardTypeService;
import com.fdmgroup.creditocube.service.CreditCardService;
import com.fdmgroup.creditocube.service.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CreditCardController {

	@Autowired
	private CreditCardService creditCardService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CardTypeService cardTypeService;

	// credit card dashboard
	@GetMapping("/creditcard-dashboard")
	public String creditCardDashboard(Principal principal, Model model) {
		return "creditcard-dashboard";
	}

	// Apply for credit card
	@GetMapping("/apply-creditcard")
	public String applyForCreditCard(Principal principal, Model model) {

		List<CardType> cardTypes = cardTypeService.findAllCardTypes();
		model.addAttribute("cardTypes", cardTypes);
		return "apply-creditcard"; // Name of your Thymeleaf template

	}

	// post mapping for registering for a credit card
	@PostMapping("/apply-creditcard")
	public String registerCreditCard(Principal principal, HttpServletRequest request) {

		// Find the user associated with the provided customer ID.
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());

		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}

		// Get the authenticated customer session object.
		Customer sessionCustomer = optionalCustomer.get();

		// validation for customer - must have all their details filled up
		if (sessionCustomer.getFirstName() == null || sessionCustomer.getLastName() == null
				|| sessionCustomer.getEmail() == null || sessionCustomer.getPhoneNumber() == null
				|| sessionCustomer.getNric() == null || sessionCustomer.getAddress() == null
				|| sessionCustomer.getSalary() == null || sessionCustomer.getGender() == null
				|| sessionCustomer.getDob() == null) {
			System.out.println("Customer details are not filled up");
			return ("apply-creditcard");
		}

		// other credit card attributes
		String cardNumber = creditCardService.generateCreditCardNumber();
		String cardLimitAsString = request.getParameter("creditCardLimit");
		int balance = 0; // start off with no transactions yet, so no money to owe the bank

		// validation for card limit - must be all numbers
		// check if what they've entered are all digits
		for (int i = 0; i < cardLimitAsString.length(); i++) {
			if (!Character.isDigit(cardLimitAsString.charAt(i))) {
				System.out.println("Card Number is not all digits");
				return ("apply-creditcard");
			}
		}

		// validation for card limit - must be larger than their salary
		int cardLimit = Integer.parseInt(cardLimitAsString); // request this
		if (cardLimit > sessionCustomer.getSalary()) {
			System.out.println("Salary is too low");
			return ("apply-creditcard");
		}

		// validation for cardtype
		Optional<CardType> optionalCardType = cardTypeService.findCardTypeByName(request.getParameter("cardType"));
		System.out.println(request.getParameter("cardType"));
		if (optionalCardType.isEmpty()) {
			// no card type of such name
			System.out.println("No such credit card type");
			return ("apply-creditcard");
		}

		CardType cardType = optionalCardType.get();

		CreditCard newCard = new CreditCard(sessionCustomer, cardNumber, balance, cardLimit, cardType);
		newCard.setCustomer(sessionCustomer);
		creditCardService.createCreditCard(newCard);
		System.out.println("Successfully created a new ccredit card");
		return ("apply-creditcard");
	}

}
