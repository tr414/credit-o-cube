package com.fdmgroup.creditocube.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.fdmgroup.creditocube.model.Admin;
import com.fdmgroup.creditocube.model.User;
import com.fdmgroup.creditocube.service.UserService;


import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AdminController {
	
	@Autowired
    private UserService userService;
	
	@Autowired
	PasswordEncoder encoder;
	
	public AdminController() {
		// TODO Auto-generated constructor stub
	}
	
	@GetMapping("/create-admin")
	public String createAdminUserForm() {
		return("create-admin");
	}
	
	@PostMapping("/create-admin")
	public String createAdminUser(HttpServletRequest request, Model model) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		System.out.println(username);
		
		if ( username.equals("") || password.equals("") ) {
			model.addAttribute("error", true);
			return("create-admin");
		}
		
		User user = new Admin(username, encoder.encode(password));
		
		System.out.println("Admin with username created " + user.getUsername() + " " + user.getUserType());
		Optional<User> createdUser = userService.createUser(user);
		
		if (createdUser.isEmpty()) {
			model.addAttribute("error", true);
			return("create-admin");
		}
		
		return("redirect:/login");
	}
}
