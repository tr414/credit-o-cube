package com.fdmgroup.creditocube.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.repository.RewardsRepository;
import com.fdmgroup.creditocube.model.Rewards;
import java.util.List;

@Service
public class RewardsService {

	@Autowired
	private RewardsRepository rewardsRepository;

	public boolean isRewardsTableEmpty() {
		boolean isRewardsTableEmpty = rewardsRepository.count() == 0;
		System.out.println("Rewards table is empty: " + isRewardsTableEmpty);
		return rewardsRepository.count() == 0;
	}
	
	public Rewards saveReward(Rewards reward) {
        return rewardsRepository.save(reward);
    }

    public List<Rewards> findAllRewards() {
        return rewardsRepository.findAll();
    }

}
