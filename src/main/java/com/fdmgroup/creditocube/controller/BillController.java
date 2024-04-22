package com.fdmgroup.creditocube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fdmgroup.creditocube.service.CreditCardService;

@Controller
public class BillController {

	@Autowired
	CreditCardService cardService;

}
