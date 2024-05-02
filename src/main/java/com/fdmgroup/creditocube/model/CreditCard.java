package com.fdmgroup.creditocube.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

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
	
	@OneToMany(mappedBy = "transactionCard")
	private List<InstallmentPayment> installmentPayments;

	// each card type can belong to multiple credit cards
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_card_type_id")
	private CardType cardType;

	private String cardNumber;

	private double balance;

	private double cardLimit; // changed from int to double as cardLimit is a currency value
	
	private double cashback;
	
	private double cashbackCarriedForward;
	
	private double monthlySpend;

	@Column(name = "card_is_active")
	private boolean isActive;

	@OneToOne(mappedBy = "card")
	private Bill bill;

	public CreditCard() {
		this.cashback = 0.0;
		this.monthlySpend = 0.0;
		setActive(true);
		setCashbackCarriedForward(0.0);
	}

	public CreditCard(Customer customer, String cardNumber, int balance, int cardLimit, CardType cardType) {
		this.customer = customer;
		this.cardNumber = cardNumber;
		this.balance = balance;
		this.cardLimit = cardLimit;
		this.cardType = cardType;
		this.cashback = 0.0;
		this.monthlySpend = 0.0;
		this.isActive = true;
		setCashbackCarriedForward(0.0);
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
		
		this.balance = new BigDecimal(balance).setScale(2, RoundingMode.HALF_UP).doubleValue();
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
	
	public double getCashback() {
		return cashback;
	}

	public void setCashback(double cashback) {
		this.cashback = new BigDecimal(cashback).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}
	
	public double getMonthlySpend() {
		return monthlySpend;
	}

	public void setMonthlySpend(double monthlySpend) {
		this.monthlySpend = new BigDecimal(monthlySpend).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	public List<InstallmentPayment> getInstallmentPayments() {
		return installmentPayments;
	}

	public void setInstallmentPayments(List<InstallmentPayment> installmentPayments) {
		this.installmentPayments = installmentPayments;
	}

	public String getFormattedCreditCardNumber() {
		if (cardNumber.length() > 12) {
			return (this.getCardNumber().substring(0, 4) + "-" + this.getCardNumber().substring(4, 8) + "-"
					+ this.getCardNumber().substring(8, 12) + "-" + this.getCardNumber().substring(12, 16));
		} else {
			return cardNumber;
		}

	}

	public String getMaskedCreditCardNumber() {
		if (cardNumber.length() > 12) {
			return ("****-****-****-" + this.getCardNumber().substring(12, 16));
		} else {
			return cardNumber;
		}
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public double getCashbackCarriedForward() {
		return cashbackCarriedForward;
	}

	public void setCashbackCarriedForward(double cashbackCarriedForward) {
		this.cashbackCarriedForward = new BigDecimal(cashbackCarriedForward).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

}
