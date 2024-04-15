package com.fdmgroup.creditocube.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fdmgroup.creditocube.model.User;
import com.fdmgroup.creditocube.repository.UserRepository;

@Repository
public class UserService {

	@Autowired
	private UserRepository userRepo;

	/**
	 * Creates a new user in the database.
	 * 
	 * @param user the user object to be created
	 * @return the created user object, or an empty optional if the user could not
	 *         be created
	 */
	public Optional<User> createUser(User user) {
		if (user != null) {
			userRepo.save(user);
			return Optional.of(user);
		} else {
			System.out.println("Error: User not saved");
			return Optional.empty();
		}
	}

	/**
	 * Returns the user with the given ID, if it exists.
	 * 
	 * @param id the ID of the user to be retrieved
	 * @return the user with the given ID, if it exists
	 */
	public Optional<User> findUserById(int id) {
		return userRepo.findById(id);
	}

	/**
	 * Deletes a user from the database based on the given user ID.
	 * 
	 * @param id the ID of the user to be deleted
	 */
	public void deleteUser(int id) {
		userRepo.deleteById(id);
	}

	/**
	 * Returns a list of all users in the database.
	 * 
	 * @return a list of all users in the database
	 */
	public List<User> getAllUsers() {

		return userRepo.findAll();
	}
}
