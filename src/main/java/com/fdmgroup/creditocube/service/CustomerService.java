package com.fdmgroup.creditocube.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.repository.CustomerRepository;

@Service
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

	public void deleteCustomer(Customer customer) {

		Optional<Customer> optionalCustomer = customerRepo.findById(customer.getUser_id());
		if (optionalCustomer.isEmpty()) {
			System.out.println("Customer not found in database");
			return;
		}
		Customer targetCustomer = optionalCustomer.get();
		System.out.println("Customer info obtained from database");

		if (targetCustomer.getDebitAccounts().size() > 0) {
			System.out.println("Customer has existing debit accounts - do not delete");
			return;
		} else {
			customerRepo.delete(targetCustomer);
		}

	}

	public ArrayList<Customer> getAllCustomers() {

		return (ArrayList<Customer>) customerRepo.findAll();
	}

	// uses the custom query that was defined in the repo class
	public Optional<Customer> findCustomerByUsername(String username) {
		return (Optional<Customer>) customerRepo.findCustomerByUsername(username);
	}

	public ArrayList<Customer> findCustomerByNric(String nric) {
		return customerRepo.findCustomerByNric(nric);
	}

	/**
	 * Registers a new user with the provided username and password. The password is
	 * encoded before saving to ensure the security of user data.
	 *
	 * @param username    the username for the new user, must be unique and not null
	 * @param rawPassword the password for the new user before encoding
	 * @param dob
	 * @return the newly created user, now stored in the database with an encoded
	 *         password
	 */

	public Customer registerNewCustomer(String username, String rawPassword, String firstName, String lastName,
			String nric, LocalDate dob) {

		Optional<Customer> optionalCustomer = customerRepo.findCustomerByUsername(username);

		if (optionalCustomer.isPresent()) {
			return optionalCustomer.get();
		}

		// if customer doesn't exist, create
		Customer customer = new Customer();

		customer.setUsername(username);
		customer.setPassword(passwordEncoder.encode(rawPassword)); // Encrypts the password before saving
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setNric(nric);
		customer.setDob(dob);
		return customerRepo.save(customer);

	}

	public Customer updateCustomerDetails(String username, String rawPassword, String firstName, String lastName,
			String email, String phoneNumber, String nric, String address, Double salary, String gender, LocalDate dob,
			String oldUsername) {

		Customer customer = customerRepo.findCustomerByUsername(oldUsername).get();

		boolean result = detailVerification(username, rawPassword, firstName, lastName, email, phoneNumber, nric,
				address, salary, gender, dob, oldUsername);

		if (result == false) {
			return customer;
		}

		customer.setUsername(username);
		customer.setPassword(passwordEncoder.encode(rawPassword)); // Encrypts the password before saving
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setEmail(email);
		customer.setPhoneNumber(phoneNumber);
		customer.setNric(nric);
		customer.setAddress(address);
		customer.setSalary(salary);
		customer.setGender(gender);
		customer.setDob(dob);
		System.out.println("customer saved into database");
		return customerRepo.save(customer);

	}

	public boolean detailVerification(String username, String rawPassword, String firstName, String lastName,
			String email, String phoneNumber, String nric, String address, Double salary, String gender, LocalDate dob,
			String oldUsername) {

		// Check if new password is more than or equal to 8 characters long
		if (rawPassword.length() < 8) {
			System.out.println("password is too short");
			return false;
		}

		// Check if new email address contains an '@' character
		if (!email.contains("@")) {
			System.out.println("invalid email format");
			return false;
		}

		// Check if new phone number is exactly 8 digits long
		if (!phoneNumberVerification(phoneNumber)) {
			System.out.println("phone number does not follow typical format");
			return false;
		}

		// Check if NRIC follows the correct format (start with letter, end with letter,
		// has 7 digits between)
		if (nricVerification(nric) == false) {
			System.out.println("NRIC does not follow proper format");
			return false;
		}

		// Check that salary is positive
		if (salary <= 0) {
			System.out.println("Salary is not positive");
			return false;
		}

		// Check that birth year is after 1900, and user is at least 18 years old
		if (dob.isBefore(LocalDate.now().minusYears(18)) || dob.isBefore(LocalDate.of(1900, 1, 1))) {
			return false;
		}

		return true;
	}

	public boolean detailVerificationRegistration(String username, String rawPassword, String firstName,
			String lastName, String nric, LocalDate dob) {

		if (rawPassword.length() < 8) {
			System.out.println("password is too short");
			return false;
		}

		if (nricVerification(nric) == false) {
			System.out.println("NRIC does not follow proper format");
			return false;
		}

		if (dob.isAfter(LocalDate.now().minusYears(18)) || dob.isBefore(LocalDate.of(1900, 1, 1))) {
			System.out.println("Year is ass");
			return false;
		}

		return true;
	}

	private boolean nricVerification(String nric) {
		if (nric.length() != 9) // Checking if the length is exactly 9
			return false;

		char startChar = nric.charAt(0);
		char endChar = nric.charAt(8);

		if (!Character.isLetter(startChar) || !Character.isLetter(endChar)) // Checking if start and end are letters
			return false;

		for (int i = 1; i < 8; i++) {
			if (!Character.isDigit(nric.charAt(i))) // Checking if characters from 3rd to 7th are digits
				return false;
		}

		return true;
	}

	private boolean phoneNumberVerification(String phoneNumber) {
		if (phoneNumber.length() != 8) // Checking if the length is exactly 8
			return false;
		for (int i = 0; i < 8; i++) {
			if (!Character.isDigit(phoneNumber.charAt(i))) // Checking if all characters are digits
				return false;
		}
		return true;
	}

}
