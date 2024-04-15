package com.fdmgroup.creditocube.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.repository.DebitAccountRepository;

@Service
public class DebitAccountService {

	@Autowired
	private DebitAccountRepository debitAccountRepository;
}
