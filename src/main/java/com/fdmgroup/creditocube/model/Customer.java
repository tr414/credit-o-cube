package com.fdmgroup.creditocube.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.model.User;


@Entity
public class Customer extends User {


	@Column(name = "account_number")
	@OneToMany(mappedBy = "customer")
	private List<DebitAccount> debitAccounts;

	private String nric;
	private String address;
	private double Salary;


	public String getNric() {
		return nric;
	}

	public void setNric(String nric) {
		this.nric = nric;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getSalary() {
		return Salary;
	}

	public void setSalary(double salary) {
		Salary = salary;
	}

	public List<DebitAccount> getDebitAccounts() {
		return debitAccounts;
	}

	public void setDebitAccounts(List<DebitAccount> debitAccounts) {
		this.debitAccounts = debitAccounts;
	}

}
