package com.housetreasure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.housetreasure.model.Offer;
import com.housetreasure.model.Offer.OfferStatus;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    // Find offers by item
    List<Offer> findByItemIdOrderByCreatedAtDesc(Long itemId);
    
    // Find offers made by a buyer
    List<Offer> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);
    
    // Find offers received by a seller
    List<Offer> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    
    // Find offers by status
    List<Offer> findByStatus(OfferStatus status);
    
    // Find offers by buyer and status
    List<Offer> findByBuyerIdAndStatus(Long buyerId, OfferStatus status);
    
    // Find offers by seller and status
    List<Offer> findBySellerIdAndStatus(Long sellerId, OfferStatus status);
    
    // Find offers by item and status
    List<Offer> findByItemIdAndStatus(Long itemId, OfferStatus status);
    
    // Find pending offers for a specific item
    List<Offer> findByItemIdAndStatusOrderByCreatedAtDesc(Long itemId, OfferStatus status);
    
    // Find expired offers
    @Query("SELECT o FROM Offer o WHERE o.expiresAt < ?1 AND o.status = 'PENDING'")
    List<Offer> findExpiredOffers(LocalDateTime now);
    
    // Find offers between buyer and seller for specific item
    List<Offer> findByBuyerIdAndSellerIdAndItemIdOrderByCreatedAtDesc(Long buyerId, Long sellerId, Long itemId);
    
    // Check if buyer has pending offer for item
    Optional<Offer> findByBuyerIdAndItemIdAndStatus(Long buyerId, Long itemId, OfferStatus status);
    
    // Get highest offer for an item
    @Query("SELECT o FROM Offer o WHERE o.item.id = ?1 AND o.status IN ('PENDING', 'COUNTERED') ORDER BY o.offeredAmount DESC")
    Page<Offer> findHighestOfferForItem(Long itemId, Pageable pageable);
    
    // Count offers by status
    long countByStatus(OfferStatus status);
    
    // Count offers for item
    long countByItemId(Long itemId);
    
    // Find recent offers (last 30 days)
    @Query("SELECT o FROM Offer o WHERE o.createdAt >= ?1 ORDER BY o.createdAt DESC")
    List<Offer> findRecentOffers(LocalDateTime since);
}
