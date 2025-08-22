package com.housetreasure.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.housetreasure.model.Message;
import com.housetreasure.repository.MessageRepository;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // === BASIC OPERATIONS ===
    
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Message saveMessage(Message message) {
        message.setSentAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        return messageRepository.save(message);
    }

    public Optional<Message> getMessageById(String id) {
        return messageRepository.findById(id);
    }

    // === CONVERSATION MANAGEMENT ===
    public Message startConversation(String sender, String receiver, String itemId, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setItem(itemId);
        message.setContent(content);
        message.setMessageType("TEXT");
        message.setStatus("SENT");
        return saveMessage(message);
    }

    public Message sendMessage(String sender, String receiver, String content, String messageType) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setMessageType(messageType != null ? messageType : "TEXT");
        message.setStatus("SENT");
        return saveMessage(message);
    }

    public Message sendMediaMessage(String sender, String receiver, String mediaUrl, String mediaType) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMediaUrl(mediaUrl);
        message.setMediaType(mediaType);
        message.setMessageType("MEDIA");
        message.setStatus("SENT");
        return saveMessage(message);
    }

    public List<Message> getConversationHistory(String user1, String user2) {
        return messageRepository.findConversationBetweenUsers(user1, user2)
                .stream()
                .sorted((m1, m2) -> m1.getSentAt().compareTo(m2.getSentAt()))
                .collect(Collectors.toList());
    }

    // === MESSAGE STATUS MANAGEMENT ===
    public Message markAsRead(String messageId) {
        return messageRepository.findById(messageId)
                .map(message -> {
                    message.setIsRead(true);
                    message.setReadAt(LocalDateTime.now());
                    message.setStatus("READ");
                    message.setUpdatedAt(LocalDateTime.now());
                    return messageRepository.save(message);
                })
                .orElse(null);
    }

    public Message markAsUnread(String messageId) {
        return messageRepository.findById(messageId)
                .map(message -> {
                    message.setIsRead(false);
                    message.setReadAt(null);
                    message.setStatus("DELIVERED");
                    message.setUpdatedAt(LocalDateTime.now());
                    return messageRepository.save(message);
                })
                .orElse(null);
    }

    public void markAllAsRead(String receiver, String sender) {
        List<Message> unreadMessages = messageRepository.findBySenderAndReceiverOrderBySentAtAsc(sender, receiver)
                .stream()
                .filter(message -> !message.getIsRead())
                .collect(Collectors.toList());
        
        unreadMessages.forEach(message -> {
            message.setIsRead(true);
            message.setReadAt(LocalDateTime.now());
            message.setStatus("READ");
            messageRepository.save(message);
        });
    }

    // === CONVERSATION RETRIEVAL ===
    public List<Message> getAllConversationsForUser(String userId) {
        return messageRepository.findAllConversationsForUser(userId);
    }

    public List<Message> getUnreadMessages(String userId) {
        return messageRepository.findByReceiverAndIsRead(userId, false);
    }

    public long getUnreadMessageCount(String userId) {
        return messageRepository.countByReceiverAndIsRead(userId, false);
    }

    // === CONVERSATION FILTERING ===
    public List<Message> getConversationsByItem(String itemId) {
        return messageRepository.findByItemOrderBySentAtAsc(itemId);
    }

    public List<Message> getConversationsByTransaction(String transactionId) {
        return messageRepository.findByTransactionOrderBySentAtAsc(transactionId);
    }

    public List<Message> getMessagesByDateRange(LocalDateTime start, LocalDateTime end) {
        return messageRepository.findBySentAtBetween(start, end);
    }

    public List<Message> getReadConversations(String userId) {
        return messageRepository.findAllConversationsForUser(userId)
                .stream()
                .filter(message -> message.getReceiver().equals(userId) && message.getIsRead())
                .collect(Collectors.toList());
    }

    public List<Message> getUnreadConversations(String userId) {
        return messageRepository.findAllConversationsForUser(userId)
                .stream()
                .filter(message -> message.getReceiver().equals(userId) && !message.getIsRead())
                .collect(Collectors.toList());
    }

    // === SEARCH ===
    public List<Message> searchMessages(String userId, String searchTerm) {
        return messageRepository.searchMessagesByContent(userId, searchTerm);
    }

    // === CONVERSATION MANAGEMENT ===
    public void deleteConversation(String user1, String user2) {
        List<Message> conversation = messageRepository.findConversationBetweenUsers(user1, user2);
        messageRepository.deleteAll(conversation);
    }

    public void deleteMessage(String messageId) {
        messageRepository.deleteById(messageId);
    }

    // === PRICE NEGOTIATION ===
    public Message sendPriceOffer(String sender, String receiver, String itemId, Double offerPrice) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setItem(itemId);
        message.setContent("Price offer: $" + offerPrice);
        message.setMessageType("PRICE_OFFER");
        message.setStatus("SENT");
        return saveMessage(message);
    }

    public Message respondToPriceOffer(String sender, String receiver, String itemId, 
                                     Double counterOffer, boolean accepted) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setItem(itemId);
        
        if (accepted) {
            message.setContent("Offer accepted!");
            message.setMessageType("OFFER_ACCEPTED");
        } else {
            message.setContent("Counter offer: $" + counterOffer);
            message.setMessageType("COUNTER_OFFER");
        }
        
        message.setStatus("SENT");
        return saveMessage(message);
    }

    // === LOCATION SHARING ===
    public Message shareLocation(String sender, String receiver, String latitude, 
                               String longitude, String locationName) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent("Shared location: " + locationName);
        message.setMessageType("LOCATION");
        message.setMediaUrl(latitude + "," + longitude); // Store coordinates in mediaUrl
        message.setStatus("SENT");
        return saveMessage(message);
    }

    // === MEETUP SCHEDULING ===
    public Message scheduleMeetup(String sender, String receiver, String itemId, 
                                LocalDateTime meetupTime, String location) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setItem(itemId);
        message.setContent("Meetup scheduled for " + meetupTime + " at " + location);
        message.setMessageType("MEETUP");
        message.setStatus("SENT");
        return saveMessage(message);
    }

    // === PHOTO SHARING ===
    public Message sendPhoto(String sender, String receiver, String photoUrl, String caption) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(caption);
        message.setMediaUrl(photoUrl);
        message.setMediaType("image");
        message.setMessageType("MEDIA");
        message.setStatus("SENT");
        return saveMessage(message);
    }

    // === BLOCKING AND REPORTING ===
    public Message reportMessage(String messageId, String reporterId, String reason) {
        // This would typically create a report record in a separate table
        // For now, we'll mark the message with a special status
        return messageRepository.findById(messageId)
                .map(message -> {
                    message.setStatus("REPORTED");
                    message.setUpdatedAt(LocalDateTime.now());
                    return messageRepository.save(message);
                })
                .orElse(null);
    }

    // === NOTIFICATIONS ===
    public List<Message> getRecentNotifications(String userId, int limit) {
        return getUnreadMessages(userId)
                .stream()
                .sorted((m1, m2) -> m2.getSentAt().compareTo(m1.getSentAt()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // === UTILITY METHODS ===
    public Message getLatestMessageBetweenUsers(String user1, String user2) {
        return messageRepository.findLatestMessageBetweenUsers(user1, user2, 
                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "sentAt")))
                .getContent()
                .stream()
                .findFirst()
                .orElse(null);
    }

    public List<Message> getMediaMessages(String userId, String mediaType) {
        return messageRepository.findByMediaTypeAndSender(mediaType, userId);
    }
}
