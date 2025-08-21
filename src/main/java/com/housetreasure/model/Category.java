package com.housetreasure.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private String iconUrl;

    // For hierarchical categories (e.g., Electronics > Phones > Smartphones)
    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    private Boolean isActive = true;

    // Category statistics
    private Integer itemCount = 0;
    private Integer viewCount = 0;
}

