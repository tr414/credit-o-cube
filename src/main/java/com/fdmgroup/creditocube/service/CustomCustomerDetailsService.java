package com.fdmgroup.creditocube.service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.User;
import com.fdmgroup.creditocube.repository.CustomerRepository;

 
/**
* Custom implementation of the UserDetailsService interface that retrieves user details
* from the database using the UserRepository.
*
* This service is used by Spring Security during the authentication process to load details
* about a user based on the username provided in the login process.
*/
@Service
public class CustomCustomerDetailsService implements UserDetailsService {
    
//    @Autowired
//    private CustomerRepository customerRepo;
 
    
    @Autowired
    private UserService userService;
    
    /**
     * Loads the user's details from the database based on the username.
     *
     * @param username the username identifying the user to be loaded
     * @return a UserDetails object containing the user's data
     * @throws UsernameNotFoundException if no user could be found for the given username
     */


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userService.findUserByUsername(username)
					.orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));
		
		
//        Customer customer = customerRepo.findCustomerByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));
 
        // Fully qualify the Spring Security User class
        // Return a Spring Security UserDetails object that includes the user's username, password,
        // and granted authorities. Here we are assuming that every user has the authority 'USER'.
		
		if (user.getUserType().equalsIgnoreCase("customer")) {
			return new org.springframework.security.core.userdetails.User(
	        		user.getUsername(),
	        		user.getPassword(),
	                Collections.singletonList(new SimpleGrantedAuthority("USER"))); // Assume every customer has USER authority
		}
		else {
			List<SimpleGrantedAuthority> roles = new ArrayList<>();
			
			roles.add(new SimpleGrantedAuthority("USER"));
			roles.add(new SimpleGrantedAuthority("ADMIN"));
			
			return new org.springframework.security.core.userdetails.User(
	        		user.getUsername(),
	        		user.getPassword(),
	                roles); 
		}
		
        
    
	}
}