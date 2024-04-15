package com.fdmgroup.creditocube.service;


import java.util.Collections;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.Customer;
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
    
    @Autowired
    private CustomerRepository customerRepo;
 
    /**
     * Loads the user's details from the database based on the username.
     *
     * @param username the username identifying the user to be loaded
     * @return a UserDetails object containing the user's data
     * @throws UsernameNotFoundException if no user could be found for the given username
     */


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepo.findCustomerByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));
 
        // Fully qualify the Spring Security User class
        // Return a Spring Security UserDetails object that includes the user's username, password,
        // and granted authorities. Here we are assuming that every user has the authority 'USER'.
        return new org.springframework.security.core.userdetails.User(
        		customer.getUsername(),
        		customer.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("USER"))); // Assume every user has USER authority
    
	}
}