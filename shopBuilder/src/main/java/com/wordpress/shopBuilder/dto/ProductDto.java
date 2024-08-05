package com.wordpress.shopBuilder.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private String name;
    private String type;
    private String regular_price;
    private String description;
    private String short_description;
    private List<ProductCategoryDto> categories;
    private List<ProductImageDto> images;
}
