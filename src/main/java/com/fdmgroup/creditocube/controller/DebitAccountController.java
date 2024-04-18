package com.fdmgroup.creditocube.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.model.DebitAccountTransaction;
import com.fdmgroup.creditocube.service.CustomerService;
import com.fdmgroup.creditocube.service.DebitAccountService;
import com.fdmgroup.creditocube.service.DebitAccountTransactionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
	private DebitAccountTransactionService debitAccountTransactionService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private HttpSession session;

	@GetMapping("/account-dashboard")
	public String goToAccountDashboard(Principal principal) {
		// Find the user associated with the provided customer ID.
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());

		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}
		Customer sessionCustomer = optionalCustomer.get();
		session.setAttribute("customer", sessionCustomer);

		List<DebitAccount> accounts = debitAccountService.findAllDebitAccountsForCustomer(sessionCustomer);

		session.setAttribute("accounts", accounts);

		return ("account-dashboard");
	}

	@GetMapping("/go-to-create-debit-account")
	public String goToCreateDebitAccountPage() {
		return ("create-debit-account");
	}

	/**
	 * Creates a new debit account for the authenticated customer.
	 *
	 * @param customer The authenticated customer object.
	 * @return A redirect to the dashboard page after creating the new debit
	 *         account.
	 */
	@PostMapping("/create-debit-account")
	public String createDebitAccount(Principal principal, HttpServletRequest request) {

		// Get request parameters
		String accountName = request.getParameter("account-name");
		String stringBalance = request.getParameter("account-balance");
		double balance = Double.parseDouble(stringBalance);

		// Find the user associated with the provided customer ID.
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());

		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}

		// Get the authenticated customer session object.
		Customer sessionCustomer = optionalCustomer.get();

		// Create a new debit account for the customer.
		DebitAccount newAccount = new DebitAccount(sessionCustomer);
		newAccount.setAccountName(accountName);
		newAccount.setAccountBalance(balance);
		newAccount.setAccountNumber(debitAccountService.generateUniqueDebitAccountNumber());

		debitAccountService.createAccount(newAccount);

		// Return a redirect to the dashboard page.
		return "redirect:/account-dashboard";
	}

	/**
	 * Closes a debit account for the authenticated customer.
	 *
	 * @param customer The authenticated customer object.
	 * @param account  The debit account to be closed.
	 * @return A redirect to the dashboard page after closing the debit account.
	 */
	@PostMapping("/deleteDebitAccount")
	public String closeDebitAccount(@SessionAttribute Customer customer, @RequestParam long accountId) {

		// Find the user associated with the provided customer ID.
		Optional<Customer> optionalCustomer = customerService.findCustomerById(customer.getUser_id());

		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}

		// Find the debit account associated with the provided account number.
		Optional<DebitAccount> optionalDebitAccount = debitAccountService.findDebitAccountByAccountId(accountId);

		// If the debit account is not found, redirect to the login page.
		if (optionalDebitAccount.isEmpty()) {
			return "redirect:/login";
		}

		DebitAccount targetDebitAccount = optionalDebitAccount.get();

		debitAccountService.closeDebitAccount(targetDebitAccount);

		// Return a redirect to the dashboard page.
		return "redirect:/account-dashboard";
	}

	@PostMapping("/deposit-withdraw")
	public String goToDepositWithdrawPage(@RequestParam int accountId) {
		Optional<DebitAccount> optionalAccount = debitAccountService.findDebitAccountByAccountId(accountId);

		if (optionalAccount.isEmpty()) {
			System.out.println("Account not found: " + accountId);
			return "redirect:/account-dashboard";
		}

		DebitAccount sessionAccount = optionalAccount.get();
		session.setAttribute("account", sessionAccount);

		return ("deposit-withdraw");
	}

	@PostMapping("/update-account-balance")
	public String depositIntoAccount(@SessionAttribute Customer customer, @RequestParam long accountId,
			@RequestParam double amount, @RequestParam(value = "transaction") String transactionType) {

		boolean isDeposit = transactionType.equals("deposit");

		// Find the user associated with the provided customer ID.
		Optional<Customer> optionalCustomer = customerService.findCustomerById(customer.getUser_id());

		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}

		// Find the debit account associated with the provided account number.
		Optional<DebitAccount> optionalDebitAccount = debitAccountService.findDebitAccountByAccountId(accountId);

		// If the debit account is not found, redirect to the login page.
		if (optionalDebitAccount.isEmpty()) {
			return "redirect:/login";
		}

		DebitAccount targetDebitAccount = optionalDebitAccount.get();

		debitAccountService.changeAccountBalance(targetDebitAccount, amount, isDeposit);

		DebitAccountTransaction newTransaction = new DebitAccountTransaction();
		newTransaction.setDebitAccountTransactionAmount(amount);

		if (isDeposit) {
			newTransaction.setDebitAccountTransactionType("deposit");
			newTransaction.setToAccount(targetDebitAccount);
		} else {
			newTransaction.setDebitAccountTransactionType("withdraw");
			newTransaction.setFromAccount(targetDebitAccount);
		}

		debitAccountTransactionService.createDebitAccountTransaction(newTransaction);

		debitAccountService.updateAccount(targetDebitAccount);

		// Return a redirect to the dashboard page.
		return "redirect:/account-dashboard";
	}

	@PostMapping("/view-transaction-history")
	public String goToViewTransactionHistoryPage(@RequestParam int accountId) {
		Optional<DebitAccount> optionalAccount = debitAccountService.findDebitAccountByAccountId(accountId);

		if (optionalAccount.isEmpty()) {
			System.out.println("Account not found: " + accountId);
			return "redirect:/account-dashboard";
		}

		DebitAccount sessionAccount = optionalAccount.get();
		session.setAttribute("account", sessionAccount);
		List<DebitAccountTransaction> accountTransactions = debitAccountTransactionService
				.findTransactionsOfAccount(sessionAccount);
		session.setAttribute("accountTransactions", accountTransactions);

		return ("view-transaction-history");
	}

}
