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
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduct;

    private String productName;
    private String description;
    private double price;
    private int stock;
    private String instructions;

    @ManyToOne
    @JoinColumn(name = "website_content_id")
    private WebsiteContent websiteContent;

    @ManyToMany
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private Set<String> productImages;

    @ManyToMany(mappedBy = "products")
    private Set<Pack> packs;

    @ManyToMany
    @JoinTable(
            name = "product_discount",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "discount_id")
    )
    private Set<Discount> discounts;

    // Getters and Setters
}
