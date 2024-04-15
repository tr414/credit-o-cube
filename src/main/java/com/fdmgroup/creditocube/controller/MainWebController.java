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
	
	
	// check if they cna login as a user, not just a customer
	private boolean checkValidUsernameAndPassword(String username, String password) {
		// check if username exists in database
		
		// if username exists
		if (!customerService.findCustomerByUsername(username).isEmpty()) {
			User user = customerService.findCustomerByUsername(username).get(0);
			if (user.getPassword().equals(password)){
				return true;
			}
		}
		return false;
	}
	
	@GetMapping("/") // http://localhost:8080/
	public String slashIndex() {
		return ("index");
		// return("something");
	}
	
	@GetMapping("/login") // http://localhost:8080/login
	public String slashLogin() {
		return ("login");
	}
	
    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username, 
                               @RequestParam("password") String password, 
                               HttpSession session, Model model) {
        // if username exists and password corresponds to correct password
        if (checkValidUsernameAndPassword(username, password)) {
        	
            // Authentication successful; set user session
        	System.out.println("Authentication was successful!");
            session.setAttribute("currentUser", username);
            return "customerDashboard";
        } else {
            // Authentication failed
        	model.addAttribute("loginFailed", true);
            return "login";
        }
    }

	

}
