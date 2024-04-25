package com.fdmgroup.creditocube.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.CardType;
import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.CreditCardTransaction;
import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.repository.CreditCardRepository;
import com.fdmgroup.creditocube.repository.CreditCardTransactionRepository;
import com.fdmgroup.creditocube.repository.CustomerRepository;

/**
 * CreditCardService class provides methods to manage credit card operations.
 *
 * 
 */
@Service
public class CreditCardService {

	/**
	 * Autowired CreditCardRepository for managing credit card operations.
	 */
	@Autowired
	private CreditCardRepository creditCardRepository;

	/**
	 * Autowired CustomerRepository for managing customer operations.
	 */
	@Autowired
	private CustomerRepository customerRepository;

	/**
	 * Autowired CreditCardTransactionRepository for managing credit card
	 * transactions.
	 */
	@Autowired
	private CreditCardTransactionRepository creditCardTransactionRepository;

	/**
	 * Creates a new credit card for a customer.
	 *
	 * @param card the credit card to be created
	 * @return void
	 */

	private static Logger logger = LogManager.getLogger(DebitAccountService.class);

	/**
	 * Creates a new credit card for a customer.
	 *
	 * @param card the credit card to be created
	 * @return void
	 * 
	 */
	public void createCreditCard(CreditCard card) {
		// determine if card exists in database
		Optional<CreditCard> optionalCard = creditCardRepository.findById(card.getCardId());

		// if the credit card is already present in the database, just return that
		if (optionalCard.isPresent()) {
			logger.info("Card already exists: ");
			return;
		}

		// determine if customer exists in database
		Customer target = card.getCustomer();

		Optional<Customer> optionalCustomer = customerRepository.findById(target.getUser_id());

		// if that card does not belong to a customer
		if (optionalCustomer.isEmpty()) {
			logger.info("Customer not found");
			return;
		}

		// determine if customer has 3 or more credit cards
		Customer cardHolder = optionalCustomer.get(); // get the customer who wants to apply for a card
		List<CreditCard> cardList = cardHolder.getCreditCards();
		List<CreditCard> activeCardList = cardList.stream().filter(c -> c.isActive()).collect(Collectors.toList());

		// if they already hold 3 or more cards, cannot make new ones
		if (activeCardList.size() >= 3) {
			logger.info("Customer tried to apply for a card when he/she already has 3 active cards");
			return;
		}

		// otherwise, add card
		cardList.add(card);
		cardHolder.setCreditCards(cardList);
		creditCardRepository.save(card);
		customerRepository.save(cardHolder);
	}

	public void updateCard(CreditCard card) {

		// determine if account exists in database
		Optional<CreditCard> optionalCard = creditCardRepository.findById(card.getCardId());

		// if account exists, persist
		if (optionalCard.isPresent()) {
			creditCardRepository.save(card);
		}
	}

	public void updateBalance(CreditCard card, CreditCardTransaction transaction) {
		// determine if account exists in database
		Optional<CreditCard> optionalCard = creditCardRepository.findById(card.getCardId());

		// if account exists, persist
		if (optionalCard.isEmpty()) {
			logger.info("Card not found in database");
			return;
		}
		CreditCard managedCard = optionalCard.get();

		Optional<CreditCardTransaction> optionalTransaction = creditCardTransactionRepository
				.findById(transaction.getTransactionId());
		if (optionalTransaction.isEmpty()) {
			logger.info("Transaction not found in database");
			return;
		}
		CreditCardTransaction managedTransaction = optionalTransaction.get();

		List<CreditCardTransaction> currentTransactions = managedCard.getCreditCardTransactions();

		currentTransactions.add(managedTransaction);
		double currentBalance = 0.0;
		for (CreditCardTransaction transactionItem : currentTransactions) {
			currentBalance += transactionItem.getTransactionAmount();
		}
		managedCard.setBalance(currentBalance);
		logger.debug("updated card balance: " + currentBalance);
		managedCard.setCreditCardTransactions(currentTransactions);

		creditCardRepository.save(managedCard);

	}

	public Optional<CreditCard> findCardByCardNumber(String cardNumber) {

		Optional<CreditCard> optionalCard = creditCardRepository.findByCardNumber(cardNumber);

		if (optionalCard.isEmpty()) {
			logger.info("Card not found");
			return Optional.empty();
		} else {
			return optionalCard;
		}
	}

	public Optional<CreditCard> findCardByCardId(long id) {

		Optional<CreditCard> optionalCard = creditCardRepository.findById(id);

		if (optionalCard.isEmpty()) {
			logger.info("Card not found");
			return Optional.empty();
		} else {
			return optionalCard;
		}
	}

	public List<CreditCard> findAllCardsForCustomer(Customer customer) {

		// find all accounts
		List<CreditCard> allCardstList = creditCardRepository.findAll();
		List<CreditCard> customerCards = new ArrayList<>();

		// check if customer exists
		Optional<Customer> optionalCustomer = customerRepository.findById(customer.getUser_id());
		if (optionalCustomer.isEmpty()) {
			logger.info("Customer not found, returning empty list");
			return customerCards;
		}

		Customer targetCustomer = optionalCustomer.get();

		// retrieve acounts that belong to a customer
		allCardstList.forEach(card -> {
			if (card.getCustomer() == targetCustomer) {
				customerCards.add(card);
			}
		});
		return customerCards;

	}

	private boolean cardNumberExists(String cardNumber) {
		Optional<CreditCard> optionalCard = creditCardRepository.findByCardNumber(cardNumber);

		return optionalCard.isPresent();
	}

	public String generateCreditCardNumber() {

		String accountNumber = "";
		final Random RANDOM = new Random();
		do {
			for (int i = 0; i < 4; i++) {
				long number = RANDOM.nextLong(9999);
				String stringNumber = String.format("%04d", number);
				accountNumber = accountNumber + stringNumber;
			}
		} while (cardNumberExists(accountNumber));
		return accountNumber;

	}

	public List<CreditCard> findAllCreditCards() {
		return creditCardRepository.findAll();
	}
	
	public List<CreditCard> findAllActiveCreditCards() {
		return creditCardRepository.findByIsActiveTrue();
	}

	public boolean customerAlreadyHasCardType(Customer customer, CardType cardType) {
		return creditCardRepository.findAllByCustomer(customer).stream()
				.anyMatch(card -> card.getCardType().equals(cardType) && card.isActive());
	}

	// close credit card
	public void closeCreditCard(CreditCard card) {
		// close only if the card balance is zero
		if (card.getBalance() == 0.0) {
			card.setActive(false);
			creditCardRepository.save(card);
		} else {
			logger.info("Card balance is not zero, cannot close card");
		}

	}

	public List<CreditCard> findAllActiveCreditCardsForCustomer(Customer sessionCustomer) {
		// TODO Auto-generated method stub
		long customerId = sessionCustomer.getUser_id();
		return creditCardRepository.findAllActiveCreditCardsofCustomer(customerId);
	}

	public List<CreditCard> findAllInactiveCreditCardsForCustomer(Customer sessionCustomer) {
		// TODO Auto-generated method stub
		long customerId = sessionCustomer.getUser_id();
		return creditCardRepository.findAllInactiveCreditCardsofCustomer(customerId);
	}

}
