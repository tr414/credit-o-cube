package com.fdmgroup.creditocube.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Customer extends User{
	
	@Id
	@Column(name = "customer_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int customerId;
	
	@OneToMany(mappedBy ="")
	private List<DebitAccount> debitAccounts;

	private String nric;
	private String address;
	private double Salary;
	
	
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

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
