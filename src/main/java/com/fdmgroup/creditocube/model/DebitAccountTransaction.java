package com.fdmgroup.creditocube.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class DebitAccountTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "debit_account_transaction_id")
	private long debitAccountTransactionId;

	@Column(name = "debit_account_transaction_type")
	private String debitAccountTransactionType;

	@ManyToOne
	@JoinColumn(name = "from_accountId")
	private DebitAccount fromAccount;

	@ManyToOne
	@JoinColumn(name = "to_accountId")
	private DebitAccount toAccount;

	@Column(name = "debit_account_transaction_date")
	private Date debitAccountTransactionDate;

	@Column(name = "amount")
	private double debitAccountTransactionAmount;

	public DebitAccountTransaction() {
		this.debitAccountTransactionDate = new Date();
	}

	public DebitAccountTransaction(DebitAccount fromAccount, DebitAccount toAccount,
			double debitAccountTransactionAmount, String debitAccountTransaction) {
		this();
		setFromAccount(fromAccount);
		setToAccount(toAccount);
		setDebitAccountTransactionAmount(debitAccountTransactionAmount);
		setDebitAccountTransactionType(debitAccountTransaction);
	}

	public Long getDebitAccountTransactionId() {
		return debitAccountTransactionId;
	}

	public void setDebitAccountTransactionId(long debitAccountTransactionId) {
		this.debitAccountTransactionId = debitAccountTransactionId;
	}

	public String getDebitAccountTransactionType() {
		return debitAccountTransactionType;
	}

	public void setDebitAccountTransactionType(String debitAccountTransactionType) {
		this.debitAccountTransactionType = debitAccountTransactionType;
	}

	public DebitAccount getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(DebitAccount fromAccount) {
		this.fromAccount = fromAccount;
	}

	public DebitAccount getToAccount() {
		return toAccount;
	}

	public void setToAccount(DebitAccount toAccount) {
		this.toAccount = toAccount;
	}

	public Date getDebitAccountTransactionDate() {
		return debitAccountTransactionDate;
	}

	public void setDebitAccountTransactionDate(Date debitAccountTransactionDate) {
		this.debitAccountTransactionDate = debitAccountTransactionDate;
	}

	public double getDebitAccountTransactionAmount() {
		return debitAccountTransactionAmount;
	}

	public void setDebitAccountTransactionAmount(double debitAccountTransactionAmount) {
		this.debitAccountTransactionAmount = debitAccountTransactionAmount;
	}

}
