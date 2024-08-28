package com.wordpress.shopBuilder.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordpress.shopBuilder.config.JwtUtil;
import com.wordpress.shopBuilder.dto.BackgroundImageDto;
import com.wordpress.shopBuilder.dto.PackImageDto;
import com.wordpress.shopBuilder.dto.ProductImageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MediaService {

    private final WebClient webClient;

    @Autowired
    public MediaService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public List<ProductImageDto> uploadImages(MultipartFile[] files, String wpToken) throws IOException {
        List<ProductImageDto> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            // Decode the JWT to get the domain name
            JsonNode decodedJwt = JwtUtil.decodeJwt(wpToken);
            String domaineName = decodedJwt.get("iss").asText(); // Extract the "iss" field


            String url = domaineName + "/wp-json/wp/v2/media";

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
        // Decode the JWT to get the domain name
        JsonNode decodedJwt = JwtUtil.decodeJwt(wpToken);
        String domaineName = decodedJwt.get("iss").asText(); // Extract the "iss" field


        String url = domaineName + "/wp-json/wp/v2/media";

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

    public List<PackImageDto> uploadImages2(MultipartFile[] files, String wpToken) throws IOException {
        List<PackImageDto> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            // Decode the JWT to get the domain name
            JsonNode decodedJwt = JwtUtil.decodeJwt(wpToken);
            String domaineName = decodedJwt.get("iss").asText(); // Extract the "iss" field


            String url = domaineName + "/wp-json/wp/v2/media";

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

            imageUrls.add(new PackImageDto(imageUrl));
        }
        System.out.println(imageUrls);

        return imageUrls;
    }

    public List<BackgroundImageDto> uploadImages3(MultipartFile[] files, String wpToken) throws IOException {
        List<BackgroundImageDto> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            // Decode the JWT to get the domain name
            JsonNode decodedJwt = JwtUtil.decodeJwt(wpToken);
            String domaineName = decodedJwt.get("iss").asText(); // Extract the "iss" field


            System.out.println("service");
            String url = domaineName + "/wp-json/wp/v2/media";

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

            imageUrls.add(new BackgroundImageDto(imageUrl));
        }
        System.out.println(imageUrls);

        return imageUrls;
    }

}
