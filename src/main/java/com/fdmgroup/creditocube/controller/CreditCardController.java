package com.fdmgroup.creditocube.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CreditCardController {

	// credit card dashboard
	@GetMapping("/creditcard-dashboard")
	public String creditCardDashboard(Principal principal, Model model) {
		return "creditcard-dashboard";
	}

}
