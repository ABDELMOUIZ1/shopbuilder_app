package com.wordpress.shopBuilder.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordpress.shopBuilder.dto.CategoryDto;
import com.wordpress.shopBuilder.dto.CategoryResponseDto;
import com.wordpress.shopBuilder.model.Category;
import com.wordpress.shopBuilder.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    private final WebClient webClient;

    @Autowired
    public CategoryService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost").build();
    }

    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public ResponseEntity<String> addCategory(CategoryDto categoryDto, String wpToken) throws IOException {
        System.out.println("service");
        String url = "/wordpress/wp-json/wc/v3/products/categories";

        Mono<String> responseMono = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + wpToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(categoryDto)
                .retrieve()
                .bodyToMono(String.class);

        String response = responseMono.block();

        // Parse the response to get the ID
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response);
        System.out.println(jsonResponse);
        long categoryId = jsonResponse.get("id").asLong();

        // Save the new category with ID to the database
        Category category = new Category();
        category.setIdCategory(categoryId);
        convertToEntity(category, categoryDto);

        System.out.println(category);
        categoryRepository.save(category);

        return ResponseEntity.ok(response);
    }

    private void convertToEntity(Category category, CategoryDto categoryDto) {
        category.setCategoryName(categoryDto.getName());
        category.setSlug(categoryDto.getSlug());
        category.setDescription(categoryDto.getDescription());
    }

    private CategoryResponseDto convertToDto(Category category) {
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setIdCategory(category.getIdCategory());
        categoryResponseDto.setName(category.getCategoryName());
        categoryResponseDto.setSlug(category.getSlug());
        categoryResponseDto.setDescription(category.getDescription());
        return categoryResponseDto;
    }
}
