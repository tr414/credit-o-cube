package com.fdmgroup.creditocube.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.fdmgroup.creditocube.service.CustomerService;
import com.fdmgroup.creditocube.service.DebitAccountService;
import com.fdmgroup.creditocube.service.UserService;

/**
 * The CustomerController class handles web requests related to customer
 * operations. It utilizes services to interact with user, debit account, and
 * customer data.
 */
@Controller
public class CustomerController {

	@Autowired
	UserService userService; // Service for user-related operations

	@Autowired
	DebitAccountService debitAccountService; // Service for debit account-related operations

	@Autowired
	CustomerService customerService; // Service for customer-related operations

	/**
	 * Handles the GET request for the login page. This method maps the "/login" URL
	 * to return the login view.
	 * 
	 * @return The name of the login view template to be rendered.
	 */

	@GetMapping("/login") // http://localhost:8080/login
	public String slashLogin() {
		return ("login");
	}

	/**
	 * Handles the GET request for the registration page. This method maps the
	 * "/register" URL to return the registration view where users can sign up as
	 * customers.
	 * 
	 * @return The name of the registration view template to be rendered.
	 */
	@GetMapping("/register") // http://localhost:8080/register
	public String slashRegister() {
		return ("register");
	}

	/**
	 * Processes the registration form submission. Registers a new user with the
	 * provided username and password and then redirects to the login page.
	 *
	 * @param username the username entered by the user
	 * @param password the password entered by the user
	 * @return a redirect URL to the login page after successful registration
	 */
	@PostMapping("/register")
	public String registerUser(String username, String password, String firstName, String lastName, String email,
			Integer phoneNumber, String nric, String address, Double salary, String gender, LocalDate dob) {
		customerService.registerNewCustomer(username, password, firstName, lastName, email, phoneNumber, nric, address,
				salary, gender, dob);
		return "redirect:/login"; // Redirects to the login page after successful registration
	}

	/**
	 * Returns the home page view. This method handles requests to display the home
	 * page after a user has logged in successfully.
	 *
	 * @return the name of the Thymeleaf template that renders the home page
	 */
	@GetMapping("/home")
	public String home() {
		return "home";
	}
	// home is the customer dashboard

	// viewing the update details page
	@GetMapping("/updateCustomerDetails")
	public String updateCustomerDetails() {
		return "updateCustomerDetails";
	}

	// actually updating their details
	@PostMapping("/updateCustomerDetails")
	public String updateCustomerDetails(String username, String password, String firstName, String lastName,
			String email, Integer phoneNumber, String nric, String address, Double salary, String gender, LocalDate dob,
			String oldUsername) {

		customerService.updateCustomerDetails(username, password, firstName, lastName, email, phoneNumber, nric,
				address, salary, gender, dob, oldUsername);
		return "redirect:/home"; // Redirects to the home page after successful update
	}

}
