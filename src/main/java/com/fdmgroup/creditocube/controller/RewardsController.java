package com.fdmgroup.creditocube.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.fdmgroup.creditocube.model.CardType;
import com.fdmgroup.creditocube.model.Merchant;
import com.fdmgroup.creditocube.model.Rewards;
import com.fdmgroup.creditocube.service.CardTypeService;
import com.fdmgroup.creditocube.service.MerchantService;
import com.fdmgroup.creditocube.service.RewardsService;

@Controller
public class RewardsController {

	@Autowired
	private RewardsService rewardsService;

	@Autowired
	private CardTypeService cardTypeService;

	@Autowired
	private MerchantService merchantService;

	@GetMapping("/create-reward")
	public String createRewardForm(Model model) {
		List<CardType> cardTypes = cardTypeService.findAllCardTypes();
		List<Merchant> merchantList = merchantService.findAllMerchants();
		Set<String> categories = new HashSet<>();
		for (Merchant merchant : merchantList) {
			categories.add(merchant.getCategory());
		}
		model.addAttribute("reward", new Rewards());
		model.addAttribute("cardTypes", cardTypes);
		model.addAttribute("categories", categories);
		return "create-reward";
	}

	@PostMapping("/create-reward")
	public String createReward(@ModelAttribute Rewards reward, Model model) {
		rewardsService.saveReward(reward);
		return "redirect:/list-rewards";
	}

	@GetMapping("/list-rewards")
	public String listRewards(Model model) {
		List<Rewards> rewards = rewardsService.findAllRewards();
		model.addAttribute("rewards", rewards);
		return "list-rewards";
	}
}
