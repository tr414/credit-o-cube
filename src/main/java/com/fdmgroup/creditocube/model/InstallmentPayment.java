package com.fdmgroup.creditocube.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class InstallmentPayment {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long installmentPaymentId;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_card_id")
	private CreditCard transactionCard;
	
	private double amountLeft;
	
	private int installmentsLeft;

	public InstallmentPayment() {
		super();
	}

	public InstallmentPayment(CreditCard card, double transactionAmount, int installmentsLeft) {
		super();
		this.transactionCard = card;
		this.amountLeft = transactionAmount;
		this.installmentsLeft = installmentsLeft;
	}

	public long getInstallmentPaymentId() {
		return installmentPaymentId;
	}

	public void setInstallmentPaymentId(long installmentPaymentId) {
		this.installmentPaymentId = installmentPaymentId;
	}

	public CreditCard getCard() {
		return transactionCard;
	}

	public void setCard(CreditCard card) {
		this.transactionCard = card;
	}

	public double getAmountLeft() {
		return amountLeft;
	}

	public void setAmountLeft(double transactionAmount) {
		this.amountLeft = transactionAmount;
	}

	public int getInstallmentsLeft() {
		return installmentsLeft;
	}

	public void setInstallmentsLeft(int installmentsLeft) {
		this.installmentsLeft = installmentsLeft;
	}
	

}
