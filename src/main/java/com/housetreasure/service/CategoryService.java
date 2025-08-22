package com.housetreasure.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.housetreasure.model.Category;
import com.housetreasure.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    // === REGULAR USER ACTIVITIES ===
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByIsActive(true);
    }
    
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentCategoryIsNull()
                .stream()
                .filter(category -> category.getIsActive())
                .toList();
    }
    
    public List<Category> getSubcategories(Long parentId) {
        return categoryRepository.findByParentCategoryId(parentId)
                .stream()
                .filter(category -> category.getIsActive())
                .toList();
    }
    
    public List<Category> searchCategories(String name) {
        return categoryRepository.searchByName(name)
                .stream()
                .filter(category -> category.getIsActive())
                .toList();
    }
    
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .filter(category -> category.getIsActive());
    }
    
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .filter(category -> category.getIsActive());
    }
    
    public Page<Category> getPopularCategories(Pageable pageable) {
        return categoryRepository.findPopularCategories(pageable);
    }
    
    public void incrementViewCount(Long categoryId) {
        categoryRepository.findById(categoryId).ifPresent(category -> {
            category.setViewCount(category.getViewCount() + 1);
            categoryRepository.save(category);
        });
    }
    
    public void incrementItemCount(Long categoryId) {
        categoryRepository.findById(categoryId).ifPresent(category -> {
            category.setItemCount(category.getItemCount() + 1);
            categoryRepository.save(category);
        });
    }
    
    public void decrementItemCount(Long categoryId) {
        categoryRepository.findById(categoryId).ifPresent(category -> {
            if (category.getItemCount() > 0) {
                category.setItemCount(category.getItemCount() - 1);
                categoryRepository.save(category);
            }
        });
    }

    // === ADMIN ACTIVITIES ===
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public Category createCategory(Category category) {
        // Validate parent category exists if specified
        if (category.getParentCategory() != null) {
            Optional<Category> parent = categoryRepository.findById(category.getParentCategory().getId());
            if (parent.isEmpty()) {
                throw new RuntimeException("Parent category not found");
            }
        }
        
        // Check if category name already exists
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new RuntimeException("Category with this name already exists");
        }
        return categoryRepository.save(category);
    }
    
    public Category updateCategory(Long id, Category updatedCategory) {
        return categoryRepository.findById(id).map(category -> {
            category.setName(updatedCategory.getName());
            category.setDescription(updatedCategory.getDescription());
            category.setIconUrl(updatedCategory.getIconUrl());
            category.setParentCategory(updatedCategory.getParentCategory());
            category.setIsActive(updatedCategory.getIsActive());
            return categoryRepository.save(category);
        }).orElseThrow(() -> new RuntimeException("Category not found"));
    }
    
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Check if category has subcategories
        List<Category> subcategories = categoryRepository.findByParentCategoryId(id);
        if (!subcategories.isEmpty()) {
            throw new RuntimeException("Cannot delete category with subcategories");
        }
        
        // Soft delete - mark as inactive instead of hard delete
        category.setIsActive(false);
        categoryRepository.save(category);
    }
    
    public void hardDeleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
    
    public List<Category> getActiveCategories() {
        return categoryRepository.findByIsActive(true);
    }
    
    public List<Category> getInactiveCategories() {
        return categoryRepository.findByIsActive(false);
    }
    
    public Category restoreCategory(Long id) {
        return categoryRepository.findById(id).map(category -> {
            category.setIsActive(true);
            return categoryRepository.save(category);
        }).orElseThrow(() -> new RuntimeException("Category not found"));
    }
    
    public long getTotalCategoriesCount() {
        return categoryRepository.count();
    }
    
    public long getActiveCategoriesCount() {
        return categoryRepository.countByIsActive(true);
    }
    
    // Legacy method for backward compatibility
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
}
