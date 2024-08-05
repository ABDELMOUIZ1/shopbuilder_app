package com.wordpress.shopBuilder.service;

import com.wordpress.shopBuilder.dto.WebsiteContentDto;
import com.wordpress.shopBuilder.model.WebsiteContent;
import com.wordpress.shopBuilder.repository.WebsiteContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WebsiteContentService {

    private final WebClient webClient;

    @Autowired
    private WebsiteContentRepository websiteContentRepository;

    @Autowired
    public WebsiteContentService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost").build();
    }

    public ResponseEntity<String> updateWebsite(WebsiteContentDto websiteContentDto, String wpToken) {
        String url = "/wordpress/wp-json/wp/v2/settings";

        Mono<String> response = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + wpToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(websiteContentDto), WebsiteContentDto.class)
                .retrieve()
                .bodyToMono(String.class);

        // Save to repository asynchronously
        response.subscribe(resp -> {
            WebsiteContent websiteContent = new WebsiteContent();
            websiteContent.setWebSiteName(websiteContentDto.getTitle());
            websiteContent.setLogo(websiteContentDto.getSite_logo());
            System.out.println(websiteContent);
            websiteContentRepository.save(websiteContent);
        });

        // Block to get the response synchronously
        return ResponseEntity.ok(response.block());
    }
}
