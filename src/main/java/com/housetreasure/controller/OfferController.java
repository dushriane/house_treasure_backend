package com.housetreasure.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.housetreasure.model.Offer;
import com.housetreasure.service.OfferService;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    public List<Offer> getAllOffers() {
        return offerService.getAllOffers();
    }

    @PostMapping
    public Offer createOffer(@RequestBody Offer offer) {
        return offerService.saveOffer(offer);
    }
}
