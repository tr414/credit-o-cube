package com.fdmgroup.creditocube.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;

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

	// each card type can belong to multiple credit cards
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_card_type_id")
	private CardType cardType;

	private String cardNumber;

	private double balance;

	private double cardLimit; // changed from int to double as cardLimit is a currency value
	
	@OneToOne(mappedBy="card")
	private Bill bill;

	public CreditCard() {

	}

	public CreditCard(Customer customer, String cardNumber, int balance, int cardLimit, CardType cardType) {

		this.customer = customer;
		this.cardNumber = cardNumber;
		this.balance = balance;
		this.cardLimit = cardLimit;
		this.cardType = cardType;
	}

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

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getCardLimit() {
		return cardLimit;
	}

	public void setCardLimit(double cardLimit) {
		this.cardLimit = cardLimit;
	}

	public CardType getCardType() {
		return cardType;
	}

	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}

	public List<CreditCardTransaction> getCreditCardTransactions() {
		return creditCardTransactions;
	}

	public void setCreditCardTransactions(List<CreditCardTransaction> creditCardTransactions) {
		this.creditCardTransactions = creditCardTransactions;
	}

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}
	
}
