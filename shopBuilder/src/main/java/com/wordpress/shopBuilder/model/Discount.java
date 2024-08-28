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
public class Discount {

    @Id
    @Column(name = "id_discount")
    private int idDiscount;
    private String code;
    private String discountType; // e.g., 'percent'
    private String amount;
    private boolean individualUse;
    private boolean excludeSaleItems;
    private String minimumAmount;
    private String description;
    private int usageLimit;
    private LocalDateTime dateExpires;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "discounts")
    private Set<Product> products;

    // Getters and Setters
}
