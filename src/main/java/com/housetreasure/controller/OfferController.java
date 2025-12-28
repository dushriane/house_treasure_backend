package com.housetreasure.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.housetreasure.model.Offer;
import com.housetreasure.model.Offer.OfferStatus;
import com.housetreasure.service.OfferService;

@RestController
@RequestMapping("/api/offers")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    // === BASIC OPERATIONS ===
    @GetMapping
    public List<Offer> getAllOffers() {
        return offerService.getAllOffers();
    }

    @PostMapping
    public Offer createOffer(@RequestBody Offer offer) {
        return offerService.saveOffer(offer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Long id) {
        return offerService.getOfferById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // === MAKING OFFERS ===
    @PostMapping("/make")
    public ResponseEntity<Offer> makeOffer(@RequestBody Map<String, Object> request) {
        try {
            Long buyerId = Long.valueOf(request.get("buyerId").toString());
            Long sellerId = Long.valueOf(request.get("sellerId").toString());
            String itemId = request.get("itemId").toString();
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String message = (String) request.get("message");
            Integer validityHours = request.get("validityHours") != null ? 
                Integer.valueOf(request.get("validityHours").toString()) : null;

            Offer offer = offerService.makeOffer(buyerId, sellerId, itemId, amount, message, validityHours);
            return ResponseEntity.ok(offer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // === UPDATING OFFERS ===
    @PutMapping("/{id}")
    public ResponseEntity<Offer> updateOffer(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            BigDecimal newAmount = new BigDecimal(request.get("amount").toString());
            String newMessage = (String) request.get("message");
            
            Offer updated = offerService.updateOffer(id, newAmount, newMessage);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // === OFFER RESPONSES ===
    @PutMapping("/{id}/accept")
    public ResponseEntity<Offer> acceptOffer(@PathVariable Long id) {
        try {
            Offer accepted = offerService.acceptOffer(id);
            return ResponseEntity.ok(accepted);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Offer> rejectOffer(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            Offer rejected = offerService.rejectOffer(id, reason);
            return ResponseEntity.ok(rejected);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/counter")
    public ResponseEntity<Offer> counterOffer(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            BigDecimal counterAmount = new BigDecimal(request.get("counterAmount").toString());
            String counterMessage = (String) request.get("counterMessage");
            
            Offer countered = offerService.counterOffer(id, counterAmount, counterMessage);
            return ResponseEntity.ok(countered);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // === CANCELING OFFERS ===
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Offer> cancelOffer(@PathVariable Long id, @RequestParam Long buyerId) {
        try {
            Offer canceled = offerService.cancelOffer(id, buyerId);
            return ResponseEntity.ok(canceled);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // === RETRIEVING OFFERS ===
    @GetMapping("/item/{itemId}")
    public List<Offer> getOffersForItem(@PathVariable String itemId) {
        return offerService.getOffersForItem(itemId);
    }

    @GetMapping("/buyer/{buyerId}")
    public List<Offer> getOffersMadeByUser(@PathVariable Long buyerId) {
        return offerService.getOffersMadeByUser(buyerId);
    }

    @GetMapping("/seller/{sellerId}")
    public List<Offer> getOffersReceivedByUser(@PathVariable Long sellerId) {
        return offerService.getOffersReceivedByUser(sellerId);
    }

    @GetMapping("/user/{userId}")
    public List<Offer> getAllOffersForUser(@PathVariable Long userId) {
        List<Offer> buyerOffers = offerService.getOffersMadeByUser(userId);
        List<Offer> sellerOffers = offerService.getOffersReceivedByUser(userId);
        
        // Combine both lists and return as array
        List<Offer> allOffers = new java.util.ArrayList<>();
        allOffers.addAll(buyerOffers);
        allOffers.addAll(sellerOffers);
        return allOffers;
    }

    @GetMapping("/status/{status}")
    public List<Offer> getOffersByStatus(@PathVariable OfferStatus status) {
        return offerService.getOffersByStatus(status);
    }

    @GetMapping("/buyer/{buyerId}/status/{status}")
    public List<Offer> getOffersByBuyerAndStatus(@PathVariable Long buyerId, @PathVariable OfferStatus status) {
        return offerService.getOffersByBuyerAndStatus(buyerId, status);
    }

    @GetMapping("/seller/{sellerId}/status/{status}")
    public List<Offer> getOffersBySellerAndStatus(@PathVariable Long sellerId, @PathVariable OfferStatus status) {
        return offerService.getOffersBySellerAndStatus(sellerId, status);
    }

    @GetMapping("/item/{itemId}/status/{status}")
    public List<Offer> getOffersByItemAndStatus(@PathVariable String itemId, @PathVariable OfferStatus status) {
        return offerService.getOffersByItemAndStatus(itemId, status);
    }

    // === OFFER HISTORY & NEGOTIATIONS ===
    @GetMapping("/history")
    public List<Offer> getOfferHistory(@RequestParam Long buyerId, 
                                      @RequestParam Long sellerId, 
                                      @RequestParam String itemId) {
        return offerService.getOfferHistory(buyerId, sellerId, itemId);
    }

    @GetMapping("/item/{itemId}/pending")
    public List<Offer> getPendingOffersForItem(@PathVariable String itemId) {
        return offerService.getPendingOffersForItem(itemId);
    }

    @GetMapping("/item/{itemId}/highest")
    public ResponseEntity<Offer> getHighestOfferForItem(@PathVariable String itemId) {
        return offerService.getHighestOfferForItem(itemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // === STATISTICS ===
    @GetMapping("/item/{itemId}/count")
    public Map<String, Long> getOfferCountForItem(@PathVariable String itemId) {
        return Map.of("offerCount", offerService.getOfferCountForItem(itemId));
    }

    @GetMapping("/stats/status/{status}")
    public Map<String, Long> getOfferCountByStatus(@PathVariable OfferStatus status) {
        return Map.of("count", offerService.getOfferCountByStatus(status));
    }

    @GetMapping("/recent")
    public List<Offer> getRecentOffers(@RequestParam(defaultValue = "30") int days) {
        return offerService.getRecentOffers(days);
    }

    // === UTILITY ENDPOINTS ===
    @PostMapping("/mark-expired")
    public ResponseEntity<String> markExpiredOffers() {
        offerService.markExpiredOffers();
        return ResponseEntity.ok("Expired offers marked");
    }

    @GetMapping("/expired")
    public List<Offer> getExpiredOffers() {
        return offerService.getExpiredOffers();
    }

    @GetMapping("/can-make-offer")
    public Map<String, Boolean> canMakeOffer(@RequestParam Long buyerId, @RequestParam String itemId) {
        return Map.of("canMakeOffer", offerService.canMakeOffer(buyerId, itemId));
    }

    // === NEGOTIATION ===
    @PutMapping("/{id}/respond-counter")
    public ResponseEntity<Offer> respondToCounterOffer(@PathVariable Long id, 
                                                      @RequestBody Map<String, Object> request) {
        try {
            Boolean accept = (Boolean) request.get("accept");
            BigDecimal newCounterAmount = request.get("newCounterAmount") != null ? 
                new BigDecimal(request.get("newCounterAmount").toString()) : null;
            String message = (String) request.get("message");
            
            Offer response = offerService.respondToCounterOffer(id, accept, newCounterAmount, message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
