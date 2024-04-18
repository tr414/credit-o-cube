package com.fdmgroup.creditocube.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Represents a transaction between two debit accounts.
 *
 * @author timothy.chai
 */
@Entity
public class DebitAccountTransaction {

	/**
	 * The unique identifier for the transaction.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "debit_account_transaction_id")
	private long debitAccountTransactionId;

	/**
	 * The type of the transaction.
	 */
	@Column(name = "debit_account_transaction_type")
	private String debitAccountTransactionType;

	/**
	 * The debit account from which the transaction originates.
	 */
	@ManyToOne
	@JoinColumn(name = "from_accountId")
	private DebitAccount fromAccount;

	/**
	 * The debit account to which the transaction is directed.
	 */
	@ManyToOne
	@JoinColumn(name = "to_accountId")
	private DebitAccount toAccount;

	/**
	 * The date of the transaction.
	 */
	@Column(name = "debit_account_transaction_date")
	private Date debitAccountTransactionDate;

	/**
	 * The amount of the transaction.
	 */
	@Column(name = "amount")
	private double debitAccountTransactionAmount;

	/**
	 * Default constructor that initializes the transaction date.
	 */
	public DebitAccountTransaction() {
		this.debitAccountTransactionDate = new Date();
	}

	/**
	 * Constructor that initializes the transaction with the specified parameters.
	 *
	 * @param fromAccount     the debit account from which the transaction
	 *                        originates
	 * @param toAccount       the debit account to which the transaction is directed
	 * @param amount          the amount of the transaction
	 * @param transactionType the type of the transaction
	 */
	public DebitAccountTransaction(DebitAccount fromAccount, DebitAccount toAccount,
			double debitAccountTransactionAmount, String debitAccountTransactionType) {
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.debitAccountTransactionAmount = debitAccountTransactionAmount;
		this.debitAccountTransactionType = debitAccountTransactionType;
	}

	// Getters and setters for the fields

	/**
	 * Get the unique identifier for the transaction.
	 *
	 * @return the unique identifier for the transaction
	 */
	public Long getDebitAccountTransactionId() {
		return debitAccountTransactionId;
	}

	/**
	 * Set the unique identifier for the transaction.
	 *
	 * @param debitAccountTransactionId the unique identifier for the transaction
	 */
	public void setDebitAccountTransactionId(long debitAccountTransactionId) {
		this.debitAccountTransactionId = debitAccountTransactionId;
	}

	/**
	 * Get the type of the transaction.
	 *
	 * @return the type of the transaction
	 */
	public String getDebitAccountTransactionType() {
		return debitAccountTransactionType;
	}

	/**
	 * Set the type of the transaction.
	 *
	 * @param debitAccountTransactionType the type of the transaction
	 * 
	 */
	public void setDebitAccountTransactionType(String debitAccountTransactionType) {
		List<String> availableTypes = new ArrayList<String>();
		availableTypes.add("deposit");
		availableTypes.add("withdraw");
		availableTypes.add("transfer");

		if (!availableTypes.contains(debitAccountTransactionType)) {
			return;
		} else {
			this.debitAccountTransactionType = debitAccountTransactionType;
		}
	}

	/**
	 * Get the debit account from which the transaction originates.
	 *
	 * @return the debit account from which the transaction originates
	 */
	public DebitAccount getFromAccount() {
		return fromAccount;
	}

	/**
	 * Set the debit account from which the transaction originates.
	 *
	 * @param fromAccount the debit account from which the transaction originates
	 */
	public void setFromAccount(DebitAccount fromAccount) {
		this.fromAccount = fromAccount;
	}

	/**
	 * Get the debit account to which the transaction is directed.
	 *
	 * @return the debit account to which the transaction is directed
	 */
	public DebitAccount getToAccount() {
		return toAccount;
	}

	/**
	 * Set the debit account to which the transaction is directed.
	 *
	 * @param toAccount the debit account to which the transaction is directed
	 */
	public void setToAccount(DebitAccount toAccount) {
		this.toAccount = toAccount;
	}

	/**
	 * Get the date of the transaction.
	 *
	 * @return the date of the transaction
	 */
	public Date getDebitAccountTransactionDate() {
		return debitAccountTransactionDate;
	}

	/**
	 * Set the date of the transaction.
	 *
	 * @param debitAccountTransactionDate the date of the transaction
	 */
	public void setDebitAccountTransactionDate(Date debitAccountTransactionDate) {
		this.debitAccountTransactionDate = debitAccountTransactionDate;
	}

	/**
	 * Get the amount of the transaction.
	 *
	 * @return the amount of the transaction
	 */
	public double getDebitAccountTransactionAmount() {
		return debitAccountTransactionAmount;
	}

	/**
	 * Set the amount of the transaction.
	 *
	 * @param debitAccountTransactionAmount the amount of the transaction
	 */
	public void setDebitAccountTransactionAmount(double debitAccountTransactionAmount) {
		this.debitAccountTransactionAmount = debitAccountTransactionAmount;
	}

}
