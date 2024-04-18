package com.fdmgroup.creditocube.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class CardType {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long card_type_ID;
	
	private String name;
	
	@OneToOne(mappedBy = "cardType")
	private CreditCard creditCard;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_reward_id")
	private Rewards rewards;

	public long getCard_type_ID() {
		return card_type_ID;
	}

	public void setCard_type_ID(long card_type_ID) {
		this.card_type_ID = card_type_ID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public CardType() {
		
	}

	public CardType(String name, CreditCard creditCard, Rewards rewards) {
		
		this.name = name;
		this.creditCard = creditCard;
		this.rewards = rewards;
	}

	public Rewards getRewards() {
		return rewards;
	}

	public void setRewards(Rewards rewards) {
		this.rewards = rewards;
	}

	
	
	

}
