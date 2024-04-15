package com.fdmgroup.creditocube.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fdmgroup.creditocube.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{
	
	@Query("SELECT c FROM Customer c WHERE c.firstName LIKE :customerFirstName")
	public ArrayList<Customer> findCustomerByFirstName(@Param("customerFirstName")String customerFirstName);
	@Query("SELECT c FROM Customer c WHERE c.firstName LIKE :customerLastName")
	public ArrayList<Customer> findCustomerByLastName(@Param("customerLastName")String customerLastName);
	@Query("SELECT c FROM Customer c WHERE c.username LIKE :username")
	public Optional<Customer> findCustomerByUsername(@Param("username") String username);

}
