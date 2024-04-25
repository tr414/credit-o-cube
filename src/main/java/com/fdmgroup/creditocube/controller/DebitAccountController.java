package com.fdmgroup.creditocube.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	private static Logger logger = LogManager.getLogger(DebitAccountController.class);

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
		logger.debug("Customer and accounts are added to session");
		logger.debug("Directing to account-dashboard.html");
		return ("account-dashboard");
	}

	@GetMapping("/go-to-create-debit-account")
	public String goToCreateDebitAccountPage() {
		logger.debug("Directing to create-debit-account.html");
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
	public String createDebitAccount(Principal principal, HttpServletRequest request,
			RedirectAttributes redirectAttrs) {

		// Get request parameters
		String accountName = request.getParameter("account-name");
		String stringBalance = request.getParameter("account-balance");
		double balance = Double.parseDouble(stringBalance);
		logger.debug("Request parameters obtained - accountName: " + accountName + ", balance: " + balance);

		// Find the user associated with the provided customer ID.
		Optional<Customer> optionalCustomer = customerService.findCustomerByUsername(principal.getName());

		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			logger.info("Customer not found in database, redirect to login page");
			return "redirect:/login";
		}

		// Get the authenticated customer session object.
		Customer sessionCustomer = optionalCustomer.get();
		List<DebitAccount> accountList = debitAccountService.findAllDebitAccountsForCustomer(sessionCustomer);

		// Validation checks and spit out error messages
		if (accountList.size() >= 5) {
			logger.info("Customer has 5 or more debit accounts, abort creating debit account");
			redirectAttrs.addFlashAttribute("moreThanFiveAccounts",
					"You already have 5 or more debit accounts, please close an existing account before creating a new one");
			return "redirect:/account-dashboard";
		}

		// Create a new debit account for the customer.
		DebitAccount newAccount = new DebitAccount(sessionCustomer);
		newAccount.setAccountName(accountName);
		newAccount.setAccountBalance(balance);
		newAccount.setActive(true);
		newAccount.setAccountNumber(debitAccountService.generateUniqueDebitAccountNumber());

		// check account balance is positive
		if (newAccount.getAccountBalance() <= 0) {
			logger.info(
					"Customer tried to create debit account with non-positive balance, abort creating debit account");
			redirectAttrs.addFlashAttribute("newAccountNegativeBalance",
					"Please deposit a positive amount into the account");
			return "redirect:/account-dashboard";
		}

		debitAccountService.createAccount(newAccount);
		logger.debug("New Debit Account created: " + newAccount.getAccountName());

		// Add initial deposit as a first transaction
		DebitAccountTransaction firstTransaction = new DebitAccountTransaction();
		firstTransaction.setDebitAccountTransactionAmount(balance);
		firstTransaction.setDebitAccountTransactionType("deposit");
		firstTransaction.setToAccountNumber(newAccount.getAccountNumber());
		debitAccountTransactionService.createDebitAccountTransaction(firstTransaction);
		logger.debug("Add first deposit transaction: deposit $" + balance);

		// Return a redirect to the dashboard page.
		logger.debug("Redirecting to account-dashboard.html");
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
	public String closeDebitAccount(@SessionAttribute Customer customer, @RequestParam long accountId,
			RedirectAttributes redirectAttrs) {

		// Find the user associated with the provided customer ID.
		Optional<Customer> optionalCustomer = customerService.findCustomerById(customer.getUser_id());

		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			logger.info("Customer not found in database, redirect to login page");
			return "redirect:/login";
		}

		logger.debug("Customer exists, Customer details retrieved from database");

		// Find the debit account associated with the provided account number.
		Optional<DebitAccount> optionalDebitAccount = debitAccountService.findDebitAccountByAccountId(accountId);

		// If the debit account is not found, redirect to the login page.
		if (optionalDebitAccount.isEmpty()) {
			logger.info("Debit Account not found in database, redirect to account dashboard");
			return "redirect:/account-dashboard";
		}

		DebitAccount targetDebitAccount = optionalDebitAccount.get();
		logger.debug("Debit Account exists, details retrieved from database");

		if (targetDebitAccount.getAccountBalance() != 0) {
			redirectAttrs.addFlashAttribute("accountStillHasBalance",
					"The account you are trying to deactivate still has a balance, please withdraw remaining balance first");
		}

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
		logger.debug("Directing to deposit-withdraw.html");
		return "deposit-withdraw";
	}

	@PostMapping("/deposit-withdraw")
	public String reloadDepositWithdrawPageWithInfo(@RequestParam long selectedAccountId) {

		// Find the debit account associated with the provided account number.
		Optional<DebitAccount> optionalDebitAccount = debitAccountService
				.findDebitAccountByAccountId(selectedAccountId);

		// If the debit account is not found, redirect to the login page.
		if (optionalDebitAccount.isEmpty()) {
			logger.info("Debit account not found in database, redirecting to account-dashboard");
			return "redirect:/account-dashboard";
		}

		DebitAccount targetDebitAccount = optionalDebitAccount.get();

		session.setAttribute("selectedAccount", targetDebitAccount);
		logger.debug("Debit account found, setting as session attribute and refreshing page");

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
			@RequestParam double amount, @RequestParam(value = "transaction") String transactionType,
			RedirectAttributes redirectAttrs) {

		boolean isDeposit = transactionType.equals("deposit");

		// Find the user associated with the provided customer ID.
		Optional<Customer> optionalCustomer = customerService.findCustomerById(customer.getUser_id());

		// If the user is not found, redirect to the login page.
		if (optionalCustomer.isEmpty()) {
			logger.info("Customer not found in database, redirect to login page");
			return "redirect:/login";
		}

		logger.debug("Customer exists, Customer details retrieved from database");

		// Find the debit account associated with the provided account number.
		Optional<DebitAccount> optionalDebitAccount = debitAccountService.findDebitAccountByAccountId(accountId);

		// If the debit account is not found, redirect to the login page.
		if (optionalDebitAccount.isEmpty()) {
			logger.info("Debit account not found in database, redirecting to account-dashboard");
			return "redirect:/account-dashboard";
		}

		DebitAccount targetDebitAccount = optionalDebitAccount.get();
		logger.debug("Debit Account exists, details retrieved from database");

		if (amount <= 0) {
			redirectAttrs.addFlashAttribute("invalidAmount", "Please enter a valid amount");
			return "redirect:/deposit-withdraw";
		}

		// Call debitAccountService to deposit into / withdraw from debit account
		// balance.
		debitAccountService.changeAccountBalance(targetDebitAccount, amount, isDeposit);

		// Create a new DebitAccountTransaction for persistence.
		DebitAccountTransaction newTransaction = new DebitAccountTransaction();
		newTransaction.setDebitAccountTransactionAmount(amount);
		logger.debug("New DebitAccountTransaction created");

		// if transaction is a deposit, set debitAccount as toAccount.
		if (isDeposit) {
			newTransaction.setDebitAccountTransactionType("deposit");
			newTransaction.setToAccountNumber(targetDebitAccount.getAccountNumber());
			logger.debug("deposit transaction created");
		} else {
			newTransaction.setDebitAccountTransactionType("withdraw");
			newTransaction.setFromAccount(targetDebitAccount);
			logger.debug("withdraw transaction created");
		}

		// persist onto database.
		debitAccountTransactionService.createDebitAccountTransaction(newTransaction);
		logger.debug("DebitAccountTransaction persisted to database");

		// save.
		debitAccountService.updateAccount(targetDebitAccount);
		logger.debug("DebitAccount updated in database");

		// Return a redirect to the dashboard page.
		logger.debug("Redirecting to account dashboard");
		return "redirect:/account-dashboard";
	}

	@GetMapping("/view-transaction-history")
	public String goToViewTransactionHistoryPage(Model model) {
		logger.debug("Directing to view-transaction-history.html");
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
	public String reloadviewTransactionHistoryPage(@RequestParam long selectedAccountId, Model model) {
		Optional<DebitAccount> optionalAccount = debitAccountService.findDebitAccountByAccountId(selectedAccountId);
		if (optionalAccount.isEmpty()) {
			logger.info("Debit account not found in database, redirecting to account-dashboard");
			return "redirect:/account-dashboard";
		}

		DebitAccount sessionAccount = optionalAccount.get();
		logger.debug("Debit Account exists, details retrieved from database");

		List<DebitAccountTransaction> accountTransactions = debitAccountTransactionService
				.findTransactionsOfAccount(sessionAccount);
		session.setAttribute("account", sessionAccount);
		session.setAttribute("accountTransactions", accountTransactions);
//		System.out.println(sessionAccount.getAccountName());
		model.addAttribute("account", sessionAccount);
		logger.debug("Account transactions are set as session attribute");

		logger.debug("Directing to view-transaction-history.html");
		return "view-transaction-history";
	}

	/**
	 * Redirects to the transfer-to-account-number page for a specific debit
	 * account.
	 *
	 * @return A redirect to the transfer-to-account-number page for the specified
	 *         debit account.
	 */
	@GetMapping("/transfer-to-account-number")
	public String goToTransferToAccountNumberPage() {
		logger.debug("Directing to transfer-to-account-number.html");
		return "transfer-to-account-number";
	}

	/**
	 * Transfers a specified amount from one debit account to another debit account.
	 *
	 * @param selectedAccountId The unique identifier of the debit account to
	 *                          transfer funds from.
	 * @param toAccountNumber   The account number of the debit account to transfer
	 *                          funds to.
	 * @param amount            The amount to be transferred.
	 *
	 * @return A redirect to the account dashboard after the transfer operation.
	 */
	@PostMapping("/transfer-to-account-number")
	public String transferToAccountNumber(@RequestParam long selectedAccountId, @RequestParam String toAccountNumber,
			@RequestParam String toOwnAccountNumber, @RequestParam double amount) {

		if (toAccountNumber == null || toAccountNumber.isEmpty() || toAccountNumber.isBlank()) {
			toAccountNumber = toOwnAccountNumber;
		}

		// Find from-account in database
		Optional<DebitAccount> optionalAccount = debitAccountService.findDebitAccountByAccountId(selectedAccountId);

		if (optionalAccount.isEmpty()) {
			logger.info("Debit account not found in database, redirecting to account-dashboard");
			return "redirect:/account-dashboard";
		}

		DebitAccount fromAccount = optionalAccount.get();

		// Check if to-account is in database
		boolean targetIsInThisBank = true;
		Optional<DebitAccount> optionalToAccount = debitAccountService.findDebitAccountByAccountNumber(toAccountNumber);

		if (optionalToAccount.isEmpty()) {
			logger.info("toAccount not found in database, assume account is in another bank");
			targetIsInThisBank = false;
		}

		// Perform transfer for from-account
		debitAccountService.transferToAccountNumber(fromAccount, toAccountNumber, amount);
		debitAccountService.updateAccount(fromAccount);

		// Create transaction for transfer
		DebitAccountTransaction newTransaction = new DebitAccountTransaction();
		newTransaction.setDebitAccountTransactionAmount(amount);
		newTransaction.setDebitAccountTransactionType("transfer");
		newTransaction.setFromAccount(fromAccount);

		if (targetIsInThisBank) {
			// Perform transfer for to-account
			DebitAccount toAccount = optionalToAccount.get();
			debitAccountService.updateAccount(toAccount);
		}

		// Update and save transaction
		newTransaction.setToAccountNumber(toAccountNumber);
		debitAccountTransactionService.createDebitAccountTransaction(newTransaction);

		return "redirect:/account-dashboard";
	}

	@PostMapping("/find-by-date-debit")
	public String findByDate(@RequestParam(name = "dateFrom", required = false) LocalDate dateFromString,
			@RequestParam(name = "dateTo", required = false) LocalDate dateToString,
			@RequestParam("selectedAccountId") String selectedAccountIdString, Model model,
			HttpServletRequest request) {
		long selectedAccountId = new BigDecimal(selectedAccountIdString).longValue();
		Optional<DebitAccount> optionalAccount = debitAccountService.findDebitAccountByAccountId(selectedAccountId);

		// Validation to do for the input dates
		// if dateFrom is null, set startDateTime to 1 month ago
		if (dateFromString == null) {
			dateFromString = LocalDate.now().minusMonths(1);
		}

		// if dateTo is null or is set to a date in the future, set dateTo to today
		if (dateToString == null || dateToString.isAfter(LocalDate.now())) {
			dateToString = LocalDate.now();
		}

		// if user selects dateFrom to be after dateTo, set dateFrom = dateTo
		if (dateFromString.isAfter(dateToString)) {
			dateFromString = dateToString;
		}

		if (optionalAccount.isEmpty()) {
			logger.info("Debit account not found in database, redirecting to account-dashboard");
			return "redirect:/view-transaction-history";
		}

		DebitAccount sessionAccount = optionalAccount.get();
		logger.debug("Debit Account exists, details retrieved from database");
		// default time zone
		ZoneId defaultZoneId = ZoneId.systemDefault();
		Date dateFrom = Date.from(dateFromString.atStartOfDay(defaultZoneId).toInstant());

		Instant endInstant = dateToString.atStartOfDay(defaultZoneId).plusDays(1).minus(Duration.ofSeconds(1))
				.toInstant();
		Date dateTo = Date.from(endInstant);

		List<DebitAccountTransaction> accountTransactions = debitAccountTransactionService
				.findByTransactionDate(dateFrom, dateTo, sessionAccount, sessionAccount.getAccountNumber());
		model.addAttribute("accountTransactions", accountTransactions);
		session.setAttribute("selectedAccount", sessionAccount);
		session.setAttribute("accountTransactions", accountTransactions);
		logger.debug("Account transactions are set as session attribute");
		model.addAttribute("account", sessionAccount);
		return "view-transaction-history";
	}

	@PostMapping("/find-by-transaction-type")
	public String findByTransactionType(@RequestParam("selectedAccountId") String selectedAccountIdString, Model model,
			HttpServletRequest request, @RequestParam("transactionType") String transactionType) {
		long selectedAccountId = new BigDecimal(selectedAccountIdString).longValue();
		Optional<DebitAccount> optionalAccount = debitAccountService.findDebitAccountByAccountId(selectedAccountId);

		if (optionalAccount.isEmpty()) {
			logger.info("Debit account not found in database, redirecting to account-dashboard");
			return "redirect:/view-transaction-history";
		}

		DebitAccount sessionAccount = optionalAccount.get();
		logger.debug("Debit Account exists, details retrieved from database");
		List<DebitAccountTransaction> allTransactions = debitAccountTransactionService
				.findTransactionsOfAccount(sessionAccount);
		List<DebitAccountTransaction> accountTransactions = new ArrayList<>();
		for (DebitAccountTransaction transaction : allTransactions) {
			if (transaction.getDebitAccountTransactionType().equals(transactionType)) {
				accountTransactions.add(transaction);
			} else if (transactionType.equals("all")) {
				accountTransactions = debitAccountTransactionService.findTransactionsOfAccount(sessionAccount);

			}
		}

		session.setAttribute("accountTransactions", accountTransactions);
		model.addAttribute("account", sessionAccount);
		return "view-transaction-history";
	}

}
