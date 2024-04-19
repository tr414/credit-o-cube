package com.fdmgroup.creditocube.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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

	public Rewards(double cashback_rate, String category) {
		super();
		this.cashback_rate = cashback_rate;
		this.category = category;
	}

}
