package com.housetreasure.controller;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.housetreasure.model.Item;
import com.housetreasure.service.ItemService;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ItemController {
    private final ItemService itemService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ItemController(ItemService itemService){
        this.itemService = itemService;
    }

    // === BASIC CRUD ===
    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    // === PAGINATED ENDPOINTS FOR INFINITE SCROLL ===
    @GetMapping("/paginated")
    public ResponseEntity<Map<String, Object>> getItemsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String sortBy) {
        
        try {
            Page<Item> itemsPage;
            Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            
            if (category != null && !category.isEmpty()) {
                itemsPage = itemService.getItemsByCategoryPaginated(category, pageable);
            } else {
                itemsPage = itemService.getAllItemsPaginated(pageable);
            }
            
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("items", itemsPage.getContent());
            response.put("currentPage", itemsPage.getNumber());
            response.put("totalItems", itemsPage.getTotalElements());
            response.put("totalPages", itemsPage.getTotalPages());
            response.put("hasNext", itemsPage.hasNext());
            response.put("hasPrevious", itemsPage.hasPrevious());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/feed")
    public ResponseEntity<Map<String, Object>> getItemsFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        
        try {
            Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page, size, 
                org.springframework.data.domain.Sort.by("createdAt").descending()
            );
            Page<Item> itemsPage = itemService.getAllItemsPaginated(pageable);
            
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("items", itemsPage.getContent());
            response.put("currentPage", itemsPage.getNumber());
            response.put("totalItems", itemsPage.getTotalElements());
            response.put("totalPages", itemsPage.getTotalPages());
            response.put("hasNext", itemsPage.hasNext());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable String id) {
        Item item = itemService.getItemWithIncrementedViews(id);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Item> createItem(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("category") String category,
            @RequestParam("condition") String condition,
            @RequestParam("location") String location,
            @RequestParam(value = "tags", required = false) String tagsJson,
            @RequestParam(value = "newImages", required = false) MultipartFile[] images) {
        
        try {
            // Create Item object
            Item item = new Item();
            item.setTitle(title);
            item.setDescription(description);
            item.setPrice(price);
            item.setCategoryId(category);
            item.setCondition(condition);
            item.setLocation(location.trim());

            // Initialize lists to prevent null issues
            item.setImageUrls(new ArrayList<>());
            item.setTags(new ArrayList<>());
            
            // Parse tags from JSON string
            if (tagsJson != null && !tagsJson.isEmpty()) {
                List<String> tags = objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {});
                item.setTags(tags);
                 System.out.println("Parsed tags: " + tags);
            }
            
             // Handle image uploads (for now, just log them)
            if (images != null && images.length > 0) {
                System.out.println("Processing " + images.length + " images:");
                for (int i = 0; i < images.length; i++) {
                    MultipartFile image = images[i];
                    System.out.println("  Image " + i + ": " + 
                        image.getOriginalFilename() + 
                        " (" + image.getSize() + " bytes, " + 
                        image.getContentType() + ")");
                }
                // TODO: Implement file upload service to save images and get URLs
                // For now, just set empty list
            }
            
            System.out.println("Saving item to database...");
            Item savedItem = itemService.createItem(item);
            System.out.println("Item created successfully with ID: " + savedItem.getId());

            return ResponseEntity.ok(savedItem);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Item> updateItem(
            @PathVariable String id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("category") String category,
            @RequestParam("condition") String condition,
            @RequestParam("location") String location,
            @RequestParam(value = "tags", required = false) String tagsJson,
            @RequestParam(value = "newImages", required = false) MultipartFile[] images,
            @RequestParam(value = "deletedImages", required = false) String deletedImagesJson) {
        
        try {
            // Create Item object
            Item item = new Item();
            item.setTitle(title);
            item.setDescription(description);
            item.setPrice(price);
            item.setCategoryId(category);
            item.setCondition(condition);
            item.setLocation(location.trim());
            
            // Parse tags from JSON string
            if (tagsJson != null && !tagsJson.trim().isEmpty()) {
                try {
                    List<String> tags = objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {});
                    item.setTags(tags);
                    System.out.println("Parsed tags: " + tags);
                } catch (Exception e) {
                    System.err.println("Failed to parse tags JSON: " + e.getMessage());
                }
            }
            
            // Handle deleted images
            if (deletedImagesJson != null && !deletedImagesJson.isEmpty()) {
                List<String> deletedImages = objectMapper.readValue(deletedImagesJson, new TypeReference<List<String>>() {});
                System.out.println("Deleted images: " + deletedImages);
                // TODO: Implement deletion of images
            }
            
            // Handle new image uploads
            if (images != null && images.length > 0) {
                System.out.println("Received " + images.length + " new images");
                // TODO: Implement file upload service
            }
            
            Item updated = itemService.updateItem(id, item);
            return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // JSON-only create endpoint (for backward compatibility)
    @PostMapping(value = "/json", consumes = {"application/json"})
    public ResponseEntity<Item> createItemJson(@RequestBody Item item) {
        try {
            Item savedItem = itemService.createItem(item);
            return ResponseEntity.ok(savedItem);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // JSON-only update endpoint (for backward compatibility)
    @PutMapping(value = "/{id}/json", consumes = {"application/json"})
    public ResponseEntity<Item> updateItemJson(@PathVariable String id, @RequestBody Item item) {
        try {
            Item updated = itemService.updateItem(id, item);
            return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
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
