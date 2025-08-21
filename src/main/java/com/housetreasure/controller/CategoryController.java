package com.housetreasure.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.housetreasure.service.CategoryService;
import com.housetreasure.model.Category;;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController (CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getAllCategories(){
        return categoryService.getAllCategory();
    }

    @PostMapping
    public Category saveCategory(@RequestBody Category category){
        return categoryService.saveCategory(category);
    }
}
