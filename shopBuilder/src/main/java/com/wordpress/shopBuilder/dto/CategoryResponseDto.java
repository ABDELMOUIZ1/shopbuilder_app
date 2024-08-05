package com.wordpress.shopBuilder.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto {
    private Long idCategory;
    private String name;
    private String slug;
    private String description;
}