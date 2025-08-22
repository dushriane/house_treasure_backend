package com.housetreasure.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "offers")
@Getter
@Setter
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @JoinColumn(name = "item_id", nullable = false)
    private String itemId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal offeredAmount;

    @Column(columnDefinition = "TEXT")
    private String message; // Optional message with the offer

    @Enumerated(EnumType.STRING)
    private OfferStatus status = OfferStatus.PENDING;

    // Offer validity
    private LocalDateTime expiresAt;
    private Boolean isExpired = false;

    // Counter offer information
    private BigDecimal counterOfferAmount;
    private String counterOfferMessage;
    private LocalDateTime counterOfferCreatedAt;

    // Timestamps
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime respondedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime rejectedAt;

    // If offer was accepted, link to transaction
    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @PreUpdate
    protected void onUpdate() {
        // Check if offer has expired
        if (expiresAt != null && LocalDateTime.now().isAfter(expiresAt)) {
            isExpired = true;
            if (status == OfferStatus.PENDING) {
                status = OfferStatus.EXPIRED;
            }
        }
    }

    public enum OfferStatus {
        PENDING,    // Offer sent, waiting for response
        ACCEPTED,   // Seller accepted the offer
        REJECTED,   // Seller rejected the offer
        COUNTERED,  // Seller made a counter offer
        EXPIRED,    // Offer expired without response
        WITHDRAWN   // Buyer withdrew the offer
    }
}
