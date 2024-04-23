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
//	@ManyToOne
//	@Column(name = "from_accountNumber")
//	private String fromAccountNumber;

	/**
	 * The debit account from which the transaction originates.
	 */
	@ManyToOne
	@JoinColumn(name = "from_accountId")
	private DebitAccount fromAccount;

	/**
	 * The debit account to which the transaction is directed.
	 */
//	@ManyToOne
	@Column(name = "to_accountNumber")
	private String toAccountNumber;

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

	public DebitAccountTransaction(DebitAccount fromAccount, String toAccountNumber,
			double debitAccountTransactionAmount, String debitAccountTransactionType) {
		this.fromAccount = fromAccount;
		this.toAccountNumber = toAccountNumber;
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

	public String getToAccountNumber() {
		return toAccountNumber;
	}

	public void setToAccountNumber(String toAccountNumber) {
		this.toAccountNumber = toAccountNumber;
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

	public String getFormattedToAccountNumber() {
		if (toAccountNumber == null || toAccountNumber.isEmpty() || toAccountNumber.isBlank()) {
			return "---";
		} else if (toAccountNumber.length() > 12) {
			return (toAccountNumber.substring(0, 4) + "-" + toAccountNumber.substring(4, 8) + "-"
					+ toAccountNumber.substring(8, 12) + "-" + toAccountNumber.substring(12, 16));
		} else {
			return (toAccountNumber.substring(0, 3) + "-" + toAccountNumber.substring(3, 6) + "-"
					+ toAccountNumber.substring(6));
		}
	}

	public String getMaskedToAccountNumber() {
		if (toAccountNumber == null || toAccountNumber.isEmpty() || toAccountNumber.isBlank()) {
			return "---";
		} else if (toAccountNumber.length() > 12) {
			return ("****-****-****-" + toAccountNumber.substring(12));
		} else {
			return ("***-***-" + toAccountNumber.substring(6));
		}
	}

	public String getFormattedFromAccountNumber() {
		if (fromAccount == null) {
			return "---";
		}

		String fromAccountNumber = fromAccount.getAccountNumber();

		if (fromAccountNumber.length() > 12) {
			return (fromAccountNumber.substring(0, 4) + "-" + fromAccountNumber.substring(4, 8) + "-"
					+ fromAccountNumber.substring(8, 12) + "-" + fromAccountNumber.substring(12, 16));
		} else {
			return (fromAccountNumber.substring(0, 3) + "-" + fromAccountNumber.substring(3, 6) + "-"
					+ fromAccountNumber.substring(6));
		}
	}

}
