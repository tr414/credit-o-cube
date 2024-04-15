package com.fdmgroup.creditocube.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.User;

import com.fdmgroup.creditocube.service.CustomerService;
import com.fdmgroup.creditocube.service.DebitAccountService;
import com.fdmgroup.creditocube.service.UserService;

import jakarta.servlet.http.HttpSession;

/*
 * This controller handles requests for 
 * - Landing page
 * - Login page for Customer
 * - Registering for a user account for the customer
 * 
 */
@Controller
public class MainWebController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	DebitAccountService debitAccountService;
	
	@Autowired
	CustomerService customerService;

	
	@GetMapping("/") // http://localhost:8080/
	public String slashIndex() {
		return ("index");
		// return("something");
	}
	

	

	
	
    

	

}
