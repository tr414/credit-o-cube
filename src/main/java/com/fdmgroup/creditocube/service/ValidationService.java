package com.fdmgroup.creditocube.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

	@Autowired
	private CustomerService customerService;

	public Optional<String> isValidName(String name) {
		if (!name.matches("[A-Za-z]+[A-Za-z ]*")) {
			return Optional.of("Name should only contain letters and spaces, and must start with a letter.");
		}
		return Optional.empty();
	}

	public Optional<String> isValidUsername(String username) {
		if (!username.matches("^[a-zA-Z0-9_\\-]{6,}$")) {
			return Optional.of(
					"Username must contain at least 6 characters and consist of only letters, numbers, '_', and '-'.");
		}

		if (customerService.existsByUsername(username)) {
			return Optional.of("Username already exists.");
		}
		return Optional.empty();
	}

	public Optional<String> isValidNRIC(String nric) {
		if (nric == null || !nric.matches("^[STFG]\\d{7}[A-Z]$")) {
			return Optional.of("NRIC format is invalid.");
		}

		if (customerService.existsByNRIC(nric)) {
			return Optional.of("NRIC already exists.");
		}
		return Optional.empty();
	}

	public Optional<String> isValidDOB(LocalDate dob) {
		if (dob == null) {
			return Optional.of("Date of birth cannot be null.");
		}

		LocalDate minDOB = LocalDate.of(1900, 1, 1);
		if (dob.isBefore(minDOB)) {
			return Optional.of("Date of birth should be after 1900-01-01.");
		}

		LocalDate today = LocalDate.now();
		LocalDate eighteenYearsAgo = today.minusYears(18);
		if (dob.isAfter(eighteenYearsAgo)) {
			return Optional.of("Date of birth should be at least 18 years ago.");
		}
		return Optional.empty();
	}

	public Optional<String> isPasswordComplex(String password) {
		if (password.length() < 8) {
			return Optional.of("Password must be at least 8 characters long.");
		}

		if (!password.matches(".*[A-Z].*")) {
			return Optional.of("Password must contain at least one uppercase letter.");
		}

		if (!password.matches(".*[a-z].*")) {
			return Optional.of("Password must contain at least one lowercase letter.");
		}

		if (!password.matches(".*\\d.*")) {
			return Optional.of("Password must contain at least one digit.");
		}

		if (!password.matches(".*[!@#$%^&*()].*")) {
			return Optional.of("Password must contain at least one special character.");
		}
		return Optional.empty();
	}

	public Optional<String> isSamePassword(String password, String confirmPassword) {
		if (!password.equals(confirmPassword)) {
			return Optional.of("Passwords do not match");
		}
		return Optional.empty();
	}

	public Optional<String> isMobileNumber(String phoneNumber) {
		String regex = "^[89]\\d{7}$";

		if (phoneNumber != null && phoneNumber.matches(regex)) {
			return Optional.empty();
		} else {
			return Optional.of("Enter a valid Singapore mobile number");
		}
	}

	public Optional<String> isValidSalary(double salary) {
		try {
			double salaryValue = salary;

			if (salaryValue < 0) {
				return Optional.of("Salary cannot be negative.");
			}
			return Optional.empty();
		} catch (NumberFormatException e) {
			return Optional.of("Salary must be a valid number.");
		}
	}
}