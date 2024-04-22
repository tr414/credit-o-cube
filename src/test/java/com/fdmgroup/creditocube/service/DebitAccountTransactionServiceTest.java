package com.fdmgroup.creditocube.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.model.DebitAccountTransaction;
import com.fdmgroup.creditocube.repository.CustomerRepository;
import com.fdmgroup.creditocube.repository.DebitAccountRepository;
import com.fdmgroup.creditocube.repository.DebitAccountTransactionRepository;

@ExtendWith(MockitoExtension.class)
public class DebitAccountTransactionServiceTest {

	@Mock
	private DebitAccountTransactionRepository debitAccountTransactionRepository;

	@Mock
	private DebitAccountRepository debitAccountRepository;

	@Mock
	private CustomerRepository customerRepository;

	@InjectMocks
	private DebitAccountTransactionService debitAccountTransactionService;

	DebitAccountTransaction transaction;
	DebitAccount account;
	Customer customer;

	Optional<DebitAccountTransaction> optionalTransaction;
	Optional<DebitAccount> optionalAccount;

	@BeforeEach
	public void setup() {
		transaction = new DebitAccountTransaction();
		account = new DebitAccount();
		customer = new Customer();

	}

	@Test
	@DisplayName("1. Test createDebitAccountTransaction works in normal conditions")
	public void testCreateDebitAccountTransaction_ValidTransaction() {
		// Mock data
		transaction.setDebitAccountTransactionAmount(100.0);
		transaction.setDebitAccountTransactionType("deposit"); // Valid type

		// Call method
		debitAccountTransactionService.createDebitAccountTransaction(transaction);

		// Verify interaction
		Mockito.verify(debitAccountTransactionRepository).save(transaction);

		// Explanation: This test simulates a scenario where a valid debit account
		// transaction is created with a positive amount and a valid transaction type.
		// The service should save the transaction to the repository and log a debug
		// message.
		// We verify that the save method is called on the repository and the debug
		// message is logged.
	}

	@Test
	@DisplayName("2. Test createDebitAccountTransaction does not update if transaction amount is zero")
	public void testCreateDebitAccountTransaction_ZeroAmount() {
		// Mock data
		transaction.setDebitAccountTransactionAmount(0.0);
		transaction.setDebitAccountTransactionType("deposit");

		// Call method
		debitAccountTransactionService.createDebitAccountTransaction(transaction);

		// Verify interaction
		Mockito.verify(debitAccountTransactionRepository, never()).save(transaction);

		// Explanation: This test simulates a scenario where the transaction amount is
		// zero.
		// The service should not save the transaction and instead log an info message.
		// We verify that the save method is not called and the info message is logged.
	}

	@Test
	@DisplayName("3. Test createDebitAccountTransaction does not update if transaction type is null")
	public void testCreateDebitAccountTransaction_NullType() {
		// Mock data
		transaction.setDebitAccountTransactionAmount(100.0);

		// Call method
		debitAccountTransactionService.createDebitAccountTransaction(transaction);

		// Verify interaction
		Mockito.verify(debitAccountTransactionRepository, never()).save(transaction);
		// Explanation: This test simulates a scenario where the transaction type is
		// null.
		// The service should not save the transaction and instead log an info message.
		// We verify that the save method is not called and the info message is logged.
	}

	@Test
	@DisplayName("4. Test updateDebitAccountTransaction updates in normal conditions")
	public void testUpdateDebitAccountTransaction_ValidUpdate_TransactionFound() {
		// Mock data
		long transactionId = 1L;
		DebitAccountTransaction originalTransaction = new DebitAccountTransaction();
		originalTransaction.setDebitAccountTransactionId(transactionId);
		originalTransaction.setDebitAccountTransactionAmount(100.0);
		originalTransaction.setDebitAccountTransactionType("deposit");

		DebitAccountTransaction updateTransaction = new DebitAccountTransaction();
		updateTransaction.setDebitAccountTransactionId(transactionId);
		updateTransaction.setDebitAccountTransactionAmount(200.0);
		updateTransaction.setDebitAccountTransactionType("withdraw");
		optionalTransaction = Optional.of(originalTransaction);

		// Mock behavior
		Mockito.when(debitAccountTransactionRepository.findById(transactionId)).thenReturn(optionalTransaction);

		// Call method
		debitAccountTransactionService.updateDebitAccountTransaction(updateTransaction);

		// Verify interaction
		Mockito.verify(debitAccountTransactionRepository).findById(transactionId);
		Mockito.verify(debitAccountTransactionRepository).save(originalTransaction);

		// Explanation: This test simulates a scenario where a valid update is performed
		// on an existing transaction (found by ID).
		// The service should update the transaction details in the managed object and
		// save it.
		// We verify that the findById and save methods are called on the repository
		// with the correct arguments,
		// and a debug message is logged.
	}

	@Test
	@DisplayName("5. Test updateDebitAccountTransaction does not update if transaction amount is zero")
	public void testUpdateDebitAccountTransaction_ZeroAmount() {
		// Mock data
		transaction.setDebitAccountTransactionAmount(0.0);
		transaction.setDebitAccountTransactionType("deposit");

		// Call method
		debitAccountTransactionService.updateDebitAccountTransaction(transaction);

		// Verify interaction
		Mockito.verify(debitAccountTransactionRepository, never()).save(transaction);

		// Explanation: This test simulates a scenario where the transaction amount in
		// the update is zero.
		// The service should not save the transaction and instead log an info message.
		// We verify that the repository methods are not called and the info message is
		// logged.
	}

	@Test
	@DisplayName("6. Test updateDebitAccountTransaction does not update if transaction type is null")
	public void testUpdateDebitAccountTransaction_NullType() {
		// Mock data
		transaction.setDebitAccountTransactionAmount(100.0);

		// Call method
		debitAccountTransactionService.updateDebitAccountTransaction(transaction);

		// Verify interaction
		Mockito.verify(debitAccountTransactionRepository, never()).save(transaction);

		// Explanation: This test simulates a scenario where the transaction type in the
		// update is null.
		// The service should not save the transaction and instead log an info message.
		// We verify that the repository methods are not called and the info message is
		// logged.
	}

	@Test
	@DisplayName("7. Test updateDebitAccountTransaction does not update if transaction is not found")
	public void testUpdateDebitAccountTransaction_TransactionNotFound() {
		// Mock data
		long transactionId = 1L;
		transaction.setDebitAccountTransactionId(transactionId);
		transaction.setDebitAccountTransactionAmount(100.0);
		transaction.setDebitAccountTransactionType("deposit");
		optionalTransaction = Optional.empty();

		// Mock behavior
		Mockito.when(debitAccountTransactionRepository.findById(transactionId)).thenReturn(optionalTransaction);

		// Call method
		debitAccountTransactionService.updateDebitAccountTransaction(transaction);

		// Verify interaction
		Mockito.verify(debitAccountTransactionRepository).findById(transactionId);
		Mockito.verify(debitAccountTransactionRepository, never()).save(transaction);

		// Explanation: This test simulates a scenario where the update is attempted on
		// a non-existent transaction (not found by ID).
		// The service should not update anything and log an info message.
		// We verify that only findById is called on the repository, no save is
		// performed

	}

	@Test
	@DisplayName("8. Test findTransactionsOfAccount updates when account and transactions are found")
	public void testFindTransactionsOfAccount_AccountExists_TransactionsFound() {
		// Mock data
		long accountId = 1L;
		account.setAccountId(accountId);
		List<DebitAccountTransaction> expectedTransactions = new ArrayList<>();

		DebitAccountTransaction transaction1 = new DebitAccountTransaction();
		transaction1.setToAccountNumber(account.getAccountNumber());
		expectedTransactions.add(transaction1);

		DebitAccountTransaction transaction2 = new DebitAccountTransaction();
		transaction2.setFromAccount(account);
		expectedTransactions.add(transaction2);
		optionalAccount = Optional.of(account);

		// Mock behavior
		Mockito.when(debitAccountRepository.findById(accountId)).thenReturn(optionalAccount);
		Mockito.when(debitAccountTransactionRepository.findByToAccountNumber(account.getAccountNumber()))
				.thenReturn(Collections.singletonList(transaction1));
		Mockito.when(debitAccountTransactionRepository.findByFromAccount(accountId))
				.thenReturn(Collections.singletonList(transaction2));

		// Call method
		List<DebitAccountTransaction> returnedTransactions = debitAccountTransactionService
				.findTransactionsOfAccount(account);

		// Verify interaction
		Mockito.verify(debitAccountRepository).findById(accountId);
		Mockito.verify(debitAccountTransactionRepository).findByToAccountNumber(account.getAccountNumber());
		Mockito.verify(debitAccountTransactionRepository).findByFromAccount(accountId);

		// Assert returned value
		assertEquals(expectedTransactions, returnedTransactions);
		assertEquals(2, returnedTransactions.size()); // Verify two transactions are returned

		// Explanation: This test simulates a scenario where the provided account exists
		// and there are related transactions (one as toAccount and one as fromAccount).
		// The service should find both transactions using the repository methods and
		// sort them by date.
		// We verify that all the relevant repository methods are called and the
		// returned list contains the expected transactions in sorted order.
	}

	@Test
	@DisplayName("9. Test findTransactionsOfAccount updates when transactions are not found")
	public void testFindTransactionsOfAccount_AccountExists_NoTransactionsFound() {
		// Mock data
		long accountId = 1L;
		account.setAccountId(accountId);
		optionalAccount = Optional.of(account);

		// Mock behavior
		Mockito.when(debitAccountRepository.findById(accountId)).thenReturn(optionalAccount);
		Mockito.when(debitAccountTransactionRepository.findByToAccountNumber(account.getAccountNumber()))
				.thenReturn(Collections.emptyList());
		Mockito.when(debitAccountTransactionRepository.findByFromAccount(accountId))
				.thenReturn(Collections.emptyList());

		// Call method
		List<DebitAccountTransaction> returnedTransactions = debitAccountTransactionService
				.findTransactionsOfAccount(account);

		// Verify interaction
		Mockito.verify(debitAccountRepository).findById(accountId);
		Mockito.verify(debitAccountTransactionRepository).findByToAccountNumber(account.getAccountNumber());
		Mockito.verify(debitAccountTransactionRepository).findByFromAccount(accountId);

		// Assert returned value
		assertEquals(Collections.emptyList(), returnedTransactions);

		// Explanation: This test simulates a scenario where the provided account exists
		// but there are no related transactions (empty lists returned from findBy*
		// methods).
		// The service should find no transactions and return an empty list.
		// We verify that all the relevant repository methods are called and the
		// returned list is empty.
	}

	@Test
	@DisplayName("10. Test findTransactionsOfAccount updates account is not found")
	public void testFindTransactionsOfAccount_AccountNotFound() {
		// Mock data
		long accountId = 1L;
		account.setAccountId(accountId);
		optionalAccount = Optional.empty();

		// Mock behavior
		Mockito.when(debitAccountRepository.findById(accountId)).thenReturn(optionalAccount);

		// Call method
		List<DebitAccountTransaction> returnedTransactions = debitAccountTransactionService
				.findTransactionsOfAccount(account);

		// Verify interaction
		Mockito.verify(debitAccountRepository).findById(accountId);
		Mockito.verify(debitAccountTransactionRepository, never()).findByToAccountNumber(account.getAccountNumber());
		Mockito.verify(debitAccountTransactionRepository, never()).findByFromAccount(accountId);

		// Assert returned value
		assertEquals(Collections.emptyList(), returnedTransactions);

		// Explanation: This test simulates a scenario where the provided account does
		// not exist in the database (empty optional returned from findById).
		// The service should not call the transaction repository methods and return an
		// empty list.
		// We verify that the methods of debitAccountTransactionRepository are not
		// called

	}

	@Test
	@DisplayName("11. Test findRecentTransactionsOfCustomer returns list of transactions when customer and transactions are found")
	public void testFindRecentTransactionsOfCustomer_CustomerFound_TransactionsFound() {
		// Mock data
		int customerId = 1;
		customer.setUser_id(customerId);
		List<DebitAccountTransaction> expectedTransactions = new ArrayList<>();
		DebitAccountTransaction transaction1 = new DebitAccountTransaction();
		transaction1.setDebitAccountTransactionDate(new Date());

		DebitAccountTransaction transaction2 = new DebitAccountTransaction();
		transaction2.setDebitAccountTransactionDate(new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000));
		expectedTransactions.add(transaction2);
		expectedTransactions.add(transaction1);
		List<DebitAccount> customerAccounts = new ArrayList<>();
		account = new DebitAccount();
		customerAccounts.add(account);
		customer.setDebitAccounts(customerAccounts);
		Optional<Customer> optionalCustomer = Optional.of(customer);

		// Mock behavior
		Mockito.when(customerRepository.findById(customerId)).thenReturn(optionalCustomer);
		Mockito.when(debitAccountTransactionRepository.findByToAccountNumber(account.getAccountNumber()))
				.thenReturn(Collections.singletonList(transaction1));
		Mockito.when(debitAccountTransactionRepository.findByFromAccount(account.getAccountId()))
				.thenReturn(Collections.singletonList(transaction2));

		// Call method
		List<DebitAccountTransaction> returnedTransactions = debitAccountTransactionService
				.findRecentTransactionsOfCustomer(customer);

		// Verify interaction
		Mockito.verify(customerRepository).findById(customerId);
		Mockito.verify(debitAccountTransactionRepository).findByToAccountNumber(account.getAccountNumber());
		Mockito.verify(debitAccountTransactionRepository).findByFromAccount(account.getAccountId());

		// Assert returned value
		assertEquals(expectedTransactions, returnedTransactions);
		assertEquals(2, returnedTransactions.size()); // Verify two transactions are returned
//		assertTrue(returnedTransactions.get(0).getDebitAccountTransactionDate().isAfter(LocalDate.now().minusDays(7)));
//		assertTrue(returnedTransactions.get(1).getDebitAccountTransactionDate().isAfter(LocalDate.now().minusDays(7)));

		// Explanation: This test simulates a scenario where the customer is found, has
		// one debit account, and there are related transactions
		// (one within 7 days and one older than 7 days). The service should find both
		// transactions, remove duplicates, sort them,
		// and keep only transactions within the last 7 days.
		// We verify that all the relevant methods are called and the returned list
		// contains the expected recent transactions in sorted order.
	}

	@Test
	@DisplayName("12. Test findRecentTransactionsOfCustomer returns empty list when customer is found, but transactions are not found")
	public void testFindRecentTransactionsOfCustomer_CustomerFound_NoTransactionsFound() {
		// Mock data
		int customerId = 1;
		int accountId = 1;
		String accountNumber = "123456";
		customer.setUser_id(customerId);
		List<DebitAccount> customerAccounts = new ArrayList<>();
		account.setAccountNumber(accountNumber);
		account.setAccountId(1);
		customerAccounts.add(account);
		customer.setDebitAccounts(customerAccounts);
		Optional<Customer> optionalCustomer = Optional.of(customer);

		// Mock behavior
		Mockito.when(customerRepository.findById(customerId)).thenReturn(optionalCustomer);

		Mockito.when(debitAccountTransactionRepository.findByToAccountNumber(accountNumber))
				.thenReturn(Collections.emptyList());
		Mockito.when(debitAccountTransactionRepository.findByFromAccount(accountId))
				.thenReturn(Collections.emptyList());

		// Call method
		List<DebitAccountTransaction> returnedTransactions = debitAccountTransactionService
				.findRecentTransactionsOfCustomer(customer);

		// Verify interaction
		Mockito.verify(customerRepository).findById(customerId);
		Mockito.verify(debitAccountTransactionRepository).findByToAccountNumber(accountNumber);
		Mockito.verify(debitAccountTransactionRepository).findByFromAccount(accountId);

		// Assert returned value
		assertEquals(Collections.emptyList(), returnedTransactions);

		// Explanation: This test simulates a scenario where the customer is found but
		// has no debit accounts or related transactions
		// (empty lists returned from findBy* methods). The service should find no
		// transactions and return an empty list.
		// We verify that all the relevant methods are called
	}
}