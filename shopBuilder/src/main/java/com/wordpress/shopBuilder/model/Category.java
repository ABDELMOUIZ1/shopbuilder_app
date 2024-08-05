package com.wordpress.shopBuilder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Category {

    @Id
    @Column(name = "id_category")
    private Long idCategory;

    private String categoryName;
    private String slug;
    private String description;

    @ManyToOne
    @JoinColumn(name = "website_content_id")
    private WebsiteContent websiteContent;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "categories")
    private Set<Product> products;

    // Getters and Setters
}
