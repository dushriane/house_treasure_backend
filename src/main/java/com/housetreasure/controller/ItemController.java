package com.housetreasure.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.housetreasure.model.Item;
import com.housetreasure.service.ItemService;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService){
        this.itemService = itemService;
    }

    // === BASIC CRUD ===
    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable String id) {
        Item item = itemService.getItemWithIncrementedViews(id);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Item createItem(@RequestBody Item item) {
        return itemService.createItem(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable String id, @RequestBody Item item) {
        Item updated = itemService.updateItem(id, item);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable String id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok("Item deleted successfully");
    }

    // === STATUS MANAGEMENT ===
    @PutMapping("/{id}/mark-sold")
    public ResponseEntity<Item> markAsSold(@PathVariable String id) {
        Item item = itemService.markAsSold(id);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/mark-reserved")
    public ResponseEntity<Item> markAsReserved(@PathVariable String id) {
        Item item = itemService.markAsReserved(id);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/mark-available")
    public ResponseEntity<Item> markAsAvailable(@PathVariable String id) {
        Item item = itemService.markAsAvailable(id);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    // === SEARCH AND FILTER ===
    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam String keyword) {
        return itemService.searchItemsByKeyword(keyword);
    }

    @GetMapping("/search/location")
    public List<Item> searchByLocation(@RequestParam String location) {
        return itemService.searchByLocation(location);
    }

    @GetMapping("/search/price-range")
    public List<Item> searchByPriceRange(@RequestParam Double minPrice, @RequestParam Double maxPrice) {
        return itemService.searchByPriceRange(minPrice, maxPrice);
    }

    @GetMapping("/search/advanced")
    public Page<Item> advancedSearch(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Double minPrice,
                                         @RequestParam(required = false) Double maxPrice,
                                         Pageable pageable) {
        return itemService.searchAvailableItems(keyword, minPrice, maxPrice, pageable);
    }

    @GetMapping("/filter/category/{categoryId}")
    public List<Item> filterByCategory(@PathVariable String categoryId) {
        return itemService.filterByCategory(categoryId);
    }

    @GetMapping("/filter/condition/{condition}")
    public List<Item> filterByCondition(@PathVariable String condition) {
        return itemService.filterByCondition(condition);
    }

    // === SORTING ===
    @GetMapping("/sort/price-asc")
    public List<Item> sortByPriceAscending() {
        return itemService.sortByPriceAscending();
    }

    @GetMapping("/sort/price-desc")
    public List<Item> sortByPriceDescending() {
        return itemService.sortByPriceDescending();
    }

    @GetMapping("/sort/date-newest")
    public List<Item> sortByDateNewest() {
        return itemService.sortByDateNewest();
    }

    @GetMapping("/sort/date-oldest")
    public List<Item> sortByDateOldest() {
        return itemService.sortByDateOldest();
    }

    @GetMapping("/sort/popular")
    public List<Item> sortByPopularity() {
        return itemService.sortByPopularity();
    }

    // === SELLER ACTIVITIES ===
    @GetMapping("/seller/{sellerId}")
    public List<Item> getItemsBySeller(@PathVariable String sellerId) {
        return itemService.getItemsBySeller(sellerId);
    }

    @GetMapping("/seller/{sellerId}/count")
    public Map<String, Long> getSellerItemCount(@PathVariable String sellerId) {
        return Map.of("activeItems", itemService.getActiveItemCountBySeller(sellerId));
    }

    // === IMAGE MANAGEMENT ===
    @PostMapping("/{id}/images")
    public ResponseEntity<Item> addImage(@PathVariable String id, @RequestBody Map<String, String> request) {
        String imageUrl = request.get("imageUrl");
        Item item = itemService.addImage(id, imageUrl);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<Item> removeImage(@PathVariable String id, @RequestBody Map<String, String> request) {
        String imageUrl = request.get("imageUrl");
        Item item = itemService.removeImage(id, imageUrl);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    // === SIMILAR ITEMS ===
    @GetMapping("/{id}/similar")
    public List<Item> getSimilarItems(@PathVariable String id) {
        return itemService.getSimilarItems(id);
    }

    // === AVAILABILITY CHECK ===
    @GetMapping("/{id}/availability")
    public Map<String, Boolean> checkAvailability(@PathVariable String id) {
        return Map.of("available", itemService.isItemAvailable(id));
    }

    // === BROWSE BY CATEGORY ===
    @GetMapping("/browse/category/{categoryId}")
    public List<Item> browseByCategory(@PathVariable String categoryId) {
        return itemService.browseByCategory(categoryId);
    }

    // === LEGACY ENDPOINT ===
    @PostMapping("/save")
    public Item saveItem(@RequestBody Item item) {
        return itemService.saveItem(item);
    }
}
