package com.housetreasure.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "messages")
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    // Optional: Link to a specific item if the message is about an item
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    // Optional: Link to a transaction if the message is about a transaction
    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType messageType = MessageType.TEXT;

    // For media messages (images, voice notes, etc.)
    private String mediaUrl;
    private String mediaType; // "image", "voice", "document"

    // Message status
    @Enumerated(EnumType.STRING)
    private MessageStatus status = MessageStatus.SENT;

    private Boolean isRead = false;
    private LocalDateTime readAt;

    // Timestamps
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum MessageType {
        TEXT,
        IMAGE,
        VOICE,
        DOCUMENT,
        SYSTEM // For system notifications
    }

    public enum MessageStatus {
        SENT,
        DELIVERED,
        READ,
        FAILED
    }
}
