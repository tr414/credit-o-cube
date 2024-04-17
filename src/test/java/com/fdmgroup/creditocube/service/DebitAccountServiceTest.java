package com.fdmgroup.creditocube.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.repository.CustomerRepository;
import com.fdmgroup.creditocube.repository.DebitAccountRepository;

@ExtendWith(MockitoExtension.class)
public class DebitAccountServiceTest {

	@Mock
	private DebitAccountRepository debitAccountRepository;

	@Mock
	private CustomerRepository customerRepository;

	@InjectMocks
	private DebitAccountService debitAccountService;

	@Test
	@DisplayName("1. Test that createAccount does not create a new account if an existing account is present")
	public void testCreateAccount_AccountExists() {
		// Mock data
		DebitAccount account = new DebitAccount();
		account.setAccountNumber(123456);
		Optional<DebitAccount> optionalAccount = Optional.of(account);

		// Mock behavior
		Mockito.when(debitAccountRepository.findByAccountNumber(account.getAccountNumber()))
				.thenReturn(optionalAccount);

		// Call method
		debitAccountService.createAccount(account);

		// Verify interaction
		Mockito.verify(debitAccountRepository, Mockito.times(1)).findByAccountNumber(account.getAccountNumber());
		Mockito.verifyNoInteractions(customerRepository);

		// Explanation: This test case simulates a scenario where an account with the
		// provided account number already exists in the database.
		// The service should check for existing accounts first and prevent duplicate
		// accounts from being created.
		// We verify that only the findByAccountNumber method from
		// debitAccountRepository is called,
		// and no interactions occur with the customerRepository since the account
		// already exists.
	}

	@Test
	public void testCreateAccount_CustomerNotFound() {
		// Mock data
		DebitAccount account = new DebitAccount();
		account.setAccountNumber(123456);
		Customer customer = new Customer();
		customer.setUser_id(1);
		account.setCustomer(customer);
		Optional<DebitAccount> optionalAccount = Optional.empty();
		Optional<Customer> optionalCustomer = Optional.empty();

		// Mock behavior
		Mockito.when(debitAccountRepository.findByAccountNumber(account.getAccountNumber()))
				.thenReturn(optionalAccount);
		Mockito.when(customerRepository.findById(customer.getUser_id())).thenReturn(optionalCustomer);

		// Call method
		debitAccountService.createAccount(account);

		// Verify interaction
		Mockito.verify(debitAccountRepository, Mockito.times(1)).findByAccountNumber(account.getAccountNumber());
		Mockito.verify(customerRepository, Mockito.times(1)).findById(customer.getUser_id());
		Mockito.verifyNoInteractions(debitAccountRepository);

		// Explanation: This test case simulates a scenario where the provided account
		// number doesn't correspond to an existing account,
		// but the customer ID linked to the account is not found in the customer
		// repository.
		// The service should check for existing accounts first and then find the
		// customer by ID.
		// We verify that both findByAccountNumber and findById methods are called from
		// their respective repositories,
		// but no save methods are called since the customer is not found.
	}

	@Test
	public void testCreateAccount_MaxAccountsReached() {
		// Mock data
		DebitAccount account = new DebitAccount();
		account.setAccountNumber(123456);
		Customer customer = new Customer();
		customer.setUser_id(1);
		List<DebitAccount> accountList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			accountList.add(new DebitAccount());
		}
		customer.setDebitAccounts(accountList);
		account.setCustomer(customer);
		Optional<DebitAccount> optionalAccount = Optional.empty();
		Optional<Customer> optionalCustomer = Optional.of(customer);

		// Mock behavior
		Mockito.when(debitAccountRepository.findByAccountNumber(account.getAccountNumber()))
				.thenReturn(optionalAccount);
		Mockito.when(customerRepository.findById(customer.getUser_id())).thenReturn(optionalCustomer);

		// Call method
		debitAccountService.createAccount(account);

		// Verify interaction
		Mockito.verify(debitAccountRepository, Mockito.times(1)).findByAccountNumber(account.getAccountNumber());
		Mockito.verify(customerRepository, Mockito.times(1)).findById(customer.getUser_id());
		Mockito.verifyNoInteractions(debitAccountRepository);

		// Explanation: This test case simulates a scenario where the customer has
		// already reached the maximum allowed number of debit accounts (5).
		// The service should check the customer's existing accounts before creating a
		// new one.
		// We verify that both findByAccountNumber and findById methods are called,
		// but no save methods are called since the customer cannot have more than 5
		// accounts.
	}

	@Test
	public void testUpdateAccount_AccountExists() {
		// Mock data
		DebitAccount account = new DebitAccount();
		account.setAccountNumber(123456);
		Optional<DebitAccount> optionalAccount = Optional.of(account);

		// Mock behavior
		Mockito.when(debitAccountRepository.findByAccountNumber(account.getAccountNumber()))
				.thenReturn(optionalAccount);

		// Call method
		debitAccountService.updateAccount(account);

		// Verify interaction
		Mockito.verify(debitAccountRepository, Mockito.times(1)).findByAccountNumber(account.getAccountNumber());
		Mockito.verify(debitAccountRepository, Mockito.times(1)).save(account);

		// Explanation: This test case simulates a scenario where an account with the
		// provided account number exists in the database.
		// The service should find the account by number and then persist the updated
		// account details.
		// We verify that both findByAccountNumber and save methods are called from the
		// debitAccountRepository.
	}

	@Test
	public void testUpdateAccount_AccountNotFound() {
		// Mock data
		DebitAccount account = new DebitAccount();
		account.setAccountNumber(123456);
		Optional<DebitAccount> optionalAccount = Optional.empty();

		// Mock behavior
		Mockito.when(debitAccountRepository.findByAccountNumber(account.getAccountNumber()))
				.thenReturn(optionalAccount);

		// Call method
		debitAccountService.updateAccount(account);

		// Verify interaction
		Mockito.verify(debitAccountRepository, Mockito.times(1)).findByAccountNumber(account.getAccountNumber());
		Mockito.verifyNoInteractions(debitAccountRepository);

		// Explanation: This test case simulates a scenario where the provided account
		// number doesn't correspond to an existing account.
		// The service should check for the account by number before attempting to
		// update.
		// We verify that only the findByAccountNumber method is called, and no save
		// methods are called since the account is not found.
	}

//	@Test
//	@DisplayName("2. Test updateAccount returns an account with the valid number")
//	public void testUpdateAccount() {
//		when(debitAccountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));
//		debitAccountService.updateAccount(account);
//		assertTrue(debitAccountRepository.findByAccountNumber(account.getAccountNumber()).isPresent());
//	}
//
//	@Test
//	@DisplayName("3. Test findDebitAccountByAccountNumber returns the correct account")
//	public void testFindDebitAccountByAccountNumber() {
//		when(debitAccountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));
//		Optional<DebitAccount> result = debitAccountService.findDebitAccountByAccountNumber(account.getAccountNumber());
//		assertTrue(result.isPresent());
//		assertEquals(account, result.get());
//	}
//
//	@Test
//	@DisplayName("4. Test findAllDebitAccountsForCustomer returns a list of accounts when customer exists")
//	public void testFindAllDebitAccountsForCustomer() {
//
//		when(userRepository.findById(customer.getUser_id())).thenReturn(Optional.of(customer));
//		when(debitAccountRepository.findAll()).thenReturn(List.of(account));
//		List<DebitAccount> result = debitAccountService.findAllDebitAccountsForCustomer(customer);
//		assertTrue(result.contains(account));
//	}
//
//	@Test
//	@DisplayName("5. Test closeDebitAccount deletes correct account")
//	public void testCloseDebitAccount() {
//		when(debitAccountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));
//		debitAccountService.closeDebitAccount(account);
//
//		Mockito.verify(debitAccountRepository).delete(account);
//
//	}
}
