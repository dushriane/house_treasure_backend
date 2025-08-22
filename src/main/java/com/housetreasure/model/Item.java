package com.housetreasure.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "items")
public class Item {
    @Id
    private String id;
    private String sellerId;
    private String title;
    private String description;
    private String categoryId;
    private Double price;
    private String condition;
    private String brand;
    private String model;
    private Integer yearOfPurchase;
    private String originalReceipt; // URL to receipt image

    // Item Images table in db 
    private List<String> imageUrls;

    // Item status
    private String status;

    // Negotiation settings
    private Boolean isNegotiable = true;

    // Timestamps
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private LocalDateTime soldAt;

    private Integer views;
    private List<String> tags;

// Constructors
    public Item() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.views = 0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSellerId() {
        return sellerId;
    }
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public String getCondition() {
        return condition;
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public Integer getYearOfPurchase() {
        return yearOfPurchase;
    }
    public void setYearOfPurchase(Integer yearOfPurchase) {
        this.yearOfPurchase = yearOfPurchase;
    }
    public String getOriginalReceipt() {
        return originalReceipt;
    }
    public void setOriginalReceipt(String originalReceipt) {
        this.originalReceipt = originalReceipt;
    }
    public List<String> getImageUrls() {
        return imageUrls;
    }
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Boolean getIsNegotiable() {
        return isNegotiable;
    }
    public void setIsNegotiable(Boolean isNegotiable) {
        this.isNegotiable = isNegotiable;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public LocalDateTime getSoldAt() {
        return soldAt;
    }
    public void setSoldAt(LocalDateTime soldAt) {
        this.soldAt = soldAt;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    
}
