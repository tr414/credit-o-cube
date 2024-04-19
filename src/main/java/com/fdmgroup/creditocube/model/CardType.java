package com.fdmgroup.creditocube.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class CardType {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long card_type_ID;
	
	private String name;
	
	@OneToMany(mappedBy = "cardType")
	private List<CreditCard> creditCards;
	
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

	public List<CreditCard> getCreditCards() {
		return creditCards;
	}

	public void setCreditCards(List<CreditCard> creditCards) {
		this.creditCards = creditCards;
	}

	public CardType() {
		
	}
	
	public CardType(String name, Rewards rewards) {
		this.name = name;
		this.rewards = rewards;
	}

	public CardType(String name, List<CreditCard> creditCards, Rewards rewards) {
		
		this.name = name;
		this.creditCards = creditCards;
		this.rewards = rewards;
	}

	public Rewards getRewards() {
		return rewards;
	}

	public void setRewards(Rewards rewards) {
		this.rewards = rewards;
	}

	
	
	

}
