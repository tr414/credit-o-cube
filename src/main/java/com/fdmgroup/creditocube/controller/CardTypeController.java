package com.fdmgroup.creditocube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String createCardType(CardType cardType, RedirectAttributes redirectAttributes) {
        CardType savedCardType = cardTypeService.saveCardType(cardType);
        if (savedCardType == null) {
            redirectAttributes.addFlashAttribute("error", "Card type already exists. Please rename your card type name.");
            return "redirect:/create-card-type";
        }
        return "redirect:/card-types";
    }
}
