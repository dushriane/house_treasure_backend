package com.housetreasure.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.housetreasure.model.Item;
import com.housetreasure.repository.ItemRepository;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final CategoryService categoryService;

    public ItemService(ItemRepository itemRepository, CategoryService categoryService) {
        this.itemRepository = itemRepository;
        this.categoryService = categoryService;
    }

    // === BASIC CRUD OPERATIONS ===
    
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Page<Item> getAllItemsPaginated(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    public Page<Item> getItemsByCategoryPaginated(String categoryId, Pageable pageable) {
        return itemRepository.findByCategoryIdAndStatus(categoryId, "AVAILABLE", pageable);
    }

    public Item createItem(Item item) {
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        item.setStatus("AVAILABLE");
        item.setViews(0);
        
        Item savedItem = itemRepository.save(item);
        
        // Increment category item count
        if (item.getCategoryId() != null) {
            categoryService.incrementItemCount(Long.valueOf(item.getCategoryId()));
        }
        
        return savedItem;
    }

    public Item updateItem(String id, Item updatedItem) {
        return itemRepository.findById(id)
            .map(item -> {
                item.setTitle(updatedItem.getTitle());
                item.setDescription(updatedItem.getDescription());
                item.setPrice(updatedItem.getPrice());
                item.setCondition(updatedItem.getCondition());
                item.setImageUrls(updatedItem.getImageUrls());
                item.setTags(updatedItem.getTags());
                item.setUpdatedAt(LocalDateTime.now());
                return itemRepository.save(item);
            })
            .orElse(null);
    }

    public void deleteItem(String id) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isPresent()) {
            // Decrement category item count
            if (item.get().getCategoryId() != null) {
                categoryService.decrementItemCount(Long.valueOf(item.get().getCategoryId()));
            }
            itemRepository.deleteById(id);
        }
    }

    public Optional<Item> getItemById(String id) {
        return itemRepository.findById(id);
    }

    // === STATUS MANAGEMENT ===
    public Item markAsSold(String id) {
        return updateItemStatus(id, "SOLD");
    }

    public Item markAsReserved(String id) {
        return updateItemStatus(id, "RESERVED");
    }

    public Item markAsAvailable(String id) {
        return updateItemStatus(id, "AVAILABLE");
    }

    private Item updateItemStatus(String id, String status) {
        return itemRepository.findById(id)
            .map(item -> {
                item.setStatus(status);
                item.setUpdatedAt(LocalDateTime.now());
                return itemRepository.save(item);
            })
            .orElse(null);
    }

    // === SEARCH AND FILTER ===
    public List<Item> searchItemsByKeyword(String keyword) {
        return itemRepository.searchByKeyword(keyword);
    }

    public List<Item> searchByLocation(String location) {
        return itemRepository.findByLocationContaining(location);
    }

    public List<Item> searchByPriceRange(Double minPrice, Double maxPrice) {
        return itemRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public Page<Item> searchAvailableItems(String keyword, Double minPrice, Double maxPrice, Pageable pageable) {
        return itemRepository.searchAvailableItems(keyword, minPrice, maxPrice, pageable);
    }

    public List<Item> filterByCategory(String categoryId) {
        return itemRepository.findByStatusAndCategoryId("AVAILABLE", categoryId);
    }

    public List<Item> filterByCondition(String condition) {
        return itemRepository.findByCondition(condition);
    }

    public List<Item> filterByCategoryAndCondition(String categoryId, String condition) {
        return itemRepository.findByCategoryAndCondition(categoryId, condition);
    }

    public List<Item> filterByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return itemRepository.findByCreatedAtBetween(startDate, endDate);
    }

    // === SORTING ===
    public List<Item> sortByPriceAscending() {
        return itemRepository.findByStatusOrderByPriceAsc("AVAILABLE");
    }

    public List<Item> sortByPriceDescending() {
        return itemRepository.findByStatusOrderByPriceDesc("AVAILABLE");
    }

    public List<Item> sortByDateNewest() {
        return itemRepository.findByStatusOrderByCreatedAtDesc("AVAILABLE");
    }

    public List<Item> sortByDateOldest() {
        return itemRepository.findByStatusOrderByCreatedAtAsc("AVAILABLE");
    }

    public List<Item> sortByPopularity() {
        return itemRepository.findByStatusOrderByViewsDesc("AVAILABLE");
    }

    // === SELLER ACTIVITIES ===
    public List<Item> getItemsBySeller(String sellerId) {
        return itemRepository.findBySellerId(sellerId);
    }

    public long getActiveItemCountBySeller(String sellerId) {
        return itemRepository.countBySellerIdAndStatus(sellerId, "AVAILABLE");
    }

    // === IMAGE MANAGEMENT ===
    public Item addImage(String itemId, String imageUrl) {
        return itemRepository.findById(itemId)
            .map(item -> {
                if (item.getImageUrls() == null) {
                    item.setImageUrls(List.of(imageUrl));
                } else {
                    item.getImageUrls().add(imageUrl);
                }
                item.setUpdatedAt(LocalDateTime.now());
                return itemRepository.save(item);
            })
            .orElse(null);
    }

    public Item removeImage(String itemId, String imageUrl) {
        return itemRepository.findById(itemId)
            .map(item -> {
                if (item.getImageUrls() != null) {
                    item.getImageUrls().remove(imageUrl);
                    item.setUpdatedAt(LocalDateTime.now());
                    return itemRepository.save(item);
                }
                return item;
            })
            .orElse(null);
    }

    // === VIEWS AND STATISTICS ===
    public void incrementViews(String id) {
        itemRepository.findById(id).ifPresent(item -> {
            item.setViews(item.getViews() + 1);
            itemRepository.save(item);
        });
    }

    public Item getItemWithIncrementedViews(String id) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isPresent()) {
            incrementViews(id);
            return item.get();
        }
        return null;
    }

    // === SIMILAR ITEMS ===
    public List<Item> getSimilarItems(String itemId) {
        return itemRepository.findById(itemId)
            .map(item -> itemRepository.findSimilarItems(item.getCategoryId(), itemId))
            .orElse(List.of());
    }

    // === AVAILABILITY CHECK ===
    public boolean isItemAvailable(String id) {
        return itemRepository.findById(id)
            .map(item -> "AVAILABLE".equals(item.getStatus()))
            .orElse(false);
    }

    // === BROWSE BY CATEGORY ===
    public List<Item> browseByCategory(String categoryId) {
        return itemRepository.findByStatusAndCategoryId("AVAILABLE", categoryId);
    }

    // === ADVANCED SEARCH ===
    public Page<Item> advancedSearch(String keyword, String categoryId, String condition, 
                                         Double minPrice, Double maxPrice, String location,
                                         String sortBy, String sortDirection, Pageable pageable) {
        
        // This would require a more complex query - for now, return basic search
        return itemRepository.searchAvailableItems(keyword, minPrice, maxPrice, pageable);
    }

    // Legacy method
    public Item saveItem(Item item) {
        item.setUpdatedAt(LocalDateTime.now());
        return itemRepository.save(item);
    }
}
