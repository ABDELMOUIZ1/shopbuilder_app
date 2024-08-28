package com.wordpress.shopBuilder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountDto {
    private String code;
    private String discount_type; // e.g., 'percent'
    private String amount;
    private boolean individual_use;
    private boolean exclude_sale_items;
    private String minimum_amount;
    private List<Integer> product_ids;
    private String description;
    private int usage_limit;
    private LocalDateTime date_expires;
}
