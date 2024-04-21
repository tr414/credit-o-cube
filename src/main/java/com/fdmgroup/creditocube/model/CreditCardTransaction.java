package com.fdmgroup.creditocube.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class CreditCardTransaction {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long transactionId;
	
	@ManyToOne
	@JoinColumn(name="CARD_ID_FK", nullable=false)
	private CreditCard transactionCard;
	
	@ManyToOne
	@JoinColumn(name="MERCHANT_ID_FK", nullable=false)
	private Merchant merchantCode;
	
	private double cashbackAmount;
	
	private LocalDateTime transactionDate;
	
	private double transactionAmount;
	
	private String description;
	

	public CreditCardTransaction() {
		super();
	}

	public CreditCardTransaction(CreditCard transactionCard, Merchant merchantCode, double cashbackAmount,
			LocalDateTime transactionDate, double transactionAmount, String description) {
		super();
		this.transactionCard = transactionCard;
		this.merchantCode = merchantCode;
		this.cashbackAmount = cashbackAmount;
		this.transactionDate = transactionDate;
		this.transactionAmount = transactionAmount;
		this.description = description;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public CreditCard getTransactionCard() {
		return transactionCard;
	}

	public void setTransactionCard(CreditCard transactionCard) {
		this.transactionCard = transactionCard;
	}

	public Merchant getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(Merchant merchantCode) {
		this.merchantCode = merchantCode;
	}

	public double getCashbackAmount() {
		return cashbackAmount;
	}

	public void setCashbackAmount(double cashbackAmount) {
		this.cashbackAmount = cashbackAmount;
	}

	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}

	public double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
	
	
}
