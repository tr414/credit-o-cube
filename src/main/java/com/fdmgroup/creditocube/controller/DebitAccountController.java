package com.fdmgroup.creditocube.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.model.User;
import com.fdmgroup.creditocube.service.DebitAccountService;
import com.fdmgroup.creditocube.service.UserService;

@Controller
public class DebitAccountController {

	@Autowired
	private DebitAccountService debitAccountService;

	@Autowired
	private UserService userService;

	public String createDebitAccount(@SessionAttribute Customer customer) {

		Optional<User> optionalCustomer = userService.findUserById(customer.getUser_id());

		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}

		Customer sessionCustomer = (Customer) optionalCustomer.get();

		DebitAccount newAccount = new DebitAccount(sessionCustomer);

		List<DebitAccount> accountList = sessionCustomer.getDebitAccounts();
		accountList.add(newAccount);
		sessionCustomer.setDebitAccounts(accountList);

		return "redirect:/dashboard";
	}

	public String closeDebitAccount(@SessionAttribute Customer customer, @SessionAttribute DebitAccount account) {

		Optional<User> optionalCustomer = userService.findUserById(customer.getUser_id());

		if (optionalCustomer.isEmpty()) {
			return "redirect:/login";
		}

		Customer sessionCustomer = (Customer) optionalCustomer.get();

		Optional<DebitAccount> optionalDebitAccount = debitAccountService
				.findDebitAccountByAccountNumber(account.getAccountNumber());
		if (optionalDebitAccount.isEmpty()) {
			return "redirect:/login";
		}

		DebitAccount sessionDebitAccount = optionalDebitAccount.get();

		List<DebitAccount> accountList = sessionCustomer.getDebitAccounts();
		accountList.remove(sessionDebitAccount);
		sessionCustomer.setDebitAccounts(accountList);

		return "redirect:/dashboard";
	}

}
