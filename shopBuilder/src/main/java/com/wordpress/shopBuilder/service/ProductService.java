package com.wordpress.shopBuilder.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordpress.shopBuilder.config.JwtUtil;
import com.wordpress.shopBuilder.dto.*;
import com.wordpress.shopBuilder.model.Category;
import com.wordpress.shopBuilder.model.Product;
import com.wordpress.shopBuilder.repository.CategoryRepository;
import com.wordpress.shopBuilder.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final WebClient webClient;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    public ProductService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public ResponseEntity<String> addProduct(ProductDto productDto, String wpToken) throws IOException {
        // Decode the JWT to get the domain name
        JsonNode decodedJwt = JwtUtil.decodeJwt(wpToken);
        String domaineName = decodedJwt.get("iss").asText(); // Extract the "iss" field


        System.out.println("service");
        String url = domaineName + "/wp-json/wc/v3/products";

        Mono<String> responseMono = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + wpToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(productDto)
                .retrieve()
                .bodyToMono(String.class);

        String response = responseMono.block();

        // Parse the response to get the ID
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response);
        long productId = jsonResponse.get("id").asLong();

        // Save the new category with ID to the database
        Product product = new Product();
        product.setIdProduct(productId);
        convertToEntity(product, productDto);
        System.out.println("wa tongooooooooooooooo service");
        System.out.println("wa tongooooooooooooooo service");
        System.out.println("wa tongooooooooooooooo service");
        System.out.println(product);
        productRepository.save(product);

        return ResponseEntity.ok(response);
    }

    private void convertToEntity(Product product, ProductDto productDto) {
        product.setProductName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(Double.parseDouble(productDto.getRegular_price()));
        product.setStock(0); // Set stock appropriately
        product.setInstructions(""); // Set instructions appropriately

        // Set categories
        Set<Category> categories = new HashSet<>();
        for (ProductCategoryDto categoryDto : productDto.getCategories()) {
            Category category = categoryRepository.findById((long) categoryDto.getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            categories.add(category);
        }
        product.setCategories(categories);

        // Set images
        Set<String> images = new HashSet<>();
        for (ProductImageDto imageDto : productDto.getImages()) {
            images.add(imageDto.getSrc());
        }
        product.setProductImages(images);
    }

    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private ProductResponseDto convertToDto(Product product) {
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setIdProduct(product.getIdProduct());
        productResponseDto.setProductName(product.getProductName());
        return productResponseDto;
    }
}
