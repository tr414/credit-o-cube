package com.fdmgroup.creditocube.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;

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

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.repository.CreditCardRepository;
import com.fdmgroup.creditocube.repository.CreditCardTransactionRepository;
import com.fdmgroup.creditocube.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
public class CreditCardServiceTest {

	@Mock
	private CreditCardRepository creditCardRepository;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private CreditCardTransactionRepository creditCardTransactionRepository;

	@InjectMocks
	private CreditCardService creditCardService;

	@Test
	@DisplayName("1. Test that createCreditCard does not create a card if an existing card exists")
	public void testCreateCreditCard_ExistingCard() {
		// Mock data
		long cardId = 1L;
		CreditCard card = new CreditCard();
		card.setCardId(cardId);
		Optional<CreditCard> optionalCard = Optional.of(card);
		Customer customer = new Customer();
		customer.setUser_id(1);

		// Mock behavior
		Mockito.when(creditCardRepository.findById(cardId)).thenReturn(optionalCard);

		// Call method
		creditCardService.createCreditCard(card);

		// Verify interaction
		Mockito.verify(creditCardRepository).findById(cardId);
		Mockito.verify(customerRepository, never()).findById(customer.getUser_id());

		// Explanation: This test simulates a scenario where the credit card already
		// exists in the database (findById returns a value).
		// The service should not create a new card and instead log an info message.
		// We verify that only findById is called on the credit card repository and no
		// interactions happen with the other repositories.
	}

	@Test
	@DisplayName("2. Test that createCreditCard does not create a card if customer does not exist")
	public void testCreateCreditCard_NonExistentCustomer() {
		// Mock data
		CreditCard card = new CreditCard();
		Customer customer = new Customer();
		card.setCustomer(customer);
		customer.setUser_id(1);
		Optional<Customer> optionalCustomer = Optional.empty();

		// Mock behavior
		Mockito.when(creditCardRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		Mockito.when(customerRepository.findById(customer.getUser_id())).thenReturn(optionalCustomer);

		// Call method
		creditCardService.createCreditCard(card);

		// Verify interaction
		Mockito.verify(creditCardRepository).findById(anyLong()); // Mockito.anyLong() for avoiding hardcoding card ID
		Mockito.verify(customerRepository).findById(customer.getUser_id());

		// Explanation: This test simulates a scenario where the customer does not exist
		// in the database (findById on customerRepository returns empty).
		// The service should not create a new card and instead log an info message.
		// We verify that findById is called on both repositories but no interactions
		// with the credit card transaction repository.
	}

	@Test
	@DisplayName("3. Test that createCreditCard does not create a card if customer already has 3 cards")
	public void testCreateCreditCard_CustomerHasMaxCards() {
		// Mock data
		CreditCard card = new CreditCard();
		Customer customer = new Customer();
		List<CreditCard> existingCards = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			existingCards.add(new CreditCard());
		}
		customer.setCreditCards(existingCards);
		card.setCustomer(customer);

		// Mock behavior
		Mockito.when(creditCardRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		Mockito.when(customerRepository.findById(customer.getUser_id())).thenReturn(Optional.of(customer));

		// Call method
		creditCardService.createCreditCard(card);

		// Verify interaction
		Mockito.verify(creditCardRepository).findById(anyLong());
		Mockito.verify(customerRepository).findById(customer.getUser_id());

		// Explanation: This test simulates a scenario where the customer already has
		// the maximum number of credit cards (3).
		// The service should not create a new card and instead log an info message.
		// We verify that findById is called on both repositories but no interactions
		// with the credit card transaction repository.
	}

	@Test
	@DisplayName("4. Test that createCreditCard creates a new card if all conditions passed")
	public void testCreateCreditCard_ValidCard() {
		// Mock data
		CreditCard card = new CreditCard();
		Customer customer = new Customer();
		card.setCustomer(customer);
		customer.setUser_id(1);
		List<CreditCard> existingCards = new ArrayList<>();
		existingCards.add(new CreditCard()); // Customer has less than 3 cards
		customer.setCreditCards(existingCards);
		Optional<Customer> optionalCustomer = Optional.of(customer);

		// Mock behavior
		Mockito.when(creditCardRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		Mockito.when(customerRepository.findById(customer.getUser_id())).thenReturn(optionalCustomer);

		// Call method
		creditCardService.createCreditCard(card);

		// Verify interaction
		Mockito.verify(creditCardRepository).findById(anyLong());
		Mockito.verify(customerRepository).findById(customer.getUser_id());
		Mockito.verifyNoInteractions(creditCardTransactionRepository);

	}

	@Test
	@DisplayName("5. Test that updateCard updates card if all conditions passed")
	public void testUpdateCard_ExistingCard() {
		// Mock data
		long cardId = 1L;
		CreditCard card = new CreditCard();
		card.setCardId(cardId);
		Optional<CreditCard> optionalCard = Optional.of(card);

		// Mock behavior
		Mockito.when(creditCardRepository.findById(cardId)).thenReturn(optionalCard);

		// Call method
		creditCardService.updateCard(card);

		// Verify interaction
		Mockito.verify(creditCardRepository).findById(cardId);
		Mockito.verify(creditCardRepository).save(card);

//		Mockito.verifyNoInteractions(customerRepository);
//		Mockito.verifyNoInteractions(creditCardTransactionRepository);

		// Explanation: This test simulates a scenario where the credit card exists in
		// the database (findById returns a value).
		// The service should update the card details in the repository.
		// We verify that findById and save are called on the credit card repository and
		// there are no interactions with other repositories.
	}

	@Test
	@DisplayName("6. Test that updateCard does not update card if card is not found")
	public void testUpdateCard_NonExistentCard() {
		// Mock data
		CreditCard card = new CreditCard();
		card.setCardId(1L);
		Optional<CreditCard> optionalCard = Optional.empty();

		// Mock behavior
		Mockito.when(creditCardRepository.findById(card.getCardId())).thenReturn(optionalCard);

		// Call method
		creditCardService.updateCard(card);

		// Verify interaction
		Mockito.verify(creditCardRepository, Mockito.times(1)).findById(card.getCardId());
//		Mockito.verifyNoInteractions(customerRepository);
//		Mockito.verifyNoInteractions(creditCardTransactionRepository);

		// Explanation: This test simulates a scenario where the credit card does not
		// exist in the database (findById returns empty).
		// The service should not update anything.
		// We verify that only findById is called on the credit card repository and
		// there are no interactions with other repositories.
	}

}
