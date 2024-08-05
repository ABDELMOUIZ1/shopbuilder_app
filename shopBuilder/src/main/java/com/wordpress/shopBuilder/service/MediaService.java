package com.wordpress.shopBuilder.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordpress.shopBuilder.dto.ProductImageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MediaService {

    private final WebClient webClient;

    @Autowired
    public MediaService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost").build();
    }

    public List<ProductImageDto> uploadImages(MultipartFile[] files, String wpToken) throws IOException {
        List<ProductImageDto> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            String url = "/wordpress/wp-json/wp/v2/media";

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            Mono<String> responseMono = webClient.post()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + wpToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class);

            String response = responseMono.block();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response);
            String imageUrl = jsonResponse.path("guid").path("rendered").asText();

            imageUrls.add(new ProductImageDto(imageUrl));
        }
        System.out.println(imageUrls);

        return imageUrls;
    }

    public Integer uploadImage(MultipartFile file, String wpToken) throws IOException {
        String url = "/wordpress/wp-json/wp/v2/media";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        Mono<String> responseMono = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + wpToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);

        String response = responseMono.block();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response);
        String imageId = jsonResponse.path("id").asText();

        return Integer.valueOf(imageId);
    }
}
