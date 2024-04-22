package com.fdmgroup.creditocube.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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

@Service
public class CreditCardService {

	@Autowired
	private CreditCardRepository creditCardRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CreditCardTransactionRepository creditCardTransactionRepository;

	private static Logger logger = LogManager.getLogger(DebitAccountService.class);

//	@Autowired
//	private DebitAccountService debitAccountService;

	public void createCreditCard(CreditCard card) {

		// determine if card exists in database
		Optional<CreditCard> optionalCard = creditCardRepository.findById(card.getCardId());

		// if the credit card is already present in the database, just return that
		if (optionalCard.isPresent()) {
			System.out.println("Card already exists: " + optionalCard.get().getCardId());
			return;
		}

		// determine if customer exists in database
		Customer target = card.getCustomer();

		Optional<Customer> optionalCustomer = customerRepository.findById(target.getUser_id());

		// if that card does not belong to a customer
		if (optionalCustomer.isEmpty()) {
			System.out.println("Customer not found");
			return;
		}

		// determine if customer has 3 or more credit cards
		Customer cardHolder = optionalCustomer.get(); // get the customer who wants to apply for a card
		List<CreditCard> cardList = cardHolder.getCreditCards();

		// if they already hold 3 or more cards, cannot make new ones
		if (cardList.size() >= 3) {
			System.out.println("You cannot have 3 or more credit cards per customer.");
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
			return;
		}
		CreditCard managedCard = optionalCard.get();

		Optional<CreditCardTransaction> optionalTransaction = creditCardTransactionRepository
				.findById(transaction.getTransactionId());
		if (optionalTransaction.isEmpty()) {
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
		logger.debug("currentBalance: " + currentBalance);
		managedCard.setCreditCardTransactions(currentTransactions);

		creditCardRepository.save(managedCard);

	}

	public Optional<CreditCard> findCardByCardNumber(String cardNumber) {

		Optional<CreditCard> optionalCard = creditCardRepository.findByCardNumber(cardNumber);

		if (optionalCard.isEmpty()) {
			return Optional.empty();
		} else {
			return optionalCard;
		}
	}

	public Optional<CreditCard> findCardByCardId(long id) {

		Optional<CreditCard> optionalCard = creditCardRepository.findById(id);

		if (optionalCard.isEmpty()) {
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

	// method to close the card

	// method calculates how much a customer needs to pay if its not a custom amount
	public double calculateAmountPayable(String paymentOption, double balance) {
		if (paymentOption.equals("Pay Current Balance")) {
			System.out.println("Paying current balance");
			// i need to find a debit account and deduct from it
			// what's the balance I need to pay
			return balance;
		} else if (paymentOption.equals("Pay minimum")) {
			return balance * 0.03;
		}
		System.out.println("Invalid payment option");
		return 0;

	}

	public List<CreditCard> findAllCreditCards() {
		return creditCardRepository.findAll();
	}

	public boolean customerAlreadyHasCardType(Customer customer, CardType cardType) {
		return creditCardRepository.findAllByCustomer(customer).stream()
				.anyMatch(card -> card.getCardType().equals(cardType));
	}

}
