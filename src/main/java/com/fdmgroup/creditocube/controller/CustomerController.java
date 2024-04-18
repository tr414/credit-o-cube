package com.fdmgroup.creditocube.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.service.CustomerService;
import com.fdmgroup.creditocube.service.DebitAccountService;
import com.fdmgroup.creditocube.service.UserService;
import com.fdmgroup.creditocube.service.ValidationService;

import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;

/**
 * The CustomerController class handles web requests related to customer
 * operations. It utilizes services to interact with user, debit account, and
 * customer data.
 */
@Controller
@SessionAttributes("firstName")
public class CustomerController {

//	@Autowired
//	private HttpSession session;

	@Autowired
	UserService userService; // Service for user-related operations

	@Autowired
	DebitAccountService debitAccountService; // Service for debit account-related operations

	@Autowired
	CustomerService customerService; // Service for customer-related operations

	@Autowired
	ValidationService validationService;

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
	public String registerUser(HttpServletRequest request, Model model) {

		// Extract parameters from the request
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirm-password");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String nric = request.getParameter("nric");
		LocalDate dob = LocalDate.parse(request.getParameter("dob"));

		// Initialize a flag to check if there are any validation errors
		boolean hasErrors = false;

		// Validate first name
		if (!validationService.isValidName(firstName)) {
			model.addAttribute("firstNameError", "First name must only contain alphabets and spaces.");
			hasErrors = true;
		}

		// Validate last name
		if (!validationService.isValidName(lastName)) {
			model.addAttribute("lastNameError", "Last name must only contain alphabets and spaces.");
			hasErrors = true;
		}

		// Validate username
		if (!validationService.isValidUsername(username)) {
			model.addAttribute("usernameError",
					"Username must be at least 6 characters long and can only contain alphanumeric characters, underscores, or dashes.");
			hasErrors = true;
		}

		// Validate NRIC
		if (!validationService.isValidNRIC(nric)) {
			model.addAttribute("nricError", "Enter a valid NRIC.");
			hasErrors = true;
		}

		// Validate Date of Birth
		if (!validationService.isValidDOB(dob)) {
			model.addAttribute("dobError", "You must be at least 18 years old to register.");
			hasErrors = true;
		}

		// Validate password complexity
		if (!validationService.isPasswordComplex(password)) {
			model.addAttribute("passwordError",
					"Password must be at least 8 characters long with at least 1 uppercase, 1 lowercase, 1 digit, and 1 special character.");
			hasErrors = true;
		}

		// Validate password match
		if (!password.equals(confirmPassword)) {
			model.addAttribute("confirmPasswordError", "Passwords do not match.");
			hasErrors = true;
		}

		// Check for any validation errors
		if (hasErrors) {
			return "register";
		}

		// If validation passes, proceed to register the new customer
		customerService.registerNewCustomer(username, password, firstName, lastName, nric, dob);
		System.out.println("New customer registered successfully");
		return "redirect:/login"; // Redirects to the login page after successful registration
	}

	/**
	 * Returns the home page view. This method handles requests to display the home
	 * page after a user has logged in successfully.
	 *
	 * @return the name of the Thymeleaf template that renders the home page
	 */
	@GetMapping("/customer-dashboard")
	public String home(Model model, Principal principal, SessionStatus status) {
		Customer customer = customerService.findCustomerByUsername(principal.getName()).get();
		model.addAttribute("firstName", customer.getFirstName()); // Add first name to the model
		List<DebitAccount> customerAccounts = debitAccountService.findAllDebitAccountsForCustomer(customer);
		model.addAttribute("accounts", customerAccounts);
		return "customer-dashboard";
	}
	// home is the customer dashboard

	// viewing the update details page
	@GetMapping("/customer-details")
	public String updateCustomerDetails(Principal principal, Model model) {
		Customer customer = customerService.findCustomerByUsername(principal.getName()).get();
		model.addAttribute("username", customer.getUsername());
		model.addAttribute("firstName", customer.getFirstName());
		model.addAttribute("lastName", customer.getLastName());
		model.addAttribute("email", customer.getEmail());
		model.addAttribute("phoneNumber", customer.getPhoneNumber());
		model.addAttribute("nric", customer.getNric());
		model.addAttribute("address", customer.getAddress());
		model.addAttribute("salary", customer.getSalary());
		model.addAttribute("gender", customer.getGender());
		model.addAttribute("dob", customer.getDob());
		model.addAttribute("customer", customer);

		return "customer-details";
	}

	// actually updating their details
	@PostMapping("/customer-details")
	public String updateCustomerDetails(Principal principal, HttpServletRequest request) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String phoneNumber = request.getParameter("phoneNumber");

		String nric = request.getParameter("nric");
		String address = request.getParameter("address");
		Double salary = Double.parseDouble(request.getParameter("salary"));
		String gender = request.getParameter("gender");
		LocalDate dob = LocalDate.parse(request.getParameter("dob"));

		String oldUsername = principal.getName();

		customerService.updateCustomerDetails(username, password, firstName, lastName, email, phoneNumber, nric,
				address, salary, gender, dob, oldUsername);
		return "redirect:/login"; // Redirects to the login page after successful update
	}

	// Delete customer account
	@GetMapping("/deleteCustomerAccount")
	public String deleteCustomerAccount(Principal principal) {
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());

		if (optionalCustomer.isEmpty()) {
			System.out.println("Customer not found in database");
			return "redirect:/login";
		}

		Customer customer = optionalCustomer.get();

		if (customer.getDebitAccounts().size() > 0) {
			System.out.println("Customer has debit accounts");
			return "redirect:/home";
		}

		customerService.deleteCustomer(customer);

		System.out.println("Deleted customer account");
		return "redirect:/login";
	}

}
