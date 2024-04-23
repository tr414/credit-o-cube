package com.fdmgroup.creditocube.service;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.CardType;
import com.fdmgroup.creditocube.repository.CardTypeRepository;
import com.fdmgroup.creditocube.repository.CustomerRepository;

@Service
public class CardTypeService {

	@Autowired
	private CardTypeRepository cardTypeRepository;
	@Autowired
	private CustomerRepository customerRepository;

	private static final Logger LOGGER = LogManager.getLogger(CreditCardTransactionService.class);

	public Optional<CardType> findCardTypeByName(String name) {
		return cardTypeRepository.findByCardTypeByName(name);

	}

	public List<CardType> findAllCardTypes() {
		// TODO Auto-generated method stub
		return cardTypeRepository.findAll();
	}

	public boolean isCardTypeTableEmpty() {
		boolean isCardTypeTableEmpty = cardTypeRepository.count() == 0;
		LOGGER.info("Rewards table is empty: " + isCardTypeTableEmpty);
//		System.out.println("Rewards table is empty: " + isCardTypeTableEmpty);
		return isCardTypeTableEmpty;
	}

	public CardType saveCardType(CardType cardType) {
		Optional<CardType> existingCardType = findCardTypeByName(cardType.getName());
        if (existingCardType.isPresent()) {
            LOGGER.error("Attempt to save duplicate card type name: " + cardType.getName());
            return null; // or throw a custom exception that you handle in your controller
        }
		
		return cardTypeRepository.save(cardType);
	}

	

}
