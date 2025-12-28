package com.housetreasure.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.housetreasure.model.Item;

public interface ItemRepository extends MongoRepository<Item, String> {
    // Search by seller
    List<Item> findBySellerId(String sellerId);
    
    // Search by category
    List<Item> findByCategoryId(String categoryId);
    
    // Search by status
    List<Item> findByStatus(String status);
    
    // Search by condition
    List<Item> findByCondition(String condition);
    
    // Search by keyword (name and description)
    @Query("{'$or': [" +
           "{'name': {$regex: ?0, $options: 'i'}}, " +
           "{'description': {$regex: ?0, $options: 'i'}}, " +
           "{'tags': {$regex: ?0, $options: 'i'}}" +
           "]}")
    List<Item> searchByKeyword(String keyword);
    
    // Search by price range
    List<Item> findByPriceBetween(Double minPrice, Double maxPrice);
    
    // Search by location
    List<Item> findByLocationContaining(String location);
    
    // Search by date range
    List<Item> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Combined search with filters
    @Query("{'categoryId': ?0, 'condition': ?1, 'status': 'AVAILABLE'}")
    List<Item> findByCategoryAndCondition(String categoryId, String condition);
    
    // Get available items only
    List<Item> findByStatusAndCategoryId(String status, String categoryId);
    
    // Paginated query for category
    Page<Item> findByCategoryIdAndStatus(String categoryId, String status, Pageable pageable);
    
    // Sort by price (ascending)
    List<Item> findByStatusOrderByPriceAsc(String status);
    
    // Sort by price (descending)
    List<Item> findByStatusOrderByPriceDesc(String status);
    
    // Sort by date (newest first)
    List<Item> findByStatusOrderByCreatedAtDesc(String status);
    
    // Sort by date (oldest first)
    List<Item> findByStatusOrderByCreatedAtAsc(String status);
    
    // Get popular items (by views)
    List<Item> findByStatusOrderByViewsDesc(String status);
    
    // Complex search query
    @Query("{'$and': [" +
           "{'status': 'AVAILABLE'}, " +
           "{'$or': [" +
           "{'name': {$regex: ?0, $options: 'i'}}, " +
           "{'description': {$regex: ?0, $options: 'i'}}" +
           "]}, " +
           "{'price': {$gte: ?1, $lte: ?2}}" +
           "]}")
    Page<Item> searchAvailableItems(String keyword, Double minPrice, Double maxPrice, Pageable pageable);
    
    // Count items by seller
    long countBySellerIdAndStatus(String sellerId, String status);
    
    // Find similar items (same category, excluding current item)
    @Query("{'categoryId': ?0, '_id': {$ne: ?1}, 'status': 'AVAILABLE'}")
    List<Item> findSimilarItems(String categoryId, String excludeId);
}
