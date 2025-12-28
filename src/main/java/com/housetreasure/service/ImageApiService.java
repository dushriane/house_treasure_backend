package com.housetreasure.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class ImageApiService {
    
    private final RestTemplate restTemplate;
    
    // Note: Replace with your actual API keys in application.properties
    private static final String UNSPLASH_ACCESS_KEY = "YOUR_UNSPLASH_ACCESS_KEY";
    private static final String PEXELS_API_KEY = "YOUR_PEXELS_API_KEY";
    
    private static final String UNSPLASH_API_URL = "https://api.unsplash.com/search/photos";
    private static final String PEXELS_API_URL = "https://api.pexels.com/v1/search";
    
    public ImageApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Search images from Unsplash API
     * @param query Search term
     * @param page Page number (for pagination)
     * @param perPage Number of images per page
     * @return List of image data
     */
    public Map<String, Object> searchUnsplashImages(String query, int page, int perPage) {
        try {
            String url = String.format("%s?query=%s&page=%d&per_page=%d&client_id=%s",
                    UNSPLASH_API_URL, query, page, perPage, UNSPLASH_ACCESS_KEY);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Version", "v1");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> results = (List<Map<String, Object>>) body.get("results");
                
                List<Map<String, Object>> images = new ArrayList<>();
                for (Map<String, Object> result : results) {
                    Map<String, Object> urls = (Map<String, Object>) result.get("urls");
                    Map<String, Object> user = (Map<String, Object>) result.get("user");
                    
                    Map<String, Object> image = new HashMap<>();
                    image.put("id", result.get("id"));
                    image.put("url", urls.get("regular"));
                    image.put("thumb", urls.get("thumb"));
                    image.put("small", urls.get("small"));
                    image.put("description", result.get("description"));
                    image.put("alt_description", result.get("alt_description"));
                    image.put("photographer", user.get("name"));
                    image.put("photographer_url", user.get("links"));
                    
                    images.add(image);
                }
                
                Map<String, Object> result = new HashMap<>();
                result.put("images", images);
                result.put("total", body.get("total"));
                result.put("total_pages", body.get("total_pages"));
                result.put("page", page);
                
                return result;
            }
        } catch (Exception e) {
            System.err.println("Error fetching Unsplash images: " + e.getMessage());
        }
        
        return createEmptyResponse();
    }
    
    /**
     * Search images from Pexels API
     * @param query Search term
     * @param page Page number (for pagination)
     * @param perPage Number of images per page
     * @return List of image data
     */
    public Map<String, Object> searchPexelsImages(String query, int page, int perPage) {
        try {
            String url = String.format("%s?query=%s&page=%d&per_page=%d",
                    PEXELS_API_URL, query, page, perPage);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", PEXELS_API_KEY);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> photos = (List<Map<String, Object>>) body.get("photos");
                
                List<Map<String, Object>> images = new ArrayList<>();
                for (Map<String, Object> photo : photos) {
                    Map<String, Object> src = (Map<String, Object>) photo.get("src");
                    
                    Map<String, Object> image = new HashMap<>();
                    image.put("id", photo.get("id"));
                    image.put("url", src.get("large"));
                    image.put("thumb", src.get("tiny"));
                    image.put("small", src.get("medium"));
                    image.put("description", photo.get("alt"));
                    image.put("photographer", photo.get("photographer"));
                    image.put("photographer_url", photo.get("photographer_url"));
                    
                    images.add(image);
                }
                
                Map<String, Object> result = new HashMap<>();
                result.put("images", images);
                result.put("total", body.get("total_results"));
                result.put("page", body.get("page"));
                result.put("per_page", body.get("per_page"));
                
                return result;
            }
        } catch (Exception e) {
            System.err.println("Error fetching Pexels images: " + e.getMessage());
        }
        
        return createEmptyResponse();
    }
    
    /**
     * Get random images from Unsplash
     * @param count Number of random images to fetch
     * @return List of random images
     */
    public List<Map<String, Object>> getRandomImages(int count) {
        try {
            String url = String.format("https://api.unsplash.com/photos/random?count=%d&client_id=%s",
                    count, UNSPLASH_ACCESS_KEY);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Version", "v1");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, List.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> results = response.getBody();
                List<Map<String, Object>> images = new ArrayList<>();
                
                for (Map<String, Object> result : results) {
                    Map<String, Object> urls = (Map<String, Object>) result.get("urls");
                    Map<String, Object> user = (Map<String, Object>) result.get("user");
                    
                    Map<String, Object> image = new HashMap<>();
                    image.put("id", result.get("id"));
                    image.put("url", urls.get("regular"));
                    image.put("thumb", urls.get("thumb"));
                    image.put("description", result.get("description"));
                    image.put("photographer", user.get("name"));
                    
                    images.add(image);
                }
                
                return images;
            }
        } catch (Exception e) {
            System.err.println("Error fetching random images: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }
    
    private Map<String, Object> createEmptyResponse() {
        Map<String, Object> empty = new HashMap<>();
        empty.put("images", new ArrayList<>());
        empty.put("total", 0);
        empty.put("page", 1);
        return empty;
    }
}
