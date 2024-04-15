package com.fdmgroup.creditocube.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import com.fdmgroup.creditocube.model.Customer;


@Entity
public class DebitAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "account_number")
	private long accountNumber;

	@Column(name = "account_name")
	private String accountName;

	@Column(name = "account_balance")
	private double accountBalance;

	@ManyToOne
	@JoinColumn(name = "fk_customer_id")
	private Customer customer;

}
