package com.housetreasure.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.housetreasure.service.ImageApiService;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ImageApiController {
    
    private final ImageApiService imageApiService;
    
    public ImageApiController(ImageApiService imageApiService) {
        this.imageApiService = imageApiService;
    }
    
    /**
     * Search images from Unsplash
     * GET /api/images/search/unsplash?query=house&page=1&perPage=20
     */
    @GetMapping("/search/unsplash")
    public ResponseEntity<Map<String, Object>> searchUnsplash(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int perPage) {
        
        Map<String, Object> result = imageApiService.searchUnsplashImages(query, page, perPage);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Search images from Pexels
     * GET /api/images/search/pexels?query=house&page=1&perPage=20
     */
    @GetMapping("/search/pexels")
    public ResponseEntity<Map<String, Object>> searchPexels(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int perPage) {
        
        Map<String, Object> result = imageApiService.searchPexelsImages(query, page, perPage);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get random images from Unsplash
     * GET /api/images/random?count=10
     */
    @GetMapping("/random")
    public ResponseEntity<List<Map<String, Object>>> getRandomImages(
            @RequestParam(defaultValue = "10") int count) {
        
        List<Map<String, Object>> images = imageApiService.getRandomImages(count);
        return ResponseEntity.ok(images);
    }
    
    /**
     * Search images with preference (tries Unsplash first, fallback to mock data)
     * GET /api/images/search?query=house&page=1&size=20
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchImages(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Try Unsplash first
        Map<String, Object> result = imageApiService.searchUnsplashImages(query, page, size);
        
        // If no results, you could fallback to other sources or return empty
        return ResponseEntity.ok(result);
    }
}
