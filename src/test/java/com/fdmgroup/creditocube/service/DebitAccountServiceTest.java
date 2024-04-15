package com.fdmgroup.creditocube.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

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
import com.fdmgroup.creditocube.model.User;
import com.fdmgroup.creditocube.repository.DebitAccountRepository;
import com.fdmgroup.creditocube.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class DebitAccountServiceTest {

	@Mock
	private DebitAccountRepository debitAccountRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private DebitAccountService debitAccountService;

	private DebitAccount account;
	private Customer customer;
	private User user;

	@BeforeEach
	public void setUp() {
		account = new DebitAccount();
		account.setAccountNumber(123456L);
		customer = new Customer();
		customer.setUser_id(1);
		account.setCustomer(customer);

	}

	@Test
	@DisplayName("1. Test createAccount creates an account with an account number")
	public void testCreateAccount() {
		when(debitAccountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.empty());
		debitAccountService.createAccount(account);

		Mockito.verify(debitAccountRepository).findByAccountNumber(123456L);
		Mockito.verify(debitAccountRepository).save(account);

	}

	@Test
	@DisplayName("2. Test updateAccount returns an account with the valid number")
	public void testUpdateAccount() {
		when(debitAccountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));
		debitAccountService.updateAccount(account);
		assertTrue(debitAccountRepository.findByAccountNumber(account.getAccountNumber()).isPresent());
	}

	@Test
	@DisplayName("3. Test findDebitAccountByAccountNumber returns the correct account")
	public void testFindDebitAccountByAccountNumber() {
		when(debitAccountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));
		Optional<DebitAccount> result = debitAccountService.findDebitAccountByAccountNumber(account.getAccountNumber());
		assertTrue(result.isPresent());
		assertEquals(account, result.get());
	}

	@Test
	@DisplayName("4. Test findAllDebitAccountsForCustomer returns a list of accounts when customer exists")
	public void testFindAllDebitAccountsForCustomer() {

		when(userRepository.findById(customer.getUser_id())).thenReturn(Optional.of(customer));
		when(debitAccountRepository.findAll()).thenReturn(List.of(account));
		List<DebitAccount> result = debitAccountService.findAllDebitAccountsForCustomer(customer);
		assertTrue(result.contains(account));
	}

	@Test
	@DisplayName("5. Test closeDebitAccount deletes correct account")
	public void testCloseDebitAccount() {
		when(debitAccountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));
		debitAccountService.closeDebitAccount(account);

		Mockito.verify(debitAccountRepository).delete(account);

	}
}
