package com.housetreasure.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter
@Setter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Item condition and details
    @Enumerated(EnumType.STRING)
    private ItemCondition condition;

    private String brand;
    private String model;
    private Integer yearOfPurchase;
    private String originalReceipt; // URL to receipt image

    // Item Images table in db 
    @ElementCollection
    @CollectionTable(name = "item_images", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    // Item status
    @Enumerated(EnumType.STRING)
    private ItemStatus status = ItemStatus.AVAILABLE;

    // Negotiation settings
    private Boolean isNegotiable = true;

    // Timestamps
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private LocalDateTime soldAt;


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ItemCondition {
        NEW,
        LIKE_NEW,
        GOOD,
        FAIR,
        POOR
    }

    public enum ItemStatus {
        AVAILABLE,
        RESERVED,
        SOLD,
        DELETED
    }
}
