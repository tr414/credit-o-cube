package com.fdmgroup.creditocube.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.repository.CustomerRepository;

@Repository
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepo;

	/**
	 * The password encoder for encoding passwords. Used to hash passwords before
	 * they are stored in the database, enhancing security.
	 */
	@Autowired
	private PasswordEncoder passwordEncoder;

	// Persists a new Customer, with attributes already set before persisting
	public Optional<Customer> createCustomer(Customer customer) {
		if (customer != null) {
			customerRepo.save(customer);
			return Optional.of(customer);
		} else {
			System.out.println("Error: Customer not saved");
			return Optional.empty();
		}
	}

	public Optional<Customer> findCustomerById(int id) {
		return customerRepo.findById(id);
	}

	public ArrayList<Customer> findCustomerByFirstName(String customerFirstName) {

		return customerRepo.findCustomerByFirstName(customerFirstName);
	}

	public ArrayList<Customer> findCustomerByLastName(String customerLastName) {

		return customerRepo.findCustomerByLastName(customerLastName);
	}

	public void deleteCustomer(int id) {

		customerRepo.deleteById(id);
	}

	public ArrayList<Customer> getAllCustomers() {

		return (ArrayList<Customer>) customerRepo.findAll();
	}

	// uses the custom query that was defined in the repo class
	public Optional<Customer> findCustomerByUsername(String username) {
		return (Optional<Customer>) customerRepo.findCustomerByUsername(username);
	}

	/**
	 * Registers a new user with the provided username and password. The password is
	 * encoded before saving to ensure the security of user data.
	 *
	 * @param username    the username for the new user, must be unique and not null
	 * @param rawPassword the password for the new user before encoding
	 * @return the newly created user, now stored in the database with an encoded
	 *         password
	 */
	public Customer registerNewCustomer(String username, String rawPassword) {
		System.out.println(username + "/password: " + rawPassword);
		Customer customer = new Customer();
		customer.setUsername(username);
		customer.setPassword(passwordEncoder.encode(rawPassword)); // Encrypts the password before saving
		return customerRepo.save(customer);
	}
}
