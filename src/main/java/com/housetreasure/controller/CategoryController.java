package com.housetreasure.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.housetreasure.service.CategoryService;
import com.housetreasure.model.Category;;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController (CategoryService categoryService){
        this.categoryService = categoryService;
    }

    // === PUBLIC/USER ENDPOINTS ===
    @GetMapping
    public List<Category> getAllActiveCategories() {
        return categoryService.getAllActiveCategories();
    }
    
    @GetMapping("/root")
    public List<Category> getRootCategories() {
        return categoryService.getRootCategories();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/subcategories")
    public List<Category> getSubcategories(@PathVariable Long id) {
        return categoryService.getSubcategories(id);
    }
    
    @GetMapping("/search")
    public List<Category> searchCategories(@RequestParam String name) {
        return categoryService.searchCategories(name);
    }
    
    @GetMapping("/popular")
    public Page<Category> getPopularCategories(Pageable pageable) {
        return categoryService.getPopularCategories(pageable);
    }
    
    @PostMapping("/{id}/view")
    public ResponseEntity<String> incrementViewCount(@PathVariable Long id) {
        categoryService.incrementViewCount(id);
        return ResponseEntity.ok("View count incremented");
    }

    // === ADMIN ENDPOINTS ===
    @GetMapping("/admin/all")
    public List<Category> getAllCategoriesAdmin() {
        return categoryService.getAllCategories();
    }
    
    @PostMapping("/admin")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        try {
            Category created = categoryService.createCategory(category);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/admin/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, 
                                                  @RequestBody Category category) {
        try {
            Category updated = categoryService.updateCategory(id, category);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Category deactivated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/admin/{id}/hard")
    public ResponseEntity<String> hardDeleteCategory(@PathVariable Long id) {
        try {
            categoryService.hardDeleteCategory(id);
            return ResponseEntity.ok("Category permanently deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/admin/active")
    public List<Category> getActiveCategories() {
        return categoryService.getActiveCategories();
    }
    
    @GetMapping("/admin/inactive")
    public List<Category> getInactiveCategories() {
        return categoryService.getInactiveCategories();
    }
    
    @PutMapping("/admin/{id}/restore")
    public ResponseEntity<Category> restoreCategory(@PathVariable Long id) {
        try {
            Category restored = categoryService.restoreCategory(id);
            return ResponseEntity.ok(restored);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/admin/stats")
    public Map<String, Long> getCategoryStats() {
        return Map.of(
            "totalCategories", categoryService.getTotalCategoriesCount(),
            "activeCategories", categoryService.getActiveCategoriesCount()
        );
    }
    
    @GetMapping("/admin/export")
    public List<Category> exportCategoryData() {
        return categoryService.getAllCategories();
    }

    // === LEGACY ENDPOINTS (for backward compatibility) ===
    @PostMapping
    public Category saveCategory(@RequestBody Category category) {
        return categoryService.saveCategory(category);
    }
}
