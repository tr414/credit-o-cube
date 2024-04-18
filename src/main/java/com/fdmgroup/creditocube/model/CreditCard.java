package com.fdmgroup.creditocube.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class CreditCard {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long cardId;

	@ManyToOne
	@JoinColumn(name = "FK_USER_ID")
	private Customer customer;

	@OneToMany(mappedBy = "transactionCard")
	private List<CreditCardTransaction> creditCardTransactions;

	private String cardNumber;

	private int balance;

	private int cardLimit;

//	@OneToOne
//	private CardType cardType;

	public long getCardId() {
		return cardId;
	}

	public void setCardId(long cardId) {
		this.cardId = cardId;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getCardLimit() {
		return cardLimit;
	}

	public void setCardLimit(int cardLimit) {
		this.cardLimit = cardLimit;
	}

//	public CardType getCardType() {
//		return cardType;
//	}
//
//	public void setCardType(CardType cardType) {
//		this.cardType = cardType;
//	}

}
