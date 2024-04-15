package com.fdmgroup.creditocube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.fdmgroup.creditocube.service.CustomerService;
import com.fdmgroup.creditocube.service.DebitAccountService;
import com.fdmgroup.creditocube.service.UserService;

/**
 * The CustomerController class handles web requests related to customer operations.
 * It utilizes services to interact with user, debit account, and customer data.
 */
@Controller
public class CustomerController {
    
    @Autowired
    UserService userService; // Service for user-related operations
    
    @Autowired
    DebitAccountService debitAccountService; // Service for debit account-related operations
    
    @Autowired
    CustomerService customerService; // Service for customer-related operations

		
    /**
     * Handles the GET request for the login page.
     * This method maps the "/login" URL to return the login view.
     * 
     * @return The name of the login view template to be rendered.
     */
    
    @GetMapping("/login") // http://localhost:8080/login
    public String slashLogin() {
        return ("login");
    }
	
    /**
     * Handles the GET request for the registration page.
     * This method maps the "/register" URL to return the registration view where users can sign up as customers.
     * 
     * @return The name of the registration view template to be rendered.
     */
    @GetMapping("/register") // http://localhost:8080/register
    public String slashRegister() {
        return ("register");
    }
    
    
    /**
     * Processes the registration form submission.
     * Registers a new user with the provided username and password and then redirects to the login page.
     *
     * @param username the username entered by the user
     * @param password the password entered by the user
     * @return a redirect URL to the login page after successful registration
     */
    @PostMapping("/register")
    public String registerUser(String username, String password) {
    	customerService.registerNewCustomer(username, password);
        return "redirect:/login"; // Redirects to the login page after successful registration
    }
    
    /**
     * Returns the home page view.
     * This method handles requests to display the home page after a user has logged in successfully.
     *
     * @return the name of the Thymeleaf template that renders the home page
     */
    @GetMapping("/home")
    public String home() {
        return "home";
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
