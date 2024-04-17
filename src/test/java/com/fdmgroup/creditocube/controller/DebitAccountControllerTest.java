package com.fdmgroup.creditocube.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;
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
import com.fdmgroup.creditocube.model.DebitAccount;
import com.fdmgroup.creditocube.service.CustomerService;
import com.fdmgroup.creditocube.service.DebitAccountService;

@SpringBootTest
public class DebitAccountControllerTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	// Mock the services used in CustomerController
	@MockBean
	private DebitAccountService debitAccountService;

	@MockBean
	private CustomerService customerService;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Test
	public void testGoToAccountDashboard_ShouldReturnAccountDashboardView() throws Exception {
		when(customerService.findCustomerByUsername("testUser")).thenReturn(Optional.of(new Customer()));

		mockMvc.perform(get("/account-dashboard").with(user("testUser"))).andExpect(status().isOk())
				.andExpect(view().name("account-dashboard"))
				.andExpect(request().sessionAttribute("customer", notNullValue()))
				.andExpect(request().sessionAttribute("accounts", notNullValue()));
	}

	@Test
	public void testGoToAccountDashboard_ShouldReturnLoginView_NoCustomerWithUsername() throws Exception {
		when(customerService.findCustomerByUsername("testUser")).thenReturn(Optional.empty());

		mockMvc.perform(get("/account-dashboard").with(user("testUser"))).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	public void testGoToAccountDashboard_WhenCustomerFound_ShouldReturnAccountDashboardViewWithEmptyAccountsList()
			throws Exception {
		// Mocking the behavior of customerService to return a non-empty Optional
		Customer sessionCustomer = new Customer(); // Assuming a customer object
		Optional<Customer> optionalCustomer = Optional.of(sessionCustomer);
		when(customerService.findCustomerByUsername("testUser")).thenReturn(optionalCustomer);

		// Mocking the behavior of debitAccountService to return an empty list of
		// accounts
		List<DebitAccount> emptyAccountsList = Collections.emptyList();
		when(debitAccountService.findAllDebitAccountsForCustomer(sessionCustomer)).thenReturn(emptyAccountsList);

		// Performing the request with a user named "testUser"
		mockMvc.perform(get("/account-dashboard").with(user("testUser"))).andExpect(status().isOk())
				.andExpect(view().name("account-dashboard"))
				.andExpect(request().sessionAttribute("customer", sessionCustomer))
				.andExpect(request().sessionAttribute("accounts", emptyAccountsList));
	}

}
