package com.fdmgroup.creditocube.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.CreditCardTransaction;
import com.fdmgroup.creditocube.service.CreditCardService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BillController {
	
	
	@Autowired
	CreditCardService cardService;

}
