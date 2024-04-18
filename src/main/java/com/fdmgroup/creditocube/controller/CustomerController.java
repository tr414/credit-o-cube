package com.fdmgroup.creditocube.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
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
	public String registerUser(HttpServletRequest request) {

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String nric = request.getParameter("nric");

		LocalDate dob = LocalDate.parse(request.getParameter("dob"));

		String confirmPassword = request.getParameter("confirm-password");
		boolean result = customerService.detailVerificationRegistration(username, password, firstName, lastName, nric,
				dob);

		ArrayList<Customer> customerWithNric = customerService.findCustomerByNric(nric);

		if (customerWithNric.size() >= 1 || !password.equals(confirmPassword) || !result
				|| customerService.findCustomerByUsername(username).isPresent()) {
			// cannot register
			return ("register");

		} else {
			customerService.registerNewCustomer(username, password, firstName, lastName, nric, dob);
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
		System.out.println(request.getParameter("dob"));
		System.out.println(request.getParameter("firstName"));
		System.out.println(request.getParameter("lastName"));
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
