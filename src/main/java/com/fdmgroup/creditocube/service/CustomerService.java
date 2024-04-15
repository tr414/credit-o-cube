package com.fdmgroup.creditocube.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.repository.CustomerRepository;


@Repository
public class CustomerService {
	
	@Autowired
	private CustomerRepository customerRepo;

	// Persists a new Customer, with attributes already set before persisting 
    public Optional<Customer> createCustomer (Customer customer){
    	if (customer != null) {
    		customerRepo.save(customer);
    		return Optional.of(customer);
    	}
    	else {
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
	public ArrayList<Customer> findCustomerByUsername(String username){
		return customerRepo.findCustomerByUsername(username);
	}
}
