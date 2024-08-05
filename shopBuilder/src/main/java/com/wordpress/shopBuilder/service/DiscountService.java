package com.wordpress.shopBuilder.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordpress.shopBuilder.dto.DiscountDto;
import com.wordpress.shopBuilder.model.Discount;
import com.wordpress.shopBuilder.model.Product;
import com.wordpress.shopBuilder.repository.DiscountRepository;
import com.wordpress.shopBuilder.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductRepository productRepository;

    private final WebClient webClient;

    @Autowired
    public DiscountService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost").build();
    }

    public ResponseEntity<String> addDiscount(DiscountDto discountDto, String wpToken) throws JsonProcessingException {
        String url = "/wordpress/wp-json/wc/v3/coupons";

        Mono<String> responseMono = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + wpToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(discountDto)
                .retrieve()
                .bodyToMono(String.class);

        String response = responseMono.block();

        // Parse the response to get the ID
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response);
        int discountId = jsonResponse.get("id").asInt();

        // Optionally save to local database
        Discount discount = new Discount();
        discount.setIdDiscount(discountId);
        convertToEntity(discount, discountDto);
        discountRepository.save(discount);

        return ResponseEntity.ok(response);
    }

    private void convertToEntity(Discount discount, DiscountDto discountDto) {
        discount.setCode(discountDto.getCode());
        discount.setDiscountType(discountDto.getDiscount_type());
        discount.setAmount(discountDto.getAmount());
        discount.setIndividualUse(discountDto.isIndividual_use());
        discount.setExcludeSaleItems(discountDto.isExclude_sale_items());
        discount.setMinimumAmount(discountDto.getMinimum_amount());
        discount.setDescription(discountDto.getDescription());
        discount.setUsageLimit(discountDto.getUsage_limit());
        discount.setDateCreated(discountDto.getDate_created());
        discount.setDateExpires(discountDto.getDate_expires());

        // Fetch products based on IDs and set to discount
        Set<Product> products = new HashSet<>();
        if (discountDto.getProduct_ids() != null) {
            products = discountDto.getProduct_ids().stream()
                    .map(productId -> productRepository.findById(Long.valueOf(productId)).orElse(null))
                    .collect(Collectors.toSet());
        }
        discount.setProducts(products);
    }
}
