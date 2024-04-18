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

		// set customer and their accounts as session attributes to retrieve in view
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

	/**
	 * Redirects to the deposit-withdraw page for a specific debit account.
	 *
	 * @param accountId The unique identifier of the debit account to be accessed.
	 * @return A redirect to the deposit-withdraw page for the specified debit
	 *         account.
	 */
	@GetMapping("/deposit-withdraw")
	public String goToDepositWithdrawPage() {

		return "deposit-withdraw";
	}

	@PostMapping("/deposit-withdraw")
	public String reloadDepositWithdrawPageWithInfo(@RequestParam long selectedAccountId) {

		// Find the debit account associated with the provided account number.
		Optional<DebitAccount> optionalDebitAccount = debitAccountService
				.findDebitAccountByAccountId(selectedAccountId);

		// If the debit account is not found, redirect to the login page.
		if (optionalDebitAccount.isEmpty()) {
			return "account-dashboard";
		}

		DebitAccount targetDebitAccount = optionalDebitAccount.get();

		session.setAttribute("selectedAccount", targetDebitAccount);

		return "deposit-withdraw";
	}

	/**
	 * Updates the balance of a debit account.
	 *
	 * @param customer        The authenticated customer object.
	 * @param accountId       The unique identifier of the debit account to be
	 *                        updated.
	 * @param amount          The amount to be deposited or withdrawn.
	 * @param transactionType The type of transaction, either "deposit" or
	 *                        "withdraw".
	 *
	 * @return A redirect to the dashboard page after updating the debit account
	 *         balance.
	 */
	@PostMapping("/update-account-balance")
	public String updateAccountBalance(@SessionAttribute Customer customer, @RequestParam long accountId,
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

		// Call debitAccountService to deposit into / withdraw from debit account
		// balance.
		debitAccountService.changeAccountBalance(targetDebitAccount, amount, isDeposit);

		// Create a new DebitAccountTransaction for persistence.
		DebitAccountTransaction newTransaction = new DebitAccountTransaction();
		newTransaction.setDebitAccountTransactionAmount(amount);

		// if transaction is a deposit, set debitAccount as toAccount.
		if (isDeposit) {
			newTransaction.setDebitAccountTransactionType("deposit");
			newTransaction.setToAccount(targetDebitAccount);
		} else {
			newTransaction.setDebitAccountTransactionType("withdraw");
			newTransaction.setFromAccount(targetDebitAccount);
		}

		// persist onto database.
		debitAccountTransactionService.createDebitAccountTransaction(newTransaction);

		// save.
		debitAccountService.updateAccount(targetDebitAccount);

		// Return a redirect to the dashboard page.
		return "redirect:/account-dashboard";
	}

	@GetMapping("/view-transaction-history")
	public String goToViewTransactionHistoryPage() {

		return "view-transaction-history";
	}

	/**
	 * Redirects to the view-transaction-history page for a specific debit account.
	 *
	 * @param accountId The unique identifier of the debit account to be accessed.
	 * @return A redirect to the view-transaction-history page for the specified
	 *         debit account.
	 */
	@PostMapping("/view-transaction-history")
	public String reloadviewTransactionHistoryPage(@RequestParam long selectedAccountId) {
		Optional<DebitAccount> optionalAccount = debitAccountService.findDebitAccountByAccountId(selectedAccountId);

		if (optionalAccount.isEmpty()) {
			System.out.println("Account not found: " + selectedAccountId);
			return "redirect:/account-dashboard";
		}

		DebitAccount sessionAccount = optionalAccount.get();

		List<DebitAccountTransaction> accountTransactions = debitAccountTransactionService
				.findTransactionsOfAccount(sessionAccount);
		session.setAttribute("selectedAccount", sessionAccount);
		session.setAttribute("accountTransactions", accountTransactions);

		return "view-transaction-history";
	}

}
