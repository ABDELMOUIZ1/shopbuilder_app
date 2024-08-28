package com.wordpress.shopBuilder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Pack {

    @Id
    @Column(name = "id_pack")
    private Long idPack;
    private String type; // 'grouped'
    private String name;
    private String description;
    private String shortDescription;
    private double price;


    @ElementCollection
    @CollectionTable(name = "pack_images", joinColumns = @JoinColumn(name = "id_pack"))
    @Column(name = "image_url")
    private Set<String> packImages;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "pack_product",
            joinColumns = @JoinColumn(name = "pack_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products;


    // Getters and Setters
}
