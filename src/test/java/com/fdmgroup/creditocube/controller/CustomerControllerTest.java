package com.fdmgroup.creditocube.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fdmgroup.creditocube.service.CustomerService;
import com.fdmgroup.creditocube.service.DebitAccountService;
import com.fdmgroup.creditocube.service.UserService;

@SpringBootTest
public class CustomerControllerTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	// Mock the services used in CustomerController
	@MockBean
	private UserService userService;

	@MockBean
	private DebitAccountService debitAccountService;

	@MockBean
	private CustomerService customerService;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Test
	public void testSlashLogin_ShouldReturnLoginView() throws Exception {
		mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
	}
}