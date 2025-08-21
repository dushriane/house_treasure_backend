package com.housetreasure.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String phoneNumber;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile userProfile;

    // Mobile money information for Rwanda
    private String mtnMobileMoneyNumber;
    private String airtelMoneyNumber;
    private String preferredPaymentMethod; // "MTN", "AIRTEL", "BOTH"

    // Location information
    private String province;
    private String district;
    private String sector;
    private String cell;
    private String village;

    // Account status
    private Boolean isActive = true;
    private String verificationToken;

    // Timestamps
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
