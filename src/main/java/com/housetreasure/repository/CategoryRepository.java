package com.housetreasure.repository;
import com.housetreasure.model.Category;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
public interface CategoryRepository extends JpaRepository<Category, Long>  {
    // Find by name
    Optional<Category> findByName(String name);
    
    // Find active categories
    List<Category> findByIsActive(Boolean isActive);
    
    // Find root categories (no parent)
    List<Category> findByParentCategoryIsNull();
    
    // Find subcategories
    List<Category> findByParentCategoryId(Long parentId);
    
    // Search categories by name
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Category> searchByName(String name);
    
    // Find categories with item count greater than
    @Query("SELECT c FROM Category c WHERE c.itemCount > ?1")
    List<Category> findCategoriesWithItemsGreaterThan(Integer count);
    
    // Get popular categories (by view count)
    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.viewCount DESC")
    Page<Category> findPopularCategories(Pageable pageable);
    
    // Count active categories
    long countByIsActive(Boolean isActive);
}
