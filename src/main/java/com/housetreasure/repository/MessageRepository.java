package com.housetreasure.repository;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.housetreasure.model.Message;

public interface MessageRepository extends MongoRepository<Message, String> {
    // Find conversations between two users
    List<Message> findBySenderAndReceiverOrderBySentAtAsc(String sender, String receiver);
    
    // Find all messages in a conversation (both directions)
    @Query("{'$or': [" +
           "{'sender': ?0, 'receiver': ?1}, " +
           "{'sender': ?1, 'receiver': ?0}" +
           "]}")
    List<Message> findConversationBetweenUsers(String user1, String user2);
    
    // Find all conversations for a user (as sender or receiver)
    @Query("{'$or': [{'sender': ?0}, {'receiver': ?0}]}")
    List<Message> findAllConversationsForUser(String userId);
    
    // Find unread messages for a user
    List<Message> findByReceiverAndIsRead(String receiver, Boolean isRead);
    
    // Find messages by item
    List<Message> findByItemOrderBySentAtAsc(String itemId);
    
    // Find messages by transaction
    List<Message> findByTransactionOrderBySentAtAsc(String transactionId);
    
    // Search messages by content
    @Query("{'$and': [" +
           "{'$or': [{'sender': ?0}, {'receiver': ?0}]}, " +
           "{'content': {$regex: ?1, $options: 'i'}}" +
           "]}")
    List<Message> searchMessagesByContent(String userId, String content);
    
    // Find messages by date range
    List<Message> findBySentAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Find latest message between two users
    @Query("{'$or': [" +
           "{'sender': ?0, 'receiver': ?1}, " +
           "{'sender': ?1, 'receiver': ?0}" +
           "]}")
    Page<Message> findLatestMessageBetweenUsers(String user1, String user2, Pageable pageable);
    
    // Count unread messages for user
    long countByReceiverAndIsRead(String receiver, Boolean isRead);
    
    // Find conversations by item
    @Query("{'item': ?0}")
    List<Message> findConversationsByItem(String itemId);
    
    // Delete conversation between users
    void deleteBySenderAndReceiver(String sender, String receiver);
    
    // Find messages by media type
    List<Message> findByMediaTypeAndSender(String mediaType, String sender);
}