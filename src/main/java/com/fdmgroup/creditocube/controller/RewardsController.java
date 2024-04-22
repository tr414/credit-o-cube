package com.fdmgroup.creditocube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fdmgroup.creditocube.model.CardType;
import com.fdmgroup.creditocube.model.Rewards;
import com.fdmgroup.creditocube.service.CardTypeService;
import com.fdmgroup.creditocube.service.RewardsService;

import java.util.List;

@Controller
public class RewardsController {

    @Autowired
    private RewardsService rewardsService;

    @Autowired
    private CardTypeService cardTypeService;

    @GetMapping("/create-reward")
    public String createRewardForm(Model model) {
        List<CardType> cardTypes = cardTypeService.findAllCardTypes();
        model.addAttribute("reward", new Rewards());
        model.addAttribute("cardTypes", cardTypes);
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
