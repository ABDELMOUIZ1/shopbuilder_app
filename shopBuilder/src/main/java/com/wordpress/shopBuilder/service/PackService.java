package com.wordpress.shopBuilder.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordpress.shopBuilder.config.JwtUtil;
import com.wordpress.shopBuilder.dto.MetaDataDto;
import com.wordpress.shopBuilder.dto.PackDto;
import com.wordpress.shopBuilder.dto.PackImageDto;
import com.wordpress.shopBuilder.dto.ProductImageDto;
import com.wordpress.shopBuilder.model.Pack;
import com.wordpress.shopBuilder.model.Product;
import com.wordpress.shopBuilder.repository.PackRepository;
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
public class PackService {

    @Autowired
    private PackRepository packRepository;

    @Autowired
    private ProductRepository productRepository; // Add ProductRepository to load products

    private final WebClient webClient;

    @Autowired
    public PackService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public ResponseEntity<String> addPack(PackDto packDto, String wpToken) throws IOException {
        // Decode the JWT to get the domain name
        JsonNode decodedJwt = JwtUtil.decodeJwt(wpToken);
        String domaineName = decodedJwt.get("iss").asText(); // Extract the "iss" field


        System.out.println("service");
        String url = domaineName + "/wp-json/wc/v3/products";

        Mono<String> responseMono = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + wpToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(packDto)
                .retrieve()
                .bodyToMono(String.class);

        String response = responseMono.block();

        // Parse the response to get the ID and price
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response);

        long packId = jsonResponse.get("id").asLong();
        double price = extractPriceFromMetaData(jsonResponse);

        // Create and save the new pack
        Pack pack = convertDto(packDto, packId, price);

        // Handle products association
        Set<Product> products = packDto.getGrouped_products().stream()
                .map(productId -> productRepository.findById(productId).orElse(null))
                .collect(Collectors.toSet());
        pack.setProducts(products);

        packRepository.save(pack);

        return ResponseEntity.ok(response);
    }

    private double extractPriceFromMetaData(JsonNode jsonResponse) {
        for (JsonNode metaData : jsonResponse.get("meta_data")) {
            String key = metaData.get("key").asText();
            if ("prix_global".equals(key)) {
                return Double.parseDouble(metaData.get("value").asText());
            }
        }
        return 0;
    }

    private Pack convertDto(PackDto packDto, long packId, double price) {
        Pack pack = new Pack();
        pack.setIdPack(packId);
        pack.setType(packDto.getType());
        pack.setName(packDto.getName());
        pack.setDescription(packDto.getDescription());
        pack.setShortDescription(packDto.getShort_description());
        pack.setPrice(price);

        Set<String> images = packDto.getImages().stream()
                .map(PackImageDto::getSrc)
                .collect(Collectors.toSet());
        pack.setPackImages(images);

        return pack;
    }
}