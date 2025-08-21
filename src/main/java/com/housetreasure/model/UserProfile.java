package com.housetreasure.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Profile information
    private String profilePictureUrl;
    private String bio;
    
    // Contact preferences
    private String preferredContactMethod; // "PHONE", "EMAIL", "IN_APP"
    
    // Activity statistics
    private Integer itemsListed = 0;
    private Integer itemsSold = 0;
    private Integer itemsPurchased = 0;
    private Integer totalTransactions = 0;
    private LocalDateTime lastActiveAt;
    
    // Preferences
    private String preferredLanguage = "en"; // "en", "rw", "fr"
    private String timezone = "Rwanda/Kigali";
    private Boolean emailNotifications = true;
    
    // Timestamps
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
