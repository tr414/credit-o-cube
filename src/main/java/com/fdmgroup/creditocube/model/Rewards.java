package com.fdmgroup.creditocube.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/*
 * Each card type can have multiple rewards, but we can't see the card types for each reward
 */
@Entity
public class Rewards {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long reward_id;

	private double cashback_rate;

	private String category;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_card_type_id")
	private CardType cardType;

	public double getCashback_rate() {
		return cashback_rate;
	}

	public void setCashback_rate(double cashback_rate) {
		this.cashback_rate = cashback_rate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public long getReward_id() {
		return reward_id;
	}

	public void setReward_id(long reward_id) {
		this.reward_id = reward_id;
	}

	public Rewards() {
		super();
	}

	public Rewards(double cashback_rate, String category, CardType cardType) {
		super();
		this.cashback_rate = cashback_rate;
		this.category = category;
		this.cardType = cardType;
	}

	public CardType getCardType() {
		return cardType;
	}

	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}
}
