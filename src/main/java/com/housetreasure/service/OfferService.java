package com.housetreasure.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.housetreasure.model.Offer;
import com.housetreasure.repository.OfferRepository;

@Service
public class OfferService {
    private final OfferRepository offerRepository;

    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }   
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }
    public Offer saveOffer(Offer offer) {
        return offerRepository.save(offer);
    }
}
