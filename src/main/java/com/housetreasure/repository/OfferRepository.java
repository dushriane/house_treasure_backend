package com.housetreasure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.housetreasure.model.Offer;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    
}
