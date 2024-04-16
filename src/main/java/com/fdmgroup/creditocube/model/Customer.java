package com.fdmgroup.creditocube.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

/**
 * Represents a Customer entity inheriting from User, with additional properties
 * such as a list of debit accounts, NRIC, address, and salary.
 */
@Entity
public class Customer extends User {

    @OneToMany(mappedBy = "customer")
    private List<DebitAccount> debitAccounts;

    private String nric;
    private String address;
    private double salary;
    private String gender;
    
    public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
     * Default constructor for JPA use.
     */
    // Constructors
    public Customer() {}
 
    /**
     * Constructs a new User with the specified username and password.
     *
     * @param username the username for the new user
     * @param password the password for the new user
     */
    public Customer(String username, String password, List<DebitAccount> debitAccounts, String nric, String address, double salary) {
		super();
        setUsername(username);
        setPassword(password);
		this.debitAccounts = debitAccounts;
		this.nric = nric;
		this.address = address;
		this.salary = salary;
	}

	/**
     * Gets the NRIC of the customer.
     * 
     * @return A string representing the customer's NRIC.
     */
    public String getNric() {
        return nric;
    }

    /**
     * Sets the NRIC of the customer.
     * 
     * @param nric The new NRIC to be set for the customer.
     */
    public void setNric(String nric) {
        this.nric = nric;
    }

    /**
     * Gets the address of the customer.
     * 
     * @return A string representing the customer's address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the customer.
     * 
     * @param address The new address to be set for the customer.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the salary of the customer.
     * 
     * @return A double value representing the customer's salary.
     */
    public double getSalary() {
        return salary;
    }

    /**
     * Sets the salary of the customer.
     * 
     * @param salary The new salary to be set for the customer.
     */
    public void setSalary(double salary) {
        this.salary = salary;
    }

    /**
     * Gets the list of debit accounts associated with the customer.
     * 
     * @return A list of DebitAccount objects associated with the customer.
     */
    public List<DebitAccount> getDebitAccounts() {
        return debitAccounts;
    }

    /**
     * Sets the list of debit accounts for the customer.
     * 
     * @param debitAccounts The new list of DebitAccount objects to be associated with the customer.
     */
    public void setDebitAccounts(List<DebitAccount> debitAccounts) {
        this.debitAccounts = debitAccounts;
    }

}