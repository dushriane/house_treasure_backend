package com.housetreasure.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.housetreasure.model.Offer;
import com.housetreasure.model.Transaction;
import com.housetreasure.model.Transaction.PaymentMethod;
import com.housetreasure.model.Transaction.TransactionStatus;
import com.housetreasure.model.User;
import com.housetreasure.repository.TransactionRepository;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public TransactionService(TransactionRepository transactionRepository, 
                            UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    // === BASIC OPERATIONS ===
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    // === CREATING TRANSACTIONS ===
    public Transaction createTransactionFromOffer(Offer offer) {
        Transaction transaction = new Transaction();
        
        transaction.setBuyer(offer.getBuyer());
        transaction.setSeller(offer.getSeller());
        transaction.setAmount(offer.getOfferedAmount());
        transaction.setTransactionReference(generateTransactionReference());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(LocalDateTime.now());
        
        return transactionRepository.save(transaction);
    }

    public Transaction createTransaction(Long buyerId, Long sellerId, String itemId, 
                                      BigDecimal amount, PaymentMethod paymentMethod,
                                      String buyerPhone, String sellerPhone) {
        Transaction transaction = new Transaction();
        
        // Set users and item
        User buyer = userService.getUserById(buyerId).orElseThrow(() -> 
            new RuntimeException("Buyer not found"));
        User seller = userService.getUserById(sellerId).orElseThrow(() -> 
            new RuntimeException("Seller not found"));
        
        transaction.setBuyer(buyer);
        transaction.setSeller(seller);
        transaction.setAmount(amount);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setBuyerPhoneNumber(buyerPhone);
        transaction.setSellerPhoneNumber(sellerPhone);
        transaction.setTransactionReference(generateTransactionReference());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(LocalDateTime.now());
        
        return transactionRepository.save(transaction);
    }

    // === STATUS MANAGEMENT ===
    public Transaction updateTransactionStatus(Long transactionId, TransactionStatus newStatus) {
        return transactionRepository.findById(transactionId)
            .map(transaction -> {
                transaction.setStatus(newStatus);
                
                // Update timestamps based on status
                switch (newStatus) {
                    case PENDING:
                        // No specific action needed for pending
                        break;
                    case PAYMENT_SENT:
                        // No specific timestamp for payment sent
                        break;
                    case PAYMENT_CONFIRMED:
                        transaction.setPaymentConfirmedAt(LocalDateTime.now());
                        break;
                    case PICKUP_ARRANGED:
                        // No specific timestamp for pickup arranged
                        break;
                    case PICKUP_COMPLETED:
                        transaction.setPickupCompletedAt(LocalDateTime.now());
                        break;
                    case COMPLETED:
                        transaction.setCompletedAt(LocalDateTime.now());
                        break;
                    case CANCELLED:
                        transaction.setCancelledAt(LocalDateTime.now());
                        break;
                    case DISPUTED:
                        // No specific timestamp for disputed
                        break;
                }
                
                return transactionRepository.save(transaction);
            })
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    // === PAYMENT PROCESSING ===
    public Transaction processPayment(Long transactionId, String paymentReference) {
        return transactionRepository.findById(transactionId)
            .map(transaction -> {
                if (transaction.getStatus() != TransactionStatus.PENDING) {
                    throw new RuntimeException("Transaction is not in pending status");
                }
                
                transaction.setStatus(TransactionStatus.PAYMENT_SENT);
                transaction.setTransactionReference(paymentReference);
                
                return transactionRepository.save(transaction);
            })
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public Transaction confirmPayment(Long transactionId, Long sellerId) {
        return transactionRepository.findById(transactionId)
            .map(transaction -> {
                if (!transaction.getSeller().getId().equals(sellerId)) {
                    throw new RuntimeException("Only seller can confirm payment");
                }
                
                if (transaction.getStatus() != TransactionStatus.PAYMENT_SENT) {
                    throw new RuntimeException("Payment not yet sent");
                }
                
                transaction.setStatus(TransactionStatus.PAYMENT_CONFIRMED);
                transaction.setPaymentConfirmedAt(LocalDateTime.now());
                
                return transactionRepository.save(transaction);
            })
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public Transaction verifyPayment(Long transactionId, String verificationCode) {
        // In a real implementation, this would verify with mobile money provider
        return transactionRepository.findById(transactionId)
            .map(transaction -> {
                // Simulate payment verification
                transaction.setStatus(TransactionStatus.PAYMENT_CONFIRMED);
                transaction.setPaymentConfirmedAt(LocalDateTime.now());
                
                return transactionRepository.save(transaction);
            })
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    // === DELIVERY MANAGEMENT ===
    public Transaction updateDeliveryInfo(Long transactionId, String pickupLocation, 
                                        LocalDateTime pickupDate, String instructions) {
        return transactionRepository.findById(transactionId)
            .map(transaction -> {
                transaction.setPickupLocation(pickupLocation);
                transaction.setPickupDate(pickupDate);
                transaction.setPickupInstructions(instructions);
                transaction.setStatus(TransactionStatus.PICKUP_ARRANGED);
                
                return transactionRepository.save(transaction);
            })
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public Transaction confirmItemDelivered(Long transactionId, Long sellerId) {
        return transactionRepository.findById(transactionId)
            .map(transaction -> {
                if (!transaction.getSeller().getId().equals(sellerId)) {
                    throw new RuntimeException("Only seller can confirm delivery");
                }
                
                transaction.setStatus(TransactionStatus.PICKUP_COMPLETED);
                transaction.setPickupCompletedAt(LocalDateTime.now());
                
                return transactionRepository.save(transaction);
            })
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public Transaction confirmItemReceived(Long transactionId, Long buyerId) {
        return transactionRepository.findById(transactionId)
            .map(transaction -> {
                if (!transaction.getBuyer().getId().equals(buyerId)) {
                    throw new RuntimeException("Only buyer can confirm receipt");
                }
                
                transaction.setStatus(TransactionStatus.COMPLETED);
                transaction.setCompletedAt(LocalDateTime.now());
                
                return transactionRepository.save(transaction);
            })
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    // === TRANSACTION COMPLETION ===
    public Transaction completeTransaction(Long transactionId) {
        return transactionRepository.findById(transactionId)
            .map(transaction -> {
                if (transaction.getStatus() != TransactionStatus.PICKUP_COMPLETED) {
                    throw new RuntimeException("Transaction cannot be completed in current status");
                }
                
                transaction.setStatus(TransactionStatus.COMPLETED);
                transaction.setCompletedAt(LocalDateTime.now());
                
                return transactionRepository.save(transaction);
            })
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    // === CANCELLATION AND REFUNDS ===
    public Transaction cancelTransaction(Long transactionId, String reason, Long userId) {
        return transactionRepository.findById(transactionId)
            .map(transaction -> {
                // Check if user is buyer or seller
                if (!transaction.getBuyer().getId().equals(userId) && 
                    !transaction.getSeller().getId().equals(userId)) {
                    throw new RuntimeException("Only buyer or seller can cancel transaction");
                }
                
                transaction.setStatus(TransactionStatus.CANCELLED);
                transaction.setCancellationReason(reason);
                transaction.setCancelledAt(LocalDateTime.now());
                
                return transactionRepository.save(transaction);
            })
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public Transaction processRefund(Long transactionId, String refundReason) {
        return transactionRepository.findById(transactionId)
            .map(transaction -> {
                if (transaction.getStatus() != TransactionStatus.CANCELLED && 
                    transaction.getStatus() != TransactionStatus.DISPUTED) {
                    throw new RuntimeException("Cannot refund transaction in current status");
                }
                
                transaction.setIsRefunded(true);
                transaction.setRefundedAt(LocalDateTime.now());
                transaction.setCancellationReason(refundReason);
                
                return transactionRepository.save(transaction);
            })
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    // === DISPUTE HANDLING ===
    public Transaction reportTransactionIssue(Long transactionId, String description, Long reporterId) {
        return transactionRepository.findById(transactionId)
            .map(transaction -> {
                transaction.setStatus(TransactionStatus.DISPUTED);
                transaction.setDisputeDescription(description);
                
                return transactionRepository.save(transaction);
            })
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    // === TRANSACTION HISTORY ===
    public List<Transaction> getBuyerTransactionHistory(Long buyerId) {
        return transactionRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId);
    }

    public List<Transaction> getSellerTransactionHistory(Long sellerId) {
        return transactionRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);
    }

    public List<Transaction> getUserTransactionHistory(Long userId) {
        List<Transaction> buyerTransactions = getBuyerTransactionHistory(userId);
        List<Transaction> sellerTransactions = getSellerTransactionHistory(userId);
        
        // Combine and sort by date
        buyerTransactions.addAll(sellerTransactions);
        buyerTransactions.sort((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()));
        
        return buyerTransactions;
    }

    public List<Transaction> getTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status);
    }

    public List<Transaction> getTransactionsByItem(String itemId) {
        return transactionRepository.findByItemIdOrderByCreatedAtDesc(itemId);
    }

    // === MESSAGING ===
    public Transaction addBuyerMessage(Long transactionId, String message, Long buyerId) {
        return transactionRepository.findById(transactionId)
            .map(transaction -> {
                if (!transaction.getBuyer().getId().equals(buyerId)) {
                    throw new RuntimeException("Only buyer can add buyer message");
                }
                
                transaction.setBuyerMessage(message);
                return transactionRepository.save(transaction);
            })
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public Transaction addSellerMessage(Long transactionId, String message, Long sellerId) {
        return transactionRepository.findById(transactionId)
            .map(transaction -> {
                if (!transaction.getSeller().getId().equals(sellerId)) {
                    throw new RuntimeException("Only seller can add seller message");
                }
                
                transaction.setSellerMessage(message);
                return transactionRepository.save(transaction);
            })
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    // === UTILITY METHODS ===
    private String generateTransactionReference() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public Optional<Transaction> getTransactionByReference(String reference) {
        return transactionRepository.findByTransactionReference(reference);
    }

    public List<Transaction> getPendingPayments(int hoursOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(hoursOld);
        return transactionRepository.findPendingPayments(cutoff);
    }

    public List<Transaction> getTransactionsRequiringPickup() {
        return transactionRepository.findTransactionsRequiringPickup();
    }

    public long getTransactionCountByStatus(TransactionStatus status) {
        return transactionRepository.countByStatus(status);
    }

    // === RECEIPT GENERATION ===
    public String generateTransactionReceipt(Long transactionId) {
        return transactionRepository.findById(transactionId)
            .map(transaction -> {
                StringBuilder receipt = new StringBuilder();
                receipt.append("HOUSE TREASURE TRANSACTION RECEIPT\n");
                receipt.append("=====================================\n");
                receipt.append("Transaction ID: ").append(transaction.getId()).append("\n");
                receipt.append("Reference: ").append(transaction.getTransactionReference()).append("\n");
                receipt.append("Date: ").append(transaction.getCreatedAt()).append("\n");
                receipt.append("Amount: RWF ").append(transaction.getAmount()).append("\n");
                receipt.append("Status: ").append(transaction.getStatus()).append("\n");
                receipt.append("Payment Method: ").append(transaction.getPaymentMethod()).append("\n");
                receipt.append("Buyer: ").append(transaction.getBuyer().getUsername()).append("\n");
                receipt.append("Seller: ").append(transaction.getSeller().getUsername()).append("\n");
                receipt.append("=====================================\n");
                
                return receipt.toString();
            })
            .orElse("Transaction not found");
    }
}
