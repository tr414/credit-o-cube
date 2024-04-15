package com.fdmgroup.creditocube.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.model.User;
import com.fdmgroup.creditocube.service.DebitAccountService;
import com.fdmgroup.creditocube.service.UserService;

/**
 * Web Controller for managing debit account-related requests
 *
 * @author timothy.chai
 */
@Controller
public class DebitAccountController {

	@Autowired
	private DebitAccountService debitAccountService;

	@Autowired
	private UserService userService;

	/**
	 * Creates a new debit account for the authenticated customer.
	 *
	 * @param customer The authenticated customer object.
	 * @return A redirect to the dashboard page after creating the new debit
	 *         account.
	 */
	public String createDebitAccount(@SessionAttribute Customer customer) {

		// Find the user associated with the provided customer ID.
		Optional<User> optionalCustomer = userService.findUserById(customer.getUser_id());

		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}

		// Get the authenticated customer session object.
		Customer sessionCustomer = (Customer) optionalCustomer.get();

		// Create a new debit account for the customer.
		DebitAccount newAccount = new DebitAccount(sessionCustomer);

		// Get the list of debit accounts associated with the customer.
		List<DebitAccount> accountList = sessionCustomer.getDebitAccounts();

		// Add the new debit account to the customer's list of accounts.
		accountList.add(newAccount);

		// Update the customer's list of debit accounts.
		sessionCustomer.setDebitAccounts(accountList);

		// Return a redirect to the dashboard page.
		return "redirect:/dashboard";
	}

	/**
	 * Closes a debit account for the authenticated customer.
	 *
	 * @param customer The authenticated customer object.
	 * @param account  The debit account to be closed.
	 * @return A redirect to the dashboard page after closing the debit account.
	 */
	public String closeDebitAccount(@SessionAttribute Customer customer, @SessionAttribute DebitAccount account) {

		// Find the user associated with the provided customer ID.
		Optional<User> optionalCustomer = userService.findUserById(customer.getUser_id());

		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}

		// Get the authenticated customer session object.
		Customer sessionCustomer = (Customer) optionalCustomer.get();

		// Find the debit account associated with the provided account number.
		Optional<DebitAccount> optionalDebitAccount = debitAccountService
				.findDebitAccountByAccountNumber(account.getAccountNumber());

		// If the debit account is not found, redirect to the login page.
		if (optionalDebitAccount.isEmpty()) {
			return "redirect:/login";
		}

		// Get the debit account to be closed.
		DebitAccount sessionDebitAccount = optionalDebitAccount.get();

		// Get the list of debit accounts associated with the customer.
		List<DebitAccount> accountList = sessionCustomer.getDebitAccounts();

		// Remove the debit account to be closed from the customer's list of accounts.
		accountList.remove(sessionDebitAccount);

		// Update the customer's list of debit accounts.
		sessionCustomer.setDebitAccounts(accountList);

		// Return a redirect to the dashboard page.
		return "redirect:/dashboard";
	}

}
