package com.fdmgroup.creditocube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.fdmgroup.creditocube.model.CardType;
import com.fdmgroup.creditocube.service.CardTypeService;

@Controller
public class CardTypeController {

    @Autowired
    private CardTypeService cardTypeService;

    @GetMapping("/card-types")
    public String listCardTypes(Model model) {
        model.addAttribute("cardTypes", cardTypeService.findAllCardTypes());
        return "card-types";
    }

    @GetMapping("/create-card-type")
    public String createCardTypeForm(Model model) {
        model.addAttribute("cardType", new CardType());
        return "create-card-type";
    }

    @PostMapping("/create-card-type")
    public String createCardType(CardType cardType, Model model) {
        cardTypeService.saveCardType(cardType);
        return "redirect:/card-types";
    }
}
