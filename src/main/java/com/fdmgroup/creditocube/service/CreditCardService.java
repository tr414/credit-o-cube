package com.fdmgroup.creditocube.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.repository.CreditCardRepository;
import com.fdmgroup.creditocube.repository.CustomerRepository;

@Service
public class CreditCardService {

	@Autowired
	private CreditCardRepository creditCardRepository;

	@Autowired
	private CustomerRepository customerRepository;

	public void createCreditCard(CreditCard card) {

		// determine if card exists in database
		Optional<CreditCard> optionalCard = creditCardRepository.findById(card.getCardId());

		if (optionalCard.isPresent()) {
			System.out.println("Card already exists: " + optionalCard.get().getCardId());
			return;
		}

		// determine if customer exists in database
		Customer target = card.getCustomer();

		Optional<Customer> optionalCustomer = customerRepository.findById(target.getUser_id());

		if (optionalCustomer.isEmpty()) {
			System.out.println("Customer not found");
			return;
		}

		// determine if customer has 3 or more credit cards
		Customer cardHolder = optionalCustomer.get();
		List<CreditCard> cardList = cardHolder.getCreditCards();

		if (cardList.size() >= 3) {
			System.out.println("You cannot have 3 or more cred it cards per customer.");
			return;
		}

		// otherwise, add account
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
		String accountNumber;
		final Random random = new Random();
		do {
			long number = (long) (100000000 + random.nextInt(900000000));
			accountNumber = String.format("%09d", number);
		} while (cardNumberExists(accountNumber));
		return accountNumber;

	}

	// method to close the card

}
