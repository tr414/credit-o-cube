package com.fdmgroup.creditocube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fdmgroup.creditocube.model.Rewards;

@Repository
public interface RewardsRepository extends JpaRepository<Rewards, Long> {

	long count();

}
