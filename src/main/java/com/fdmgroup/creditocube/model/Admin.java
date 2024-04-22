package com.fdmgroup.creditocube.model;

import jakarta.persistence.Entity;

@Entity
public class Admin extends User {

	public Admin() {
		super();
	}
	
	public Admin(String username, String password) {
		setUsername(username);
		setPassword(password);
		setUserType("admin");
	}
	
	
}
