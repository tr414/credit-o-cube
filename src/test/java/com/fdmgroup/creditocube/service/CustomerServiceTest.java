package com.fdmgroup.creditocube.service;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fdmgroup.creditocube.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

	@Mock
	private CustomerRepository customerRepo;

	@InjectMocks
	private CustomerService customerService;

	@Test
	public void testDetailVerification_ShortPassword() {
		String username = "test_user";
		String rawPassword = "short"; // Less than 8 characters
		String firstName = "John";
		String lastName = "Doe";
		String email = "valid@email.com";
		String phoneNumber = "12345678";
		String nric = "S1234567A";
		String address = "123 Main St";
		Double salary = 1000.0;
		String gender = "Male";
		LocalDate dob = LocalDate.now().minusYears(20); // Valid DoB

		// Call method
//		boolean result = customerService.detailVerification(username, rawPassword, firstName, lastName, email,
//				phoneNumber, nric, address, salary, gender, dob);

		// Assert outcome
//		assertFalse(result);
		// No verification of other fields since password fails first

		// Explanation: This test simulates a scenario where the password is less than 8
		// characters,
		// which should trigger a short password message and return false.
		// We don't need to verify other fields since the method should return early on
		// password failure.
	}

	@Test
	public void testDetailVerification_InvalidEmail() {
		String username = "test_user";
		String rawPassword = "validpassword";
		String firstName = "John";
		String lastName = "Doe";
		String email = "invalidEmail"; // Missing "@" symbol
		String phoneNumber = "12345678";
		String nric = "S1234567A";
		String address = "123 Main St";
		Double salary = 1000.0;
		String gender = "Male";
		LocalDate dob = LocalDate.now().minusYears(20); // Valid DoB

		// Call method
//		boolean result = customerService.detailVerification(username, rawPassword, firstName, lastName, email,
//				phoneNumber, nric, address, salary, gender, dob);

		// Assert outcome
//		assertFalse(result);
		// No verification of other fields since email fails after password check

		// Explanation: This test simulates a scenario where the email format is invalid
		// (missing "@").
		// The method should return false after the password check and email validation.
		// We don't need to verify other fields since the method should return early on
		// email format failure.
	}

	@Test
	public void testDetailVerification_InvalidPhoneNumber() {
		String username = "test_user";
		String rawPassword = "validpassword";
		String firstName = "John";
		String lastName = "Doe";
		String email = "valid@email.com";
		String phoneNumber = "invalid123"; // Contains characters other than digits
		String nric = "S1234567A";
		String address = "123 Main St";
		Double salary = 1000.0;
		String gender = "Male";
		LocalDate dob = LocalDate.now().minusYears(20); // Valid DoB

		// Call method
//		boolean result = customerService.detailVerification(username, rawPassword, firstName, lastName, email,
//				phoneNumber, nric, address, salary, gender, dob);

		// Assert outcome
//		assertFalse(result);
		// No verification of other fields since phone number fails after password and
		// email checks

		// Explanation: This test simulates a scenario where the phone number format is
		// invalid (contains non-digits).
		// The method should return false after the password, email, and phone number
		// checks.
		// We don't need to verify other fields since the method should return early on
		// phone number format failure.
	}

	@Test
	public void testDetailVerification_InvalidNRIC() {
		String username = "test_user";
		String rawPassword = "validpassword";
		String firstName = "John";
		String lastName = "Doe";
		String email = "valid@email.com";
		String phoneNumber = "12345678";
		String nric = "invalidNRIC"; // Invalid format
		String address = "123 Main St";
		Double salary = 1000.0;
		String gender = "Male";
		LocalDate dob = LocalDate.now().minusYears(20); // Valid DoB

		// Call method
//		boolean result = customerService.detailVerification(username, rawPassword, firstName, lastName, email,
//				phoneNumber, nric, address, salary, gender, dob);

		// Assert outcome
//		assertFalse(result);

	}

	@Test
	public void testDetailVerification_InvalidSalary() {
		String username = "test_user";
		String rawPassword = "validpassword";
		String firstName = "John";
		String lastName = "Doe";
		String email = "valid@email.com";
		String phoneNumber = "12345678";
		String nric = "S1234567A";
		String address = "123 Main St";
		Double salary = -1000.0; // invalid salary
		String gender = "Male";
		LocalDate dob = LocalDate.now().minusYears(20); // Valid DoB

		// Call method
//		boolean result = customerService.detailVerification(username, rawPassword, firstName, lastName, email,
//				phoneNumber, nric, address, salary, gender, dob);

		// Assert outcome
//		assertFalse(result);
	}

	@Test
	public void testDetailVerification_YoungAge() {
		String username = "test_user";
		String rawPassword = "validpassword";
		String firstName = "John";
		String lastName = "Doe";
		String email = "valid@email.com";
		String phoneNumber = "12345678";
		String nric = "S1234567A";
		String address = "123 Main St";
		Double salary = 1000.0;
		String gender = "Male";
		LocalDate dob = LocalDate.now().minusYears(17); // invalid DoB

		// Call method
//		boolean result = customerService.detailVerification(username, rawPassword, firstName, lastName, email,
//				phoneNumber, nric, address, salary, gender, dob);

		// Assert outcome
//		assertFalse(result);
	}
}
