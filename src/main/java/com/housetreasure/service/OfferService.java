package com.housetreasure.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.housetreasure.model.Offer;
import com.housetreasure.model.Offer.OfferStatus;
import com.housetreasure.model.User;
import com.housetreasure.repository.OfferRepository;

@Service
public class OfferService {
    private final OfferRepository offerRepository;
    private final UserService userService;
    private final ItemService itemService;

    public OfferService(OfferRepository offerRepository, UserService userService, ItemService itemService) {
        this.offerRepository = offerRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    // === BASIC OPERATIONS ===
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    public Offer saveOffer(Offer offer) {
        return offerRepository.save(offer);
    }

    public Optional<Offer> getOfferById(Long id) {
        return offerRepository.findById(id);
    }

    // === MAKING OFFERS ===
    public Offer makeOffer(Long buyerId, Long sellerId, Long itemId, BigDecimal amount, 
                          String message, Integer validityHours) {
        
        // Check if buyer already has a pending offer for this item
        Optional<Offer> existingOffer = offerRepository.findByBuyerIdAndItemIdAndStatus(
            buyerId, itemId, OfferStatus.PENDING);
        
        if (existingOffer.isPresent()) {
            throw new RuntimeException("You already have a pending offer for this item");
        }

        Offer offer = new Offer();
        
        // Set users and item (you'd typically fetch these from database)
        User buyer = userService.getUserById(buyerId).orElseThrow(() -> 
            new RuntimeException("Buyer not found"));
        User seller = userService.getUserById(sellerId).orElseThrow(() -> 
            new RuntimeException("Seller not found"));
        
        offer.setBuyer(buyer);
        offer.setSeller(seller);
        offer.setOfferedAmount(amount);
        offer.setMessage(message);
        offer.setStatus(OfferStatus.PENDING);
        offer.setCreatedAt(LocalDateTime.now());
        
        // Set expiry if validity hours provided
        if (validityHours != null && validityHours > 0) {
            offer.setExpiresAt(LocalDateTime.now().plusHours(validityHours));
        }

        return offerRepository.save(offer);
    }

    // === UPDATING OFFERS ===
    
    public Offer updateOffer(Long offerId, BigDecimal newAmount, String newMessage) {
        return offerRepository.findById(offerId)
            .map(offer -> {
                if (offer.getStatus() != OfferStatus.PENDING) {
                    throw new RuntimeException("Can only update pending offers");
                }
                
                offer.setOfferedAmount(newAmount);
                offer.setMessage(newMessage);
                return offerRepository.save(offer);
            })
            .orElseThrow(() -> new RuntimeException("Offer not found"));
    }

    // === OFFER RESPONSES ===
    
    public Offer acceptOffer(Long offerId) {
        return offerRepository.findById(offerId)
            .map(offer -> {
                if (offer.getStatus() != OfferStatus.PENDING && offer.getStatus() != OfferStatus.COUNTERED) {
                    throw new RuntimeException("Can only accept pending or countered offers");
                }
                
                offer.setStatus(OfferStatus.ACCEPTED);
                offer.setAcceptedAt(LocalDateTime.now());
                offer.setRespondedAt(LocalDateTime.now());
                
                // Here you might want to create a transaction
                // offer.setTransaction(createTransactionFromOffer(offer));
                
                return offerRepository.save(offer);
            })
            .orElseThrow(() -> new RuntimeException("Offer not found"));
    }

    public Offer rejectOffer(Long offerId, String reason) {
        return offerRepository.findById(offerId)
            .map(offer -> {
                if (offer.getStatus() != OfferStatus.PENDING && offer.getStatus() != OfferStatus.COUNTERED) {
                    throw new RuntimeException("Can only reject pending or countered offers");
                }
                
                offer.setStatus(OfferStatus.REJECTED);
                offer.setRejectedAt(LocalDateTime.now());
                offer.setRespondedAt(LocalDateTime.now());
                offer.setCounterOfferMessage(reason); // Store rejection reason
                
                return offerRepository.save(offer);
            })
            .orElseThrow(() -> new RuntimeException("Offer not found"));
    }

    public Offer counterOffer(Long originalOfferId, BigDecimal counterAmount, String counterMessage) {
        return offerRepository.findById(originalOfferId)
            .map(offer -> {
                if (offer.getStatus() != OfferStatus.PENDING) {
                    throw new RuntimeException("Can only counter pending offers");
                }
                
                offer.setStatus(OfferStatus.COUNTERED);
                offer.setCounterOfferAmount(counterAmount);
                offer.setCounterOfferMessage(counterMessage);
                offer.setCounterOfferCreatedAt(LocalDateTime.now());
                offer.setRespondedAt(LocalDateTime.now());
                
                return offerRepository.save(offer);
            })
            .orElseThrow(() -> new RuntimeException("Offer not found"));
    }

    // === CANCELING OFFERS ===
    
    public Offer cancelOffer(Long offerId, Long buyerId) {
        return offerRepository.findById(offerId)
            .map(offer -> {
                if (!offer.getBuyer().getId().equals(buyerId)) {
                    throw new RuntimeException("Can only cancel your own offers");
                }
                
                if (offer.getStatus() != OfferStatus.PENDING && offer.getStatus() != OfferStatus.COUNTERED) {
                    throw new RuntimeException("Can only cancel pending or countered offers");
                }
                
                offer.setStatus(OfferStatus.WITHDRAWN);
                offer.setRespondedAt(LocalDateTime.now());
                
                return offerRepository.save(offer);
            })
            .orElseThrow(() -> new RuntimeException("Offer not found"));
    }

    // === RETRIEVING OFFERS ===
    
    public List<Offer> getOffersForItem(Long itemId) {
        return offerRepository.findByItemIdOrderByCreatedAtDesc(itemId);
    }

    public List<Offer> getOffersMadeByUser(Long buyerId) {
        return offerRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId);
    }

    public List<Offer> getOffersReceivedByUser(Long sellerId) {
        return offerRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);
    }

    public List<Offer> getOffersByStatus(OfferStatus status) {
        return offerRepository.findByStatus(status);
    }

    public List<Offer> getOffersByBuyerAndStatus(Long buyerId, OfferStatus status) {
        return offerRepository.findByBuyerIdAndStatus(buyerId, status);
    }

    public List<Offer> getOffersBySellerAndStatus(Long sellerId, OfferStatus status) {
        return offerRepository.findBySellerIdAndStatus(sellerId, status);
    }

    public List<Offer> getOffersByItemAndStatus(Long itemId, OfferStatus status) {
        return offerRepository.findByItemIdAndStatus(itemId, status);
    }

    // === OFFER HISTORY & NEGOTIATIONS ===
    
    public List<Offer> getOfferHistory(Long buyerId, Long sellerId, Long itemId) {
        return offerRepository.findByBuyerIdAndSellerIdAndItemIdOrderByCreatedAtDesc(buyerId, sellerId, itemId);
    }

    public List<Offer> getPendingOffersForItem(Long itemId) {
        return offerRepository.findByItemIdAndStatusOrderByCreatedAtDesc(itemId, OfferStatus.PENDING);
    }

    public Optional<Offer> getHighestOfferForItem(Long itemId) {
        return offerRepository.findHighestOfferForItem(itemId, PageRequest.of(0, 1))
                .getContent()
                .stream()
                .findFirst();
    }

    // === UTILITY METHODS ===
    
    public long getOfferCountForItem(Long itemId) {
        return offerRepository.countByItemId(itemId);
    }

    public long getOfferCountByStatus(OfferStatus status) {
        return offerRepository.countByStatus(status);
    }

    public List<Offer> getRecentOffers(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return offerRepository.findRecentOffers(since);
    }

    // === EXPIRED OFFERS MANAGEMENT ===
    
    public void markExpiredOffers() {
        List<Offer> expiredOffers = offerRepository.findExpiredOffers(LocalDateTime.now());
        
        expiredOffers.forEach(offer -> {
            offer.setStatus(OfferStatus.EXPIRED);
            offer.setIsExpired(true);
            offerRepository.save(offer);
        });
    }

    public List<Offer> getExpiredOffers() {
        return offerRepository.findByStatus(OfferStatus.EXPIRED);
    }

    // === NEGOTIATION HELPERS ===
    
    public boolean hasPendingOffer(Long buyerId, Long itemId) {
        return offerRepository.findByBuyerIdAndItemIdAndStatus(buyerId, itemId, OfferStatus.PENDING).isPresent();
    }

    public boolean canMakeOffer(Long buyerId, Long itemId) {
        // Check if item is available and buyer doesn't have pending offer
        return !hasPendingOffer(buyerId, itemId);
    }

    public Offer respondToCounterOffer(Long offerId, boolean accept, BigDecimal newCounterAmount, String message) {
        return offerRepository.findById(offerId)
            .map(offer -> {
                if (offer.getStatus() != OfferStatus.COUNTERED) {
                    throw new RuntimeException("Can only respond to counter offers");
                }
                
                if (accept) {
                    // Accept the counter offer
                    return acceptOffer(offerId);
                } else if (newCounterAmount != null) {
                    // Make another counter offer
                    offer.setOfferedAmount(newCounterAmount);
                    offer.setMessage(message);
                    offer.setStatus(OfferStatus.PENDING);
                    offer.setCreatedAt(LocalDateTime.now());
                    return offerRepository.save(offer);
                } else {
                    // Reject the counter offer
                    return rejectOffer(offerId, message);
                }
            })
            .orElseThrow(() -> new RuntimeException("Offer not found"));
    }
}
