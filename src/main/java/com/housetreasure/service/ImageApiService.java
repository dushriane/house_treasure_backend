package com.housetreasure.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImageApiService {

    @Autowired
    private RestTemplate restTemplate;

    // API Keys - Replace with your actual keys
    private static final String UNSPLASH_ACCESS_KEY = "YOUR_UNSPLASH_ACCESS_KEY";
    private static final String PEXELS_API_KEY = "YOUR_PEXELS_API_KEY";

    // Unsplash API endpoints
    private static final String UNSPLASH_SEARCH_URL = "https://api.unsplash.com/search/photos";
    private static final String UNSPLASH_RANDOM_URL = "https://api.unsplash.com/photos/random";

    // Pexels API endpoints
    private static final String PEXELS_SEARCH_URL = "https://api.pexels.com/v1/search";

    /**
     * Search images from Unsplash with pagination
     */
    public Map<String, Object> searchUnsplashImages(String query, int page, int perPage) {
        try {
            String url = String.format("%s?query=%s&page=%d&per_page=%d&client_id=%s",
                    UNSPLASH_SEARCH_URL, query, page, perPage, UNSPLASH_ACCESS_KEY);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("results")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> results = (List<Map<String, Object>>) body.get("results");
                
                List<Map<String, Object>> images = results.stream().map(photo -> {
                    Map<String, Object> imageData = new HashMap<>();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> urls = (Map<String, Object>) photo.get("urls");
                    @SuppressWarnings("unchecked")
                    Map<String, Object> user = (Map<String, Object>) photo.get("user");
                    
                    imageData.put("id", photo.get("id"));
                    imageData.put("url", urls != null ? urls.get("regular") : null);
                    imageData.put("thumbnail", urls != null ? urls.get("thumb") : null);
                    imageData.put("description", photo.get("description"));
                    imageData.put("alt_description", photo.get("alt_description"));
                    imageData.put("photographer", user != null ? user.get("name") : "Unknown");
                    imageData.put("source", "unsplash");
                    
                    return imageData;
                }).collect(Collectors.toList());

                Map<String, Object> result = new HashMap<>();
                result.put("images", images);
                result.put("total", body.get("total"));
                result.put("totalPages", body.get("total_pages"));
                result.put("currentPage", page);
                
                return result;
            }

            return Collections.emptyMap();
        } catch (Exception e) {
            System.err.println("Error fetching Unsplash images: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * Search images from Pexels with pagination
     */
    public Map<String, Object> searchPexelsImages(String query, int page, int perPage) {
        try {
            String url = String.format("%s?query=%s&page=%d&per_page=%d",
                    PEXELS_SEARCH_URL, query, page, perPage);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", PEXELS_API_KEY);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("photos")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> photos = (List<Map<String, Object>>) body.get("photos");
                
                List<Map<String, Object>> images = photos.stream().map(photo -> {
                    Map<String, Object> imageData = new HashMap<>();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> src = (Map<String, Object>) photo.get("src");
                    
                    imageData.put("id", photo.get("id"));
                    imageData.put("url", src != null ? src.get("large") : null);
                    imageData.put("thumbnail", src != null ? src.get("small") : null);
                    imageData.put("description", photo.get("alt"));
                    imageData.put("photographer", photo.get("photographer"));
                    imageData.put("source", "pexels");
                    
                    return imageData;
                }).collect(Collectors.toList());

                Map<String, Object> result = new HashMap<>();
                result.put("images", images);
                result.put("total", body.get("total_results"));
                result.put("totalPages", ((Integer) body.get("total_results") / perPage) + 1);
                result.put("currentPage", page);
                
                return result;
            }

            return Collections.emptyMap();
        } catch (Exception e) {
            System.err.println("Error fetching Pexels images: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * Get random images from Unsplash
     */
    public List<Map<String, Object>> getRandomImages(int count) {
        try {
            String url = String.format("%s?count=%d&client_id=%s",
                    UNSPLASH_RANDOM_URL, count, UNSPLASH_ACCESS_KEY);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            List<Map<String, Object>> photos = response.getBody();
            if (photos != null) {
                return photos.stream().map(photo -> {
                    Map<String, Object> imageData = new HashMap<>();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> urls = (Map<String, Object>) photo.get("urls");
                    @SuppressWarnings("unchecked")
                    Map<String, Object> user = (Map<String, Object>) photo.get("user");
                    
                    imageData.put("id", photo.get("id"));
                    imageData.put("url", urls != null ? urls.get("regular") : null);
                    imageData.put("thumbnail", urls != null ? urls.get("thumb") : null);
                    imageData.put("description", photo.get("description"));
                    imageData.put("alt_description", photo.get("alt_description"));
                    imageData.put("photographer", user != null ? user.get("name") : "Unknown");
                    imageData.put("source", "unsplash");
                    
                    return imageData;
                }).collect(Collectors.toList());
            }

            return Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error fetching random Unsplash images: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Unified search across both APIs
     */
    public Map<String, Object> searchImages(String query, int page, int size, String source) {
        if ("pexels".equalsIgnoreCase(source)) {
            return searchPexelsImages(query, page, size);
        } else {
            return searchUnsplashImages(query, page, size);
        }
    }
}