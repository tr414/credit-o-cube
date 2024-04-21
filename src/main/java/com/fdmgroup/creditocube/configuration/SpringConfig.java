package com.fdmgroup.creditocube.configuration;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fdmgroup.creditocube.model.CardType;
import com.fdmgroup.creditocube.model.Rewards;

@Configuration
@EnableScheduling
public class SpringConfig {

	@Bean
	public Rewards reward_10percent() {
		Rewards rewards = new Rewards();
		rewards.setCashback_rate(10);
		rewards.setCategory("Dining");
		return rewards;
	}

	@Bean
	public CardType diningCard(@Qualifier("reward_10percent") Rewards reward) {
		CardType diningCard = new CardType();
		diningCard.setName("Dining");
		diningCard.setRewards(new ArrayList<Rewards>(Arrays.asList(reward)));
		return diningCard;
	}

}
