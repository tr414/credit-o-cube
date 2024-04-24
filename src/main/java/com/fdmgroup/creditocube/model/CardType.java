package com.fdmgroup.creditocube.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;


@Entity
public class CardType {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long card_type_ID;

	@Column(unique = true) // Ensures that name is unique
	private String name;
	
	private String imageUrl; // URL to an image for the card type
    private String requirements; // Requirements for signing up

	@OneToMany(mappedBy="cardType", cascade = CascadeType.ALL)
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

	public CardType(String name, String imageUrl, String requirements, List<Rewards> rewards) {
		
		this.name = name;
		this.imageUrl = imageUrl;
		this.requirements = requirements;
		this.rewards = rewards;
	}

	public List<Rewards> getRewards() {
		return rewards;
	}

	public void setRewards(List<Rewards> rewards) {
		this.rewards = rewards;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getRequirements() {
		return requirements;
	}

	public void setRequirements(String requirements) {
		this.requirements = requirements;
	}
	

	
	

}
