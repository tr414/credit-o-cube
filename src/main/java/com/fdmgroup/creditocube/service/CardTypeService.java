package com.fdmgroup.creditocube.service;

import java.util.List;
import java.util.Optional;

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

	public Optional<CardType> findCardTypeByName(String name) {
		return cardTypeRepository.findByCardTypeByName(name);

	}

	public List<CardType> findAllCardTypes() {
		// TODO Auto-generated method stub
		return cardTypeRepository.findAll();
	}

	public boolean isCardTypeTableEmpty() {
		boolean isCardTypeTableEmpty = cardTypeRepository.count() == 0;
		System.out.println("Rewards table is empty: " + isCardTypeTableEmpty);
		return isCardTypeTableEmpty;
	}
	
	public CardType saveCardType(CardType cardType) {
        return cardTypeRepository.save(cardType);
    }
	
	public boolean canCreateNewCardType() {
	    long count = cardTypeRepository.count();
	    return count < 5;
	}
	
	
}
