package com.fdmgroup.creditocube.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

	@Autowired
	private CustomerService customerService;

	public boolean isValidName(String name) {
		return name.matches("[A-Za-z]+[A-Za-z ]*");
	}

	public boolean isValidUsername(String username) {
		if (!username.matches("^[a-zA-Z0-9_\\-]{6,}$")) {
			return false;
		}
		return !customerService.existsByUsername(username);
	}

	public boolean isValidNRIC(String nric) {
		if (nric == null || !nric.matches("^[STFG]\\d{7}[A-Z]$")) {
			return false;
		}
		return !customerService.existsByNRIC(nric);
	}

	public boolean isValidDOB(LocalDate dob) {
		if (dob == null) {
			return false;
		}
		LocalDate minDOB = LocalDate.of(1900, 1, 1);
		if (dob.isBefore(minDOB)) {
			return false;
		}
		LocalDate today = LocalDate.now();
		LocalDate eighteenthBirthday = dob.plusYears(18);
		return !eighteenthBirthday.isAfter(today);
	}

	public boolean isPasswordComplex(String password) {
		return password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[a-z].*")
				&& password.matches(".*\\d.*") && password.matches(".*[!@#$%^&*()].*");
	}

	public boolean isSamePassword(String password, String confirmPassword) {
		return password.equals(confirmPassword);
	}
}