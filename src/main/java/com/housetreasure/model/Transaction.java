package com.housetreasure.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(name = "item_id", nullable = false)
    private String itemId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // Payment information for Rwanda mobile money
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // MTN_MOBILE_MONEY, AIRTEL_MONEY

    private String transactionReference; // Mobile money transaction reference
    private String buyerPhoneNumber; // Phone number used for payment
    private String sellerPhoneNumber; // Phone number receiving payment

    // Transaction status
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;

    // Pickup and delivery information
    private String pickupLocation;
    private LocalDateTime pickupDate;
    private String pickupInstructions;

    // Communication between buyer and seller
    private String buyerMessage;
    private String sellerMessage;

    // Timestamps
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime paymentConfirmedAt;
    private LocalDateTime pickupCompletedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;

    // Dispute and refund information
    private String cancellationReason;
    private String disputeDescription;
    private Boolean isRefunded = false;
    private LocalDateTime refundedAt;


    public enum PaymentMethod {
        MTN_MOBILE_MONEY,
        AIRTEL_MONEY
    }

    public enum TransactionStatus {
        PENDING,           // Buyer initiated transaction
        PAYMENT_SENT,      // Payment sent to seller
        PAYMENT_CONFIRMED, // Seller confirmed payment
        PICKUP_ARRANGED,   // Pickup location and time arranged
        PICKUP_COMPLETED,  // Item picked up by buyer
        COMPLETED,         // Transaction fully completed
        CANCELLED,         // Transaction cancelled
        DISPUTED           // Transaction in dispute
    }
}
