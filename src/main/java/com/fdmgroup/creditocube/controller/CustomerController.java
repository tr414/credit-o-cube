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

		boolean hasErrors = false;

		// Validate first name and handle retention of input
		Optional<String> firstNameError = validationService.isValidName(firstName);
		if (firstNameError.isPresent()) {
			model.addAttribute("firstNameError", firstNameError.get());
			hasErrors = true;
		} else {
			model.addAttribute("firstName", firstName);
		}

		// Validate last name and handle retention of input
		Optional<String> lastNameError = validationService.isValidName(lastName);
		if (lastNameError.isPresent()) {
			model.addAttribute("lastNameError", lastNameError.get());
			hasErrors = true;
		} else {
			model.addAttribute("lastName", lastName);
		}

		// Validate username and handle retention of input
		Optional<String> usernameError = validationService.isValidUsername(username);
		if (usernameError.isPresent()) {
			model.addAttribute("usernameError", usernameError.get());
			hasErrors = true;
		} else {
			model.addAttribute("username", username);
		}

		// Validate NRIC and handle retention of input
		Optional<String> nricError = validationService.isValidNRIC(nric);
		if (nricError.isPresent()) {
			model.addAttribute("nricError", nricError.get());
			hasErrors = true;
		} else {
			model.addAttribute("nric", nric);
		}

		// Validate Date of Birth and handle retention of input
		Optional<String> dobError = validationService.isValidDOB(dob);
		if (dobError.isPresent()) {
			model.addAttribute("dobError", dobError.get());
			hasErrors = true;
		} else {
			model.addAttribute("dob", dob.toString());
		}

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
			return "register";
		} else {
			// Proceed to register the new customer if all validations pass
			customerService.registerNewCustomer(username, password, firstName, lastName, nric, dob);
			System.out.println("New customer registered successfully");
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
