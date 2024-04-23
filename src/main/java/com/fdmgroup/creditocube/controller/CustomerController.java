package com.fdmgroup.creditocube.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.CreditCardTransaction;
import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.model.DebitAccountTransaction;
import com.fdmgroup.creditocube.model.User;
import com.fdmgroup.creditocube.service.CreditCardTransactionService;
import com.fdmgroup.creditocube.service.CustomerService;
import com.fdmgroup.creditocube.service.DebitAccountService;
import com.fdmgroup.creditocube.service.DebitAccountTransactionService;
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
	DebitAccountTransactionService debitAccountTransactionService; // Service for debit account transaction operations

	@Autowired
	CustomerService customerService; // Service for customer-related operations

	@Autowired
	ValidationService validationService;

	@Autowired
	CreditCardTransactionService creditCardTransactionService; // Service for credit card transaction operations

	private static Logger logger = LogManager.getLogger(CustomerController.class);

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

		logger.debug("registration request parameters received");

		boolean hasErrors = false;

		// Validate first name and handle retention of input
		Optional<String> firstNameError = validationService.isValidName(firstName);
		if (firstNameError.isPresent()) {
			model.addAttribute("firstNameError", firstNameError.get());
			hasErrors = true;
			logger.info("first name is not valid: " + firstName);
		} else {
			model.addAttribute("firstName", firstName);
		}

		// Validate last name and handle retention of input
		Optional<String> lastNameError = validationService.isValidName(lastName);
		if (lastNameError.isPresent()) {
			model.addAttribute("lastNameError", lastNameError.get());
			hasErrors = true;
			logger.info("last name is not valid: " + lastName);
		} else {
			model.addAttribute("lastName", lastName);
		}

		// Validate username and handle retention of input
		Optional<String> usernameError = validationService.isValidUsername(username);
		if (usernameError.isPresent()) {
			model.addAttribute("usernameError", usernameError.get());
			hasErrors = true;
			logger.info("username is not valid: " + username);
		} else {
			model.addAttribute("username", username);
		}

		// Validate NRIC and handle retention of input
		Optional<String> nricError = validationService.isValidNRIC(nric);
		if (nricError.isPresent()) {
			model.addAttribute("nricError", nricError.get());
			hasErrors = true;
			logger.info("NRIC is not valid: " + nric);
		} else {
			model.addAttribute("nric", nric);
		}

		// Validate Date of Birth and handle retention of input
		Optional<String> dobError = validationService.isValidDOB(dob);
		if (dobError.isPresent()) {
			model.addAttribute("dobError", dobError.get());
			hasErrors = true;
			logger.info("date-of-birth is not valid: " + dob);
		} else {
			model.addAttribute("dob", dob.toString());
		}

		// Validate password complexity and handle retention of input
		Optional<String> passwordError = validationService.isPasswordComplex(password);
		if (passwordError.isPresent()) {
			model.addAttribute("passwordError", passwordError.get());
			hasErrors = true;
			logger.info("password is not valid: ");
		}

		// Validate password match and handle retention of input
		Optional<String> confirmPasswordError = validationService.isSamePassword(password, confirmPassword);
		if (confirmPasswordError.isPresent()) {
			model.addAttribute("confirmPasswordError", confirmPasswordError.get());
			hasErrors = true;
			logger.info("passwords do not match: ");
		}

		if (hasErrors) {
			// Return to registration page if there are any errors
			logger.info("registration request has errors, returning to registration page: ");
			return "register";
		} else {
			// Proceed to register the new customer if all validations pass
			customerService.registerNewCustomer(username, password, firstName, lastName, nric, dob);
			logger.debug("New customer registered successfully");
			return "redirect:/login"; // Redirects to the login page after successful registration
		}
	}

	/**
	 * Returns the home page view. This method handles requests to display the home
	 * page after a user has logged in successfully.
	 *
	 * @return the name of the Thymeleaf template that renders the home page
	 */
	@GetMapping("/customer-dashboard")
	public String home(Model model, Principal principal, SessionStatus status) {
		User user = userService.findUserByUsername(principal.getName()).get();
		logger.debug(user.getUsername() + " logged in successfully");
		if (user.getUserType().equalsIgnoreCase("admin")) {
			return "landing";
		}

		Customer customer = customerService.findCustomerByUsername(principal.getName()).get();
		model.addAttribute("firstName", customer.getFirstName()); // Add first name to the model
		List<DebitAccount> customerAccounts = debitAccountService.findAllDebitAccountsForCustomer(customer);
		model.addAttribute("accounts", customerAccounts);
		List<DebitAccountTransaction> recentTransactions = debitAccountTransactionService
				.findRecentTransactionsOfCustomer(customer);
		model.addAttribute("recentTransactions", recentTransactions);
		List<CreditCard> cards = customer.getCreditCards();
		model.addAttribute("cards", cards);
		List<CreditCardTransaction> creditTransactions = new ArrayList<>();
		for (CreditCard card : customer.getCreditCards()) {
			creditTransactions.addAll(creditCardTransactionService.findAllCreditCardTransactions(card));
		}
		model.addAttribute("creditTransactions", creditTransactions);
		return "customer-dashboard";
	}

	@GetMapping("/update-password")
	public String goToUpdatePasswordPage(Principal principal, Model model) {
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());
		if (optionalCustomer.isEmpty()) {
			logger.debug("Customer is not logged in");
			return "redirect:/login";
		}
		Customer customer = optionalCustomer.get();
		model.addAttribute("customer", customer);
		return "update-password";
	}

	@PostMapping("/update-password")
	public String updatePassword(HttpServletRequest request, Principal principal, Model model) {
		// Check that customer is logged in
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());
		if (optionalCustomer.isEmpty()) {
			logger.debug("Customer is not logged in");
			return "redirect:/login";
		}
		Customer customer = optionalCustomer.get();

		// Get request parameters
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirm-password");

		// if password fields are null, use database value
		if (password == null || confirmPassword == null) {
			password = customer.getPassword();
			confirmPassword = customer.getPassword();
		}

		boolean hasErrors = false;

		// Validate password complexity and handle retention of input
		Optional<String> passwordError = validationService.isPasswordComplex(password);
		if (passwordError.isPresent()) {
			model.addAttribute("passwordError", passwordError.get());
			hasErrors = true;
		}

		// Validate password match and handle retention of input
		Optional<String> confirmPasswordError = validationService.isSamePassword(password, confirmPassword);
		if (confirmPasswordError.isPresent()) {
			model.addAttribute("confirmPasswordError", confirmPasswordError.get());
			hasErrors = true;
		}

		if (hasErrors) {
			// Return to registration page if there are any errors
			return "customer-details";
		} else {
//			customerService.updateCustomerDetails(username, firstName, lastName, email, phoneNumber, nric, address,
//					salary, gender, dob);
			return "redirect:/login";
		}

	}

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

	@PostMapping("/customer-details")
	public String updateCustomerDetails(HttpServletRequest request, Principal principal, Model model) {

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

		// Extract parameters from the request
		String username = request.getParameter("username");

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String nric = request.getParameter("nric");
		LocalDate dob = LocalDate.parse(request.getParameter("dob"));

		String stringSalary = request.getParameter("salary");
		String address = request.getParameter("address");
		String gender = request.getParameter("gender");
		String phoneNumber = request.getParameter("phoneNumber");
		String email = request.getParameter("email");

		boolean hasErrors = false;

		// if customer input is null but there is a registered phone number in database,
		// use database value
		if (phoneNumber.isEmpty() && customer.getPhoneNumber() != null) {
			phoneNumber = customer.getPhoneNumber();
		}
		// if customer input is not null, use customer input
		else if (!phoneNumber.isEmpty()) {
			Optional<String> phoneNumberError = validationService.isMobileNumber(phoneNumber);
			if (phoneNumberError.isPresent()) {
				model.addAttribute("phoneNumberError", phoneNumberError.get());
				hasErrors = true;
			} else {
				model.addAttribute("phoneNumber", phoneNumber);
			}
		}
		// otherwise, if phone number is null and registered phone number is null, then
		// ignore field

		// if customer input is null but there is a registered salary in database,
		// use database value
		double salary = 0;
		if (stringSalary.isEmpty() && customer.getSalary() != null) {
			salary = customer.getSalary();
		}
		// if customer input is not null, use customer input
		else if (!stringSalary.isEmpty()) {
			salary = Double.parseDouble(stringSalary);
			Optional<String> salaryError = validationService.isValidSalary(salary);
			if (salaryError.isPresent()) {
				model.addAttribute("salaryError", salaryError.get());
				hasErrors = true;
			} else {
				model.addAttribute("salary", salary);
			}
		}
		// otherwise, if salary is null and registered salary is null, then ignore field

		if (address.isEmpty() && customer.getAddress() != null) {
			address = customer.getAddress();
		}

		if (hasErrors) {
			// Return to registration page if there are any errors
			return "customer-details";
		} else {
			customerService.updateCustomerDetails(username, firstName, lastName, email, phoneNumber, nric, address,
					salary, gender, dob);
			return "redirect:/login";
		}
	}

	// Delete customer account
	@GetMapping("/deleteCustomerAccount")
	public String deleteCustomerAccount(Principal principal) {
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());

		if (optionalCustomer.isEmpty()) {
			logger.info("Customer not found in database, abort account deletion");
			return "redirect:/login";
		}

		Customer customer = optionalCustomer.get();

		if (customer.getDebitAccounts().size() > 0) {
			logger.debug("Customer has existing debit accounts, abort account deletion");
			return "redirect:/home";
		}

		customerService.deleteCustomer(customer);

		logger.debug("Deleted customer account successfully");
		return "redirect:/login";
	}

}
