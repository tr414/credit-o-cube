package com.fdmgroup.creditocube.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;

@Entity
public class ForeignCurrencyCreditCardTransaction extends CreditCardTransaction {
	private String transactionCurrency;
	private double exchangeRate;
	

	public ForeignCurrencyCreditCardTransaction() {
		// TODO Auto-generated constructor stub
	}

	public ForeignCurrencyCreditCardTransaction(CreditCard transactionCard, Merchant merchantCode,
			double cashbackAmount, LocalDateTime transactionDate, double transactionAmount, String description, String transactionCurrency, double exchangeRate) {
		super(transactionCard, merchantCode, cashbackAmount, transactionDate, transactionAmount, description);
		this.transactionCurrency = transactionCurrency;
		this.exchangeRate = exchangeRate;
	}

	public ForeignCurrencyCreditCardTransaction(String transactionCurrency, double exchangeRate) {
		super();
		this.transactionCurrency = transactionCurrency;
		this.exchangeRate = exchangeRate;
	}

	public String getTransactionCurrency() {
		return transactionCurrency;
	}

	public void setTransactionCurrency(String transactionCurrency) {
		this.transactionCurrency = transactionCurrency;
	}

	public double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	
	
}
