package com.fdmgroup.creditocube.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

@Entity
public class CardType {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long card_type_ID;

	private String name;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "reward_id")
	private List<Rewards> rewards;

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

	public CardType() {

	}

	public CardType(String name, List<Rewards> rewards) {
		this.name = name;
		this.rewards = rewards;
	}

	public List<Rewards> getRewards() {
		return rewards;
	}

	public void setRewards(List<Rewards> rewards) {
		this.rewards = rewards;
	}

}
