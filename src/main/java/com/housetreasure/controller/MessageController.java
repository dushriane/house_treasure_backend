package com.housetreasure.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.housetreasure.model.Message;
import com.housetreasure.service.MessageService;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // === BASIC OPERATIONS ===
    @GetMapping
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }

    @PostMapping
    public Message createMessage(@RequestBody Message message) {
        return messageService.saveMessage(message);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable String id) {
        return messageService.getMessageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // === CONVERSATION MANAGEMENT ===
    @PostMapping("/start-conversation")
    public Message startConversation(@RequestBody Map<String, String> request) {
        return messageService.startConversation(
                request.get("sender"),
                request.get("receiver"),
                request.get("itemId"),
                request.get("content")
        );
    }

    @PostMapping("/send")
    public Message sendMessage(@RequestBody Map<String, String> request) {
        return messageService.sendMessage(
                request.get("sender"),
                request.get("receiver"),
                request.get("content"),
                request.get("messageType")
        );
    }

    @GetMapping("/conversation/{user1}/{user2}")
    public List<Message> getConversationHistory(@PathVariable String user1, @PathVariable String user2) {
        return messageService.getConversationHistory(user1, user2);
    }

    // === MESSAGE STATUS ===
    @PutMapping("/{id}/mark-read")
    public ResponseEntity<Message> markAsRead(@PathVariable String id) {
        Message message = messageService.markAsRead(id);
        return message != null ? ResponseEntity.ok(message) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/mark-unread")
    public ResponseEntity<Message> markAsUnread(@PathVariable String id) {
        Message message = messageService.markAsUnread(id);
        return message != null ? ResponseEntity.ok(message) : ResponseEntity.notFound().build();
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<String> markAllAsRead(@RequestParam String receiver, @RequestParam String sender) {
        messageService.markAllAsRead(receiver, sender);
        return ResponseEntity.ok("All messages marked as read");
    }

    // === USER CONVERSATIONS ===
    @GetMapping("/user/{userId}/conversations")
    public List<Message> getAllConversationsForUser(@PathVariable String userId) {
        return messageService.getAllConversationsForUser(userId);
    }

    @GetMapping("/user/{userId}/unread")
    public List<Message> getUnreadMessages(@PathVariable String userId) {
        return messageService.getUnreadMessages(userId);
    }

    @GetMapping("/user/{userId}/unread-count")
    public Map<String, Long> getUnreadMessageCount(@PathVariable String userId) {
        return Map.of("unreadCount", messageService.getUnreadMessageCount(userId));
    }

    // === FILTERING ===
    @GetMapping("/item/{itemId}")
    public List<Message> getMessagesByItem(@PathVariable String itemId) {
        return messageService.getConversationsByItem(itemId);
    }

    @GetMapping("/transaction/{transactionId}")
    public List<Message> getMessagesByTransaction(@PathVariable String transactionId) {
        return messageService.getConversationsByTransaction(transactionId);
    }

    @GetMapping("/user/{userId}/read")
    public List<Message> getReadConversations(@PathVariable String userId) {
        return messageService.getReadConversations(userId);
    }

    @GetMapping("/user/{userId}/unread-conversations")
    public List<Message> getUnreadConversations(@PathVariable String userId) {
        return messageService.getUnreadConversations(userId);
    }

    @GetMapping("/date-range")
    public List<Message> getMessagesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return messageService.getMessagesByDateRange(start, end);
    }

    // === SEARCH ===
    @GetMapping("/user/{userId}/search")
    public List<Message> searchMessages(@PathVariable String userId, @RequestParam String query) {
        return messageService.searchMessages(userId, query);
    }

    // === DELETION ===
    @DeleteMapping("/conversation/{user1}/{user2}")
    public ResponseEntity<String> deleteConversation(@PathVariable String user1, @PathVariable String user2) {
        messageService.deleteConversation(user1, user2);
        return ResponseEntity.ok("Conversation deleted");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMessage(@PathVariable String id) {
        messageService.deleteMessage(id);
        return ResponseEntity.ok("Message deleted");
    }

    // === PRICE NEGOTIATION ===
    @PostMapping("/price-offer")
    public Message sendPriceOffer(@RequestBody Map<String, Object> request) {
        return messageService.sendPriceOffer(
                (String) request.get("sender"),
                (String) request.get("receiver"),
                (String) request.get("itemId"),
                (Double) request.get("offerPrice")
        );
    }

    @PostMapping("/price-response")
    public Message respondToPriceOffer(@RequestBody Map<String, Object> request) {
        return messageService.respondToPriceOffer(
                (String) request.get("sender"),
                (String) request.get("receiver"),
                (String) request.get("itemId"),
                (Double) request.get("counterOffer"),
                (Boolean) request.get("accepted")
        );
    }

    // === LOCATION SHARING ===
    @PostMapping("/share-location")
    public Message shareLocation(@RequestBody Map<String, String> request) {
        return messageService.shareLocation(
                request.get("sender"),
                request.get("receiver"),
                request.get("latitude"),
                request.get("longitude"),
                request.get("locationName")
        );
    }

    // === MEETUP SCHEDULING ===
    @PostMapping("/schedule-meetup")
    public Message scheduleMeetup(@RequestBody Map<String, Object> request) {
        return messageService.scheduleMeetup(
                (String) request.get("sender"),
                (String) request.get("receiver"),
                (String) request.get("itemId"),
                LocalDateTime.parse((String) request.get("meetupTime")),
                (String) request.get("location")
        );
    }

    // === PHOTO SHARING ===
    @PostMapping("/send-photo")
    public Message sendPhoto(@RequestBody Map<String, String> request) {
        return messageService.sendPhoto(
                request.get("sender"),
                request.get("receiver"),
                request.get("photoUrl"),
                request.get("caption")
        );
    }

    @PostMapping("/send-media")
    public Message sendMediaMessage(@RequestBody Map<String, String> request) {
        return messageService.sendMediaMessage(
                request.get("sender"),
                request.get("receiver"),
                request.get("mediaUrl"),
                request.get("mediaType")
        );
    }

    // === REPORTING ===
    @PostMapping("/{id}/report")
    public ResponseEntity<Message> reportMessage(@PathVariable String id, @RequestBody Map<String, String> request) {
        Message message = messageService.reportMessage(id, request.get("reporterId"), request.get("reason"));
        return message != null ? ResponseEntity.ok(message) : ResponseEntity.notFound().build();
    }

    // === NOTIFICATIONS ===
    @GetMapping("/user/{userId}/notifications")
    public List<Message> getRecentNotifications(@PathVariable String userId, 
                                               @RequestParam(defaultValue = "10") int limit) {
        return messageService.getRecentNotifications(userId, limit);
    }

    // === UTILITY ===
    @GetMapping("/latest/{user1}/{user2}")
    public ResponseEntity<Message> getLatestMessage(@PathVariable String user1, @PathVariable String user2) {
        Message message = messageService.getLatestMessageBetweenUsers(user1, user2);
        return message != null ? ResponseEntity.ok(message) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}/media/{mediaType}")
    public List<Message> getMediaMessages(@PathVariable String userId, @PathVariable String mediaType) {
        return messageService.getMediaMessages(userId, mediaType);
    }
}
