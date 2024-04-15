package com.fdmgroup.creditocube.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fdmgroup.creditocube.model.User;
import com.fdmgroup.creditocube.repository.UserRepository;

@Repository
public class UserService {
	

	@Autowired
	private UserRepository userRepo;
		
    public Optional<User> createUser (User user){
    	if (user != null) {
    		userRepo.save(user);
    		return Optional.of(user);
    	}
    	else {
    		System.out.println("Error: User not saved");
    		return Optional.empty();
    	}
    }
	
	public Optional<User> findUserById(int id) {
		return userRepo.findById(id);
	}
	

		
	public void deleteUser(int id) {

		userRepo.deleteById(id);
	}
	
	public ArrayList<User> getAllUsers() {
		
		return (ArrayList<User>) userRepo.findAll();
	}
	


}
