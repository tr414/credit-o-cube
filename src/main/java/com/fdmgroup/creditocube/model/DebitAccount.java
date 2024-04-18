/**
 * DebitAccount class represents a debit account in the CreditCube system.
 * It has an account number, account name, account balance, and a reference to the customer it belongs to.
 *
 * @author timothy.chai
 */
package com.fdmgroup.creditocube.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Represents a debit account in the CreditCube system.
 *
 * @author timothy.chai
 */
@Entity
public class DebitAccount {

	/**
	 * The primary key of the debit account.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "account_id")
	private long accountId;

	@Column(name = "account_number")
	private String accountNumber;

	/**
	 * The name of the debit account.
	 */
	@Column(name = "account_name")
	private String accountName;

	/**
	 * The current balance of the debit account.
	 */
	@Column(name = "account_balance")
	private double accountBalance;

	/**
	 * The customer to whom this debit account belongs.
	 */
	@ManyToOne
	@JoinColumn(name = "fk_user_id")
	private Customer customer;

	public DebitAccount() {

	}

	/**
	 * Constructs a new debit account with the given customer.
	 *
	 * @param customer the customer to whom this debit account belongs
	 */
	public DebitAccount(Customer customer) {
		this.customer = customer;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long id) {
		this.accountId = id;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * Gets the account name of this debit account.
	 *
	 * @return the account name
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * Sets the account name of this debit account.
	 *
	 * @param accountName the new account name
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	/**
	 * Gets the current balance of this debit account.
	 *
	 * @return the current balance
	 */
	public double getAccountBalance() {
		return accountBalance;
	}

	/**
	 * Sets the current balance of this debit account.
	 *
	 * @param accountBalance the new current balance
	 */
	public void setAccountBalance(double accountBalance) {
		if (accountBalance < 0) {
			System.out.println("account balance is negative");
			return;
		} else {
			this.accountBalance = accountBalance;
		}

	}

	/**
	 * Gets the customer to whom this debit account belongs.
	 *
	 * @return the customer
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * Sets the customer to whom this debit account belongs.
	 *
	 * @param customer the new customer
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}