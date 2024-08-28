package com.wordpress.shopBuilder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackDto {
    private String name;
    private String type;
    private String description;
    private String short_description;
    private List<PackImageDto> images;
    private List<Long> grouped_products; // Product IDs
    private List<MetaDataDto> meta_data;
}