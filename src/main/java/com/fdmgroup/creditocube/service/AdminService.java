package com.fdmgroup.creditocube.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.Admin;
import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.User;
import com.fdmgroup.creditocube.repository.AdminRepository;

@Service
public class AdminService {
	
	@Autowired
	private AdminRepository adminRepo;

	
	
	public Optional<Admin> createAdmin(Admin admin) {
		if (admin != null) {
			adminRepo.save(admin);
			return Optional.of(admin);
		} else {
			System.out.println("Error: Customer not saved");
			return Optional.empty();
		}
	}
	
	public Optional<User> findUserByUsername(String username) {
		return adminRepo.findByUsername(username);
	}

}
