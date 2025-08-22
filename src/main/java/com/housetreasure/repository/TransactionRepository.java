package com.housetreasure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.housetreasure.model.Transaction;
import com.housetreasure.model.Transaction.PaymentMethod;
import com.housetreasure.model.Transaction.TransactionStatus;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Find transactions by buyer
    List<Transaction> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);
    
    // Find transactions by seller
    List<Transaction> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    
    // Find transactions by item
    List<Transaction> findByItemIdOrderByCreatedAtDesc(Long itemId);
    
    // Find transactions by status
    List<Transaction> findByStatus(TransactionStatus status);
    
    // Find transactions by payment method
    List<Transaction> findByPaymentMethod(PaymentMethod paymentMethod);
    
    // Find transactions by buyer and status
    List<Transaction> findByBuyerIdAndStatus(Long buyerId, TransactionStatus status);
    
    // Find transactions by seller and status
    List<Transaction> findBySellerIdAndStatus(Long sellerId, TransactionStatus status);
    
    // Find transactions by transaction reference
    Optional<Transaction> findByTransactionReference(String transactionReference);
    
    // Find transactions by date range
    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Find pending payments
    @Query("SELECT t FROM Transaction t WHERE t.status IN ('PENDING', 'PAYMENT_SENT') AND t.createdAt < ?1")
    List<Transaction> findPendingPayments(LocalDateTime cutoffTime);
    
    // Find disputed transactions
    List<Transaction> findByStatusAndCreatedAtBetween(TransactionStatus status, LocalDateTime start, LocalDateTime end);
    
    // Find transactions requiring pickup
    @Query("SELECT t FROM Transaction t WHERE t.status = 'PAYMENT_CONFIRMED' AND t.pickupDate IS NOT NULL")
    List<Transaction> findTransactionsRequiringPickup();
    
    // Count transactions by status
    long countByStatus(TransactionStatus status);
    
    // Find refunded transactions
    List<Transaction> findByIsRefundedTrue();
    
    // Find transactions by phone number
    List<Transaction> findByBuyerPhoneNumberOrSellerPhoneNumber(String buyerPhone, String sellerPhone);
    
    // Monthly transaction summary
    @Query("SELECT EXTRACT(MONTH FROM t.createdAt) as month, COUNT(t) as count, SUM(t.amount) as total " +
           "FROM Transaction t WHERE EXTRACT(YEAR FROM t.createdAt) = ?1 GROUP BY EXTRACT(MONTH FROM t.createdAt)")
    List<Object[]> getMonthlyTransactionSummary(int year);
}
