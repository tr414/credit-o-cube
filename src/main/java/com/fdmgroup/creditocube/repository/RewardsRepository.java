package com.fdmgroup.creditocube.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fdmgroup.creditocube.model.CardType;
import com.fdmgroup.creditocube.model.Rewards;

@Repository
public interface RewardsRepository extends JpaRepository<Rewards, Long> {

	long count();
	public Optional<Rewards> findByCardTypeIsAndCategoryIs(CardType cardType, String category); 
}
