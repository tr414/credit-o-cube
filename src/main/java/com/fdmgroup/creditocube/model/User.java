package com.fdmgroup.creditocube.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Represents an abstract User entity to be extended by specific user types.
 * This class provides a common structure for user entities including basic user
 * information.
 */
@MappedSuperclass
public abstract class User {

	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int user_id;
	private String email;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(nullable = false, unique = true)
	private String username;
	@Column(nullable = false)
	private String password;

	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;

	public User() {

	}

	/**
	 * Gets the user's ID.
	 * 
	 * @return The ID of the user.
	 */
	public int getUser_id() {
		return user_id;
	}

	/**
	 * Sets the user's ID.
	 * 
	 * @param user_id The new ID of the user.
	 */
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	/**
	 * Gets the user's email address.
	 * 
	 * @return The email address of the user.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the user's email address.
	 * 
	 * @param email The new email address of the user.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the user's phone number.
	 * 
	 * @return The phone number of the user.
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Sets the user's phone number.
	 * 
	 * @param phoneNumber The new phone number of the user.
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Gets the user's username.
	 * 
	 * @return The username of the user.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the user's username.
	 * 
	 * @param username The new username of the user.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the user's password.
	 * 
	 * @return The password of the user.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the user's password.
	 * 
	 * @param password The new password of the user.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the user's first name.
	 * 
	 * @return The first name of the user.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the user's first name.
	 * 
	 * @param firstName The new first name of the user.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets the user's last name.
	 * 
	 * @return The last name of the user.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the user's last name.
	 * 
	 * @param lastName The new last name of the user.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}