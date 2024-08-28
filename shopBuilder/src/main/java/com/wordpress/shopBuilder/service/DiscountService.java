
package com.wordpress.shopBuilder.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordpress.shopBuilder.config.JwtUtil;
import com.wordpress.shopBuilder.dto.DiscountDto;
import com.wordpress.shopBuilder.model.Discount;
import com.wordpress.shopBuilder.model.Product;
import com.wordpress.shopBuilder.repository.DiscountRepository;
import com.wordpress.shopBuilder.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductRepository productRepository;

    private final WebClient webClient;

    @Autowired
    public DiscountService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public ResponseEntity<String> addDiscount(DiscountDto discountDto, String wpToken) throws IOException {
        // Decode the JWT to get the domain name
        JsonNode decodedJwt = JwtUtil.decodeJwt(wpToken);
        String domaineName = decodedJwt.get("iss").asText(); // Extract the "iss" field


        System.out.println("service");
        String url = domaineName + "/wp-json/wc/v3/coupons";

        Mono<ResponseEntity<String>> responseMono = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + wpToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(discountDto)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody)))
                )
                .toEntity(String.class);

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = responseMono.block();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        // Parse the response to get the ID
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(responseEntity.getBody());
        int discountId = jsonResponse.get("id").asInt();

        // Optionally save to local database
        Discount discount = new Discount();
        discount.setIdDiscount(discountId);
        convertToEntity(discount, discountDto);
        discountRepository.save(discount);

        return ResponseEntity.ok(responseEntity.getBody());
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
