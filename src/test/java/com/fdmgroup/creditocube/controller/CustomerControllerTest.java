package com.fdmgroup.creditocube.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fdmgroup.creditocube.model.Customer;
import com.fdmgroup.creditocube.model.User;
import com.fdmgroup.creditocube.service.CustomerService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import com.fdmgroup.creditocube.service.DebitAccountService;
import com.fdmgroup.creditocube.service.UserService;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
public class CustomerControllerTest {

	@Autowired
	private WebApplicationContext context;


	private MockMvc mockMvc;

	// Mock the services used in CustomerController
	@MockBean
	private UserService userService;
	
	@MockBean
	private CustomerService customerService;

	@MockBean
	private DebitAccountService debitAccountService;
	
	Customer customer;

	@BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
	
    @Test
    public void testSlashLogin_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get  ("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
    
    @Test
    public void testGetRegisterShouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }
    
    @Test
    public void testSlashIndexReturnsLandingView() throws Exception {
    	mockMvc.perform(get("/"))
	        .andExpect(status().isOk())
	        .andExpect(view().name("landing"));
    }
    
    @Test
    public void testSlashHomeRedirectsUnauthenticatedUserToLoginPage() throws Exception {
    	mockMvc.perform(get("/home"))
	        .andExpect(status().is3xxRedirection())
	        .andExpect(redirectedUrlPattern("**/login"));
    }
    
    @Test
    public void testSlashHomeReturnsAuthenticatedUserToHomePage() throws Exception {
    	mockMvc.perform(get("/home").with(user("Alex")))
	        .andExpect(status().isOk())
	        .andExpect(view().name("home"));
    }
    
    @Test
    public void testGetCustomerDetailsRedirectsUnauthenticatedUserToLoginPage() throws Exception {
    	mockMvc.perform(get("/customer-details"))
	        .andExpect(status().is3xxRedirection())
	        .andExpect(redirectedUrlPattern("**/login"));
    }
    
    @Test
    public void testGetCustomerDetailsReturnsCustomerDetailsForAuthenticatedUser() throws Exception {
    	customer = new Customer();
    	when(customerService.findCustomerByUsername(any(String.class))).thenReturn(Optional.of(customer));
    	mockMvc.perform(get("/customer-details").with(user("Alex")))
	        .andExpect(status().isOk())
	        .andExpect(view().name("customer-details"));
    }
    
    @Test
    public void testPostRegisterShouldCreateNewCustomerAccount() throws Exception {
        mockMvc.perform(post("/register")
        		.content("")
        		.with(csrf()))
		        .andExpect(status().is3xxRedirection())
		        .andExpect(redirectedUrl("/login"));        
    }

}