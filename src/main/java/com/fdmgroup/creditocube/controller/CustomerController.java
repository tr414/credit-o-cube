package com.fdmgroup.creditocube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.fdmgroup.creditocube.service.CustomerService;
import com.fdmgroup.creditocube.service.DebitAccountService;
import com.fdmgroup.creditocube.service.UserService;

@Controller
public class CustomerController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	DebitAccountService debitAccountService;
	
	@Autowired
	CustomerService customerService;
	
	
	
	// login as a customer
	@GetMapping("/login") // http://localhost:8080/login
	public String slashLogin() {
		return ("login");
	}
	
    // register as a customer 
	@GetMapping("/register") // http://localhost:8080/login
	public String slashRegister() {
		return ("register");
	}
	
	
	
//	// login as a customer
//  @PostMapping("/login")
//  public String processLogin(@RequestParam("username") String username, 
//                             @RequestParam("password") String password, 
//                             HttpSession session, Model model) {
//      // if username exists and password corresponds to correct password
//      if (checkValidUsernameAndPassword(username, password)) {
//      	
//          // Authentication successful; set user session
//      	System.out.println("Authentication was successful!");
//          session.setAttribute("currentUser", username);
//          return "customerDashboard";
//      } else {
//          // Authentication failed
//      	model.addAttribute("loginFailed", true);
//          return "login";
//      }
//  }
  

	
	// registering as a customer
//  @PostMapping("/login")
//  public String processCustomerRegistration(
//  		@RequestParam("username") String username, 
//          @RequestParam("password") String password, 
//          @RequestParam("firstName") String firstName,
//          @RequestParam("lastName") String lastName,
//          @RequestParam("nric") String nric,
//          @RequestParam("salary") double salary,
//          HttpSession session, 
//          Model model) {
//  	
//  	// idk if spring security will check if this username and passwords are valid??
//  	
//  	// assuming it does
//  	
//  }
	
	
	
//	// check if they can login as a user, not just a customer
//	private boolean checkValidUsernameAndPassword(String username, String password) {
//		// check if username exists in database
//		
//		// if username exists
//		if (!customerService.findCustomerByUsername(username).isEmpty()) {
//			User user = customerService.findCustomerByUsername(username).get(0);
//			if (user.getPassword().equals(password)){
//				return true;
//			}
//		}
//		return false;
//	}

}
