package com.housetreasure.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.housetreasure.model.Transaction;
import com.housetreasure.model.Transaction.PaymentMethod;
import com.housetreasure.model.Transaction.TransactionStatus;
import com.housetreasure.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    // === BASIC OPERATIONS ===
    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @PostMapping
    public Transaction saveTransaction(@RequestBody Transaction transaction) {
        return transactionService.saveTransaction(transaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // === CREATING TRANSACTIONS ===
    @PostMapping("/create")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Map<String, Object> request) {
        try {
            Long buyerId = Long.valueOf(request.get("buyerId").toString());
            Long sellerId = Long.valueOf(request.get("sellerId").toString());
            Long itemId = Long.valueOf(request.get("itemId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            PaymentMethod paymentMethod = PaymentMethod.valueOf(request.get("paymentMethod").toString());
            String buyerPhone = (String) request.get("buyerPhone");
            String sellerPhone = (String) request.get("sellerPhone");

            Transaction transaction = transactionService.createTransaction(
                buyerId, sellerId, itemId, amount, paymentMethod, buyerPhone, sellerPhone);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // === STATUS MANAGEMENT ===
    @PutMapping("/{id}/status")
    public ResponseEntity<Transaction> updateTransactionStatus(@PathVariable Long id, 
                                                             @RequestBody Map<String, String> request) {
        try {
            TransactionStatus status = TransactionStatus.valueOf(request.get("status"));
            Transaction updated = transactionService.updateTransactionStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // === PAYMENT PROCESSING ===
    @PutMapping("/{id}/process-payment")
    public ResponseEntity<Transaction> processPayment(@PathVariable Long id, 
                                                    @RequestBody Map<String, String> request) {
        try {
            String paymentReference = request.get("paymentReference");
            Transaction updated = transactionService.processPayment(id, paymentReference);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/confirm-payment")
    public ResponseEntity<Transaction> confirmPayment(@PathVariable Long id, @RequestParam Long sellerId) {
        try {
            Transaction updated = transactionService.confirmPayment(id, sellerId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/verify-payment")
    public ResponseEntity<Transaction> verifyPayment(@PathVariable Long id, 
                                                   @RequestBody Map<String, String> request) {
        try {
            String verificationCode = request.get("verificationCode");
            Transaction updated = transactionService.verifyPayment(id, verificationCode);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // === DELIVERY MANAGEMENT ===
    @PutMapping("/{id}/delivery-info")
    public ResponseEntity<Transaction> updateDeliveryInfo(@PathVariable Long id, 
                                                        @RequestBody Map<String, Object> request) {
        try {
            String pickupLocation = (String) request.get("pickupLocation");
            LocalDateTime pickupDate = LocalDateTime.parse((String) request.get("pickupDate"));
            String instructions = (String) request.get("instructions");

            Transaction updated = transactionService.updateDeliveryInfo(id, pickupLocation, pickupDate, instructions);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/confirm-delivered")
    public ResponseEntity<Transaction> confirmItemDelivered(@PathVariable Long id, @RequestParam Long sellerId) {
        try {
            Transaction updated = transactionService.confirmItemDelivered(id, sellerId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/confirm-received")
    public ResponseEntity<Transaction> confirmItemReceived(@PathVariable Long id, @RequestParam Long buyerId) {
        try {
            Transaction updated = transactionService.confirmItemReceived(id, buyerId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // === TRANSACTION COMPLETION ===
    @PutMapping("/{id}/complete")
    public ResponseEntity<Transaction> completeTransaction(@PathVariable Long id) {
        try {
            Transaction completed = transactionService.completeTransaction(id);
            return ResponseEntity.ok(completed);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // === CANCELLATION AND REFUNDS ===
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Transaction> cancelTransaction(@PathVariable Long id, 
                                                       @RequestBody Map<String, Object> request) {
        try {
            String reason = (String) request.get("reason");
            Long userId = Long.valueOf(request.get("userId").toString());
            
            Transaction cancelled = transactionService.cancelTransaction(id, reason, userId);
            return ResponseEntity.ok(cancelled);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/refund")
    public ResponseEntity<Transaction> processRefund(@PathVariable Long id, 
                                                   @RequestBody Map<String, String> request) {
        try {
            String refundReason = request.get("refundReason");
            Transaction refunded = transactionService.processRefund(id, refundReason);
            return ResponseEntity.ok(refunded);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // === DISPUTE HANDLING ===
    @PutMapping("/{id}/report-issue")
    public ResponseEntity<Transaction> reportTransactionIssue(@PathVariable Long id, 
                                                            @RequestBody Map<String, Object> request) {
        try {
            String description = (String) request.get("description");
            Long reporterId = Long.valueOf(request.get("reporterId").toString());
            
            Transaction reported = transactionService.reportTransactionIssue(id, description, reporterId);
            return ResponseEntity.ok(reported);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // === TRANSACTION HISTORY ===
    @GetMapping("/buyer/{buyerId}")
    public List<Transaction> getBuyerTransactionHistory(@PathVariable Long buyerId) {
        return transactionService.getBuyerTransactionHistory(buyerId);
    }

    @GetMapping("/seller/{sellerId}")
    public List<Transaction> getSellerTransactionHistory(@PathVariable Long sellerId) {
        return transactionService.getSellerTransactionHistory(sellerId);
    }

    @GetMapping("/user/{userId}")
    public List<Transaction> getUserTransactionHistory(@PathVariable Long userId) {
        return transactionService.getUserTransactionHistory(userId);
    }

    @GetMapping("/status/{status}")
    public List<Transaction> getTransactionsByStatus(@PathVariable TransactionStatus status) {
        return transactionService.getTransactionsByStatus(status);
    }

    @GetMapping("/item/{itemId}")
    public List<Transaction> getTransactionsByItem(@PathVariable Long itemId) {
        return transactionService.getTransactionsByItem(itemId);
    }

    // === MESSAGING ===
    @PutMapping("/{id}/buyer-message")
    public ResponseEntity<Transaction> addBuyerMessage(@PathVariable Long id, 
                                                     @RequestBody Map<String, Object> request) {
        try {
            String message = (String) request.get("message");
            Long buyerId = Long.valueOf(request.get("buyerId").toString());
            
            Transaction updated = transactionService.addBuyerMessage(id, message, buyerId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/seller-message")
    public ResponseEntity<Transaction> addSellerMessage(@PathVariable Long id, 
                                                      @RequestBody Map<String, Object> request) {
        try {
            String message = (String) request.get("message");
            Long sellerId = Long.valueOf(request.get("sellerId").toString());
            
            Transaction updated = transactionService.addSellerMessage(id, message, sellerId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // === UTILITY ENDPOINTS ===
    @GetMapping("/reference/{reference}")
    public ResponseEntity<Transaction> getTransactionByReference(@PathVariable String reference) {
        return transactionService.getTransactionByReference(reference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending-payments")
    public List<Transaction> getPendingPayments(@RequestParam(defaultValue = "24") int hoursOld) {
        return transactionService.getPendingPayments(hoursOld);
    }

    @GetMapping("/requiring-pickup")
    public List<Transaction> getTransactionsRequiringPickup() {
        return transactionService.getTransactionsRequiringPickup();
    }

    @GetMapping("/stats/status/{status}")
    public Map<String, Long> getTransactionCountByStatus(@PathVariable TransactionStatus status) {
        return Map.of("count", transactionService.getTransactionCountByStatus(status));
    }

    // === RECEIPT GENERATION ===
    @GetMapping("/{id}/receipt")
    public ResponseEntity<Map<String, String>> generateTransactionReceipt(@PathVariable Long id) {
        String receipt = transactionService.generateTransactionReceipt(id);
        return ResponseEntity.ok(Map.of("receipt", receipt));
    }
}
